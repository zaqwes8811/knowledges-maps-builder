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

import static gae_store_space.queries.OfyService.ofy;

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
import com.googlecode.objectify.VoidWork;
import com.googlecode.objectify.Work;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Ignore;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Load;

import cross_cuttings_layer.AssertException;
import cross_cuttings_layer.OwnCollections;

//@ItIsAggregate
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
  private Key<GeneratorKind> generator;
   
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
  private static int COUNT_REPEATS = 3;
  
  @Ignore
  GAESpecific store = new GAESpecific();
  
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
   
  // Транзакцией сделать нельзя - поиск это сразу больше 5 EG
  // Да кажется можно, просто не ясно зачем
  public static Optional<PageKind> syncRestore(final String pageName) { 	
  	Optional<PageKind> page = new GAESpecific().restorePageByName_evCons(pageName);
  	
  	if (page.isPresent()) {
	    String rawSource = page.get().rawSource;
	    
	    // обрабатываем
	    PageKind tmpPage = buildPipeline().pass(page.get().name, rawSource);
	    
	    // теперь нужно запустить процесс обработки,
	    page.get().assign(tmpPage);
	    page.get().restoreGenerator();
    }
    
    return page;  // 1 item
  }
  
  private void assign(PageKind rhs) {
  	unigramKinds = rhs.unigramKinds;
  	sentencesKinds = rhs.sentencesKinds;
  }
 
  public List<String> getGenNames() {
  	ArrayList<String> r = new ArrayList<String>();
  	r.add(TextPipeline.defaultGenName);
  	return r;
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
	public static PageKind createPageIfNotExist_strongCons_maybe(final String name, String text) {
		// local work
		final GAESpecific store = new GAESpecific();
		TextPipeline processor = new TextPipeline();
  	final PageKind page = processor.pass(name, text); 
  	final GeneratorKind g = GeneratorKind.create(page.buildSourceImportanceDistribution());

  	// transaction boundary
  	PageKind r = ofy().transactNew(COUNT_REPEATS, new Work<PageKind>() {
	    public PageKind run() {
	    	ofy().save().entity(g).now();
	    	
	    	// нельзя не сохраненны присоединять - поэтому нельзя восп. сущ. методом
	    	page.setGenerator(g);  
	 
	    	ofy().save().entity(page).now();
	      return page;
	    }
		});
  	
  	// FIXME: база данный в каком состоянии будет тут? согласованном?
  	// https://cloud.google.com/appengine/docs/java/datastore/transactions - Isolation
  	List<PageKind> pages = store.getPagesByName_evCons(name);	    			

  	if (pages.size() > 1) 
			throw new IllegalArgumentException();	
  	
		return r;
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
  	GeneratorKind gen = restoreGenerator().get();
  	ArrayList<DistributionElement> r = gen.getCurrentDistribution();
  	checkDistributionInvariant(r);
  	return r;
  }
  
  private void checkDistributionInvariant(ArrayList<DistributionElement> d) {
  	if (d.size() != unigramKinds.size())
  		throw new IllegalStateException();
  }
  
  // About: Возвращать пустое распределение
  private ArrayList<DistributionElement> buildSourceImportanceDistribution() {
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
  	GeneratorKind go = restoreGenerator().get();  // FIXME: нужно нормально обработать
    
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
  
  private Optional<GeneratorKind> restoreGenerator() {
  	Optional<GeneratorKind> r = Optional.absent();
  	r = store.restoreGenerator_evCons(generator);
		if (r.isPresent())
			r.get().reload();
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
  	Integer r = restoreGenerator().get().getActiveCount();
  	/*CrossIO.print("know; Among = " + r + "; et = " + this.etalonVolume+ "; boundary = " + this.boundaryPtr);*/
  	return r;
  }
  
  private void setVolume(Integer val) {
  	etalonVolume = val;
  }
  
  private void setDistribution(ArrayList<DistributionElement> d) {
  	checkDistributionInvariant(d);
  	restoreGenerator().get().reloadGenerator(d);
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
  	Integer pos = p.pointPos;
  	// лучше здесь
  	if (etalonVolume.equals(0)) {
     	if (getCurrentVolume() < 2)
     		throw new IllegalStateException();
     	
   		// создаем первый объем
   		setVolume(getCurrentVolume());
  	}
  	
  	moveBoundary();

  	GeneratorKind g = restoreGenerator().get();
  	
  	checkAccessIndex(pos);
  	
		g.disablePoint(pos);
		
		// Если накопили все в пределах границы сделано, то нужно сдвинуть границу и перегрузить генератор.
		persist();
  }

  private void persist() {
  	final PageKind p = this;
  	ofy().transact(new VoidWork() {
	    public void vrun() {
	    	ofy().save().entity(p.restoreGenerator().get()).now();
	    	ofy().save().entity(p).now();
	    }
		});
  }
  
  public void asyncDeleteFromStore() { 
  	final PageKind p = this;
  	ofy().transact(new VoidWork() {
	    public void vrun() {
	    	ofy().delete().key(generator).now();
	    	ofy().delete().entity(p).now();
	    }
		});
  }
}
