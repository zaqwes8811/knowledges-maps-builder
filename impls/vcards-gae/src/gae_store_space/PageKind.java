// Tasks:
//   Сперва подключить кеш,
//   Затем думать о распределении
//   Затем думать об удалении.

// http://www.oracle.com/technetwork/java/javase/documentation/index-137868.html#styleguide
// TODO: http://www.oracle.com/technetwork/articles/marx-jpa-087268.html
// TODO: скрыть персистентность в этом классе, пусть сам себя сохраняет и удаляет.
// TODO: Функция очистки данных связанных со страницей, себя не удаляет.
// TODO: Добавить оценки текста
// не хочется выносить ofy()... выше. Но может быть, если использовать класс пользователя, то он может.
/**
 * About:
 *   Отражает один элемент данный пользователя, например, один файл субтитров.
 */

package gae_store_space;


import gae_store_space.queries.GAESpecific;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import net.jcip.annotations.NotThreadSafe;

import org.apache.commons.collections4.Predicate;
import org.javatuples.Pair;

import pipeline.TextPipeline;
import pipeline.math.DistributionElement;
import servlets.protocols.PathValue;
import servlets.protocols.WordDataValue;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Ignore;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Load;

import cross_cuttings_layer.AssertException;
import cross_cuttings_layer.OwnCollections;


@NotThreadSafe
@Entity
public class PageKind {
  private PageKind() { }

  public @Id Long id;
  
  public Long getId() { return id; }

  @Index String name;
  
  String rawSource;  // для обновленной версии

  // Формированием не управляет, но остальным управляет.
  // Обязательно отсортировано
  @Ignore 
  private ArrayList<NGramKind> unigramKinds = new ArrayList<NGramKind>();
  
  // Хранить строго как в исходном контексте 
  @Ignore 
  private ArrayList<SentenceKind> sentencesKinds = new ArrayList<SentenceKind>();

  // Теперь страница полностью управляет временем жизни
  // FIXME: почему отношение не работает?
  // Попытка сделать так чтобы g не стал нулевым указателем
  // все равно может упасть. с единичным ключем фигня какая-то
  // FIXME: вообще это проблема
  @Load  
  private Key<GeneratorKind> generator = null;
  
  private Optional<Key<GeneratorKind>> accessGen() {
  	return Optional.fromNullable(generator);
  }
  
  // по столько будем шагать
  // По малу шагать плохо тем что распределение может снова стать равном.
  // 10 * 20 = 200 слов, почти уникальных, лучше меньше
  @Ignore
	private static final Integer STEP_WINDOW_SIZE = 8;  
  @Ignore
  private static final Double SWITCH_THRESHOLD = 0.2;
  
  private Integer boundaryPtr = STEP_WINDOW_SIZE;  // указатель на текущyю границу
  private Integer etalonVolume = 0;
  
  @Ignore
  GAESpecific gae = new GAESpecific();
  
  //@Ignore
  private static TextPipeline buildPipeline() {
    return new TextPipeline();
  }
  
  public String getName() { 
  	return name; 
  }
  
  public ArrayList<Integer> getLengthsSentences() {
  	ArrayList<Integer> r = new ArrayList<Integer>();
  	for (SentenceKind k : sentencesKinds)
  		r.add(k.getCountWords());
  	return r;
  }
   
  // FIXME: если появится пользователи, то одного имени будет мало
  public static Optional<PageKind> syncRestore(String pageName) {
  	Optional<PageKind> page = new GAESpecific().getPageWaitConvergence(pageName);
  	
  	if (page.isPresent()) {
	    String rawSource = page.get().rawSource;
	    
	    // обрабатываем
	    PageKind tmpPage = buildPipeline().pass(page.get().name, rawSource);
	    
	    // теперь нужно запустить процесс обработки,
	    page.get().assign(tmpPage);
    }
    
    return page;  // 1 item
  }
  
  private void assign(PageKind rhs) {
  	unigramKinds = rhs.unigramKinds;
  	sentencesKinds = rhs.sentencesKinds;
  }
 
  public List<String> getGenNames() {
  	return gae.getGenNames(accessGen().get());
  }

  private void setGenerator(GeneratorKind gen) {
  	Key<GeneratorKind> k = Key.create(gen);
    generator = k;
  }

  // Это при создании с нуля
  public PageKind(
  		String name, ArrayList<SentenceKind> items, ArrayList<NGramKind> words, String rawSource) 
  	{
    this.name = name;
   	this.unigramKinds = words;
   	this.sentencesKinds = items;    
    this.rawSource = rawSource;
  }

  // FIXME: вот эту операцию лучше синхронизировать. И пользователю высветить, что идет процесс
	//   Иначе будут гонки. А может быть есть транзации на GAE?
	public static PageKind syncCreatePageIfNotExist(String name, String text) {
		// FIXME: add user info
		List<PageKind> pages = new GAESpecific().getPagesMaybeOutdated(name);
		
		if (pages.isEmpty()) {
			TextPipeline processor = new TextPipeline();
	  	PageKind page = processor.pass(name, text);  
	  	
	  	GeneratorKind g = GeneratorKind.create(page.buildSourceImportanceDistribution(), TextPipeline.defaultGenName);
	  	g.syncCreateInStore();
	  	
	  	page.setGenerator(g);
	  	
	  	new GAESpecific().syncCreateInStore(page);
			return page;
		} else {
			throw new IllegalArgumentException();
		}
	}
  
  private Set<String> getNGramms(Integer boundary) {
  	Integer end = sentencesKinds.size();
  	
  	if (sentencesKinds.size() > boundary)
  		end = boundary;
  	
  	// [.., end)
  	ArrayList<SentenceKind> kinds = new ArrayList<SentenceKind>(sentencesKinds.subList(0, end));
  	
  	return buildPipeline().getNGrams(kinds);
  }
  
  private static class Tmp implements Predicate<NGramKind> {
		@Override
		public boolean evaluate(NGramKind o) {
			return o.getValue().equals(ngram);
		}
		
		String ngram;
		public Tmp(String value) {
			ngram = value;
		}
	};
  
  private Integer getUnigramIndex(String ngram) {
  	Tmp p = new Tmp(ngram);
  	
  	Pair<NGramKind, Integer> k = OwnCollections.find(unigramKinds, p);
  	if (k.getValue1().equals(-1))
  		throw new IllegalStateException();
  	
  	Integer r = k.getValue1();
  	checkAccessIndex(r);
  	
  	return r;
  }
  
  private void checkAccessIndex(Integer idx) {
  	if (!(idx < unigramKinds.size())) {
  		throw new IllegalArgumentException();
  	}
  }
  
  public ArrayList<DistributionElement> getDistribution() {
  	GeneratorKind gen = getGenerator().get();
  	ArrayList<DistributionElement> r = gen.getCurrentDistribution();
  	checkDistributionInvariant(r);
  	return r;
  }
  
  private void checkDistributionInvariant(ArrayList<DistributionElement> d) {
  	if (d.size() != unigramKinds.size())
  		throw new IllegalStateException();
  }
  
  // About: Возвращать частоты, сортированные по убыванию.
  // Это должен быть getter
  // Все известные слова обнуляет!!
  public ArrayList<DistributionElement> buildSourceImportanceDistribution() {
    // Сортируем - элементы могут прийти в случайном порядке
    Collections.sort(unigramKinds, NGramKind.createImportanceComparator());
    Collections.reverse(unigramKinds);

    // Form result
    ArrayList<DistributionElement> r = new ArrayList<DistributionElement>();
    for (NGramKind word : unigramKinds)
      r.add(new DistributionElement(word.getImportance()));
    
    r = applyBoundary(r);
    
    // генератора еще нету   		
    return r;
  }
  
  private ArrayList<DistributionElement> applyBoundary(ArrayList<DistributionElement> d) {
  	checkDistributionInvariant(d);
  	
  	// Get word befor boundary
    Set<String> ngramms = getNGramms(boundaryPtr);
    
    for (String ngram: ngramms) {
    	Integer index = getUnigramIndex(ngram);
    	checkAccessIndex(index);
    	
    	// Проверка! Тестов как таковых нет, так что пока так
    	if (!unigramKinds.get(index).getValue().equals(ngram))
    		throw new AssertException();
    	
    	d.get(index).markInBoundary();
    }
    return d;
  }
   
  public Optional<WordDataValue> getWordData() {
  	GeneratorKind go = getGenerator().get();  // FIXME: нужно нормально обработать
    
		Integer pointPosition = go.getPosition();
		NGramKind ngramKind =  getNGram(pointPosition);
		String value = ngramKind.getValue();
		ImmutableList<SentenceKind> sentenceKinds = ngramKind.getContendKinds();

		ArrayList<String> content = new ArrayList<String>(); 
		for (SentenceKind k: sentenceKinds)
		  content.add(k.getSentence());
		
		WordDataValue r = new WordDataValue(value, content, pointPosition);
		
		r.setImportance(ngramKind.getImportance());
		
		return Optional.of(r);
  }
  
  private Optional<GeneratorKind> getGenerator() {
  	Optional<GeneratorKind> r = Optional.absent();
  	if (accessGen().isPresent()) {
  		r = gae.asyncGetGenerator(accessGen().get());
  	}
  	return r;
  }
   
  // FIXME: а логика разрешает Отсутствующее значение?
  // http://stackoverflow.com/questions/2758224/assertion-in-java
  // генераторы могут быть разными, но набор слов один.
  private NGramKind getNGram(Integer pos) {
  	checkAccessIndex(pos);
		return unigramKinds.get(pos);
  }
  
  private void IncBoundary() {
  	boundaryPtr += STEP_WINDOW_SIZE;
  	if (boundaryPtr > sentencesKinds.size())
  		boundaryPtr = sentencesKinds.size();
  }
  
  private Integer getCurrentVolume() {
  	Integer r = getGenerator().get().getActiveVolume();
  	/*CrossIO.print("know; Among = " + r + "; et = " + this.etalonVolume+ "; boundary = " + this.boundaryPtr);*/
  	return r;
  }
  
  private void setVolume(Integer val) {
  	etalonVolume = val;
  }
  
  private void setDistribution(ArrayList<DistributionElement> d) {
  	checkDistributionInvariant(d);
  	getGenerator().get().reloadGenerator(d);
  }
   
  private void moveBoundary() {
  	Integer currentVolume = getCurrentVolume();
  	
  	if (currentVolume < SWITCH_THRESHOLD * etalonVolume) {
  		// FIXME: no exception safe
  		// перезагружаем генератор
  		Integer currBoundary = boundaryPtr;
  		IncBoundary(); 
  		
  		if (getCurrentVolume() < 2) 
  			IncBoundary();  // пока один раз
  		
  		// подошли к концу
  		if (!currBoundary.equals(boundaryPtr)) {
  			ArrayList<DistributionElement> d = getDistribution();
  			d = applyBoundary(d);
  			
  			setDistribution(d);
  			setVolume(getCurrentVolume());
  		}
  	}
  }
  
  public void disablePoint(PathValue p) {
  	// лучше здесь
  	if (etalonVolume.equals(0)) {
     	if (getCurrentVolume() < 2)
     		throw new IllegalStateException();
     	
   		// создаем первый объем
   		setVolume(getCurrentVolume());
   		gae.asyncPersist(this);
  	}
  	
  	moveBoundary();

  	GeneratorKind g = getGenerator().get();
  	
  	checkAccessIndex(p.pointPos);
  	
		g.disablePoint(p.pointPos);
		
		// Если накопили все в пределах границы сделано, то нужно сдвинуть границу и перегрузить генератор.
		gae.asyncPersist(this);
		gae.asyncPersist(g);
  }

  
  public void asyncDeleteFromStore() { 	
  	if (accessGen().isPresent())
  		gae.asyncDeleteGenerators(accessGen().get());
  	
		gae.asyncDeletePage(this);
  }
}
