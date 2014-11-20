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


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static gae_store_space.OfyService.ofy;

import net.jcip.annotations.NotThreadSafe;

import org.apache.commons.collections4.Predicate;
import org.javatuples.Pair;

import pipeline.TextPipeline;
import pipeline.math.DistributionElement;
import web_relays.protocols.PathValue;
import web_relays.protocols.WordDataValue;

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

	// Assumption: raw source >> sum(other fields)
	public long getPageByteSize() {
		// it's trouble
		// http://stackoverflow.com/questions/9368764/calculate-size-of-object-in-java
		// http://stackoverflow.com/questions/52353/in-java-what-is-the-best-way-to-determine-the-size-of-an-object
		//
		return rawSource.length();
	}
  
  public Long getId() { return id; }

  @Index String name;
  
  String rawSource;  // для обновленной версии

  // Формированием не управляет, но остальным управляет.
  // Обязательно отсортировано
  @Ignore 
  private ArrayList<NGramKind> unigramKinds = new ArrayList<>();
  
  // Хранить строго как в исходном контексте 
  @Ignore 
  private ArrayList<SentenceKind> sentencesKinds = new ArrayList<SentenceKind>();

  // Теперь страница полностью управляет временем жизни
  // удобен разве что для запроса
  // Можно загружать по нему при загрузке страницы, а потом пользоваться кешем
  @Load  
  private Key<GeneratorKind> generator;  
  
  @Ignore
  private GeneratorKind generatorCache;
   
  // по столько будем шагать
  // По малу шагать плохо тем что распределение может снова стать равном.
  // 10 * 20 = 200 слов, почти уникальных, лучше меньше
  @Ignore
	private static final Integer STEP_WINDOW_SIZE = 8;  
  @Ignore
  private static final Double SWITCH_THRESHOLD = 0.2;
  
  private Integer boundaryPtr = STEP_WINDOW_SIZE;  // указатель на текущyю границу
  private Integer referenceVolume = 0;

  
  @Ignore
  GAEStoreAccessManager store = new GAEStoreAccessManager();
  
  //@Ignore
  private static TextPipeline buildPipeline() {
    return new TextPipeline();
  }
  
  public String getName() { 
  	return name; 
  }
  
  public ArrayList<Integer> getLengthsSentences() {
  	ArrayList<Integer> r = new ArrayList<>();
  	for (SentenceKind k : sentencesKinds)
  		r.add(k.getCountWords());
  	return r;
  }
   
  // Транзакцией сделать нельзя - поиск это сразу больше 5 EG
  // Да кажется можно, просто не ясно зачем
  public static Optional<PageKind> restore(final String pageName) {
  	GAEStoreAccessManager store = new GAEStoreAccessManager();
  	Optional<PageKind> page = store.restorePageByName_eventually(pageName);
  	
  	if (page.isPresent()) {
	    String rawSource = page.get().rawSource;
	    
	    PageKind tmpPage = buildPipeline().pass(page.get().name, rawSource);

	    page.get().assign(tmpPage);
	    // загружается только при восстановлении
	    page.get().generatorCache = store.restoreGenerator_eventually(page.get().generator).get();
    }
    return page;  // 1 item
  }
  
  private GeneratorKind getGeneratorCache() {
  	if (!Optional.fromNullable(generatorCache).isPresent())
  		throw new IllegalStateException();
  	return generatorCache;
  }
  
  private void assign(PageKind rhs) {
  	unigramKinds = rhs.unigramKinds;
  	sentencesKinds = rhs.sentencesKinds;
  }
 
  public List<String> getGenNames() {
  	ArrayList<String> r = new ArrayList<>();
  	r.add(TextPipeline.defaultGenName);
  	return r;
  }

  // Пришлось раскрыть
  private void setGenerator(GeneratorKind gen) {
		generator = Key.create(gen);
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
	public static PageKind createPageIfNotExist_eventually(final String name, String text) {
		if (text.length() > GAEStoreAccessManager.LIMIT_DATA_STORE_SIZE)
			throw new IllegalArgumentException();

		// local work
		final GAEStoreAccessManager store = new GAEStoreAccessManager();
		TextPipeline processor = new TextPipeline();
		final PageKind page = processor.pass(name, text);

		// FIXME: how to know object size - need todo it!
		if (page.getPageByteSize() > GAEStoreAccessManager.LIMIT_DATA_STORE_SIZE)
			throw new IllegalArgumentException();

		final GeneratorKind g = GeneratorKind.create(page.buildSourceImportanceDistribution());

		// check-then-act/read-modify-write
		// FIXME: Looks like imposable without races.
		// Doesn't help really
		{
			// https://cloud.google.com/appengine/articles/transaction_isolation - Isolation
			List<PageKind> pages = store.getPagesByName_eventually(name);
			if (pages.size() >= 1) {
				throw new IllegalArgumentException();
			}

			// transaction boundary
			Work<PageKind> work = new Work<PageKind>() {
				public PageKind run() {
					ofy().save().entity(g).now();

					// нельзя не сохраненны присоединять - поэтому нельзя восп. сущ. методом
					page.setGenerator(g);

					ofy().save().entity(page).now();
					return page;
				}
			};
			// FIXME: база данный в каком состоянии будет тут? согласованном?
			// check here, but what can do?

			return store.firstPersist(work);
		}
	}
  
  private Set<String> getNGramms(Integer boundary) {
  	Integer end = sentencesKinds.size();
  	
  	if (sentencesKinds.size() > boundary)
  		end = boundary;
  	
  	// [.., end)
  	ArrayList<SentenceKind> kinds = new ArrayList<>(sentencesKinds.subList(0, end));
  	
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
	}
  
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
  	GeneratorKind gen = getGeneratorCache();
  	ArrayList<DistributionElement> r = gen.getCurrentDistribution();
  	checkDistributionInvariant(r);
  	return r;
  }
  
  private void checkDistributionInvariant(ArrayList<DistributionElement> d) {
  	if (d.size() != unigramKinds.size())
  		throw new IllegalStateException(
				String.format("Distribution size = %d / Element count = %d"
					, d.size(), unigramKinds.size()));
  }
  
  // About: Возвращать пустое распределение
  private ArrayList<DistributionElement> buildSourceImportanceDistribution() {
    // Сортируем - элементы могут прийти в случайном порядке
    Collections.sort(unigramKinds, NGramKind.createImportanceComparator());
    Collections.reverse(unigramKinds);

    // Form result
    ArrayList<DistributionElement> r = new ArrayList<>();
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
  
  private Integer getMaxImportancy() {
  	return this.generatorCache.getMaxImportance();
  }
   
  public Optional<WordDataValue> getWordData() {
  	GeneratorKind go = getGeneratorCache();  // FIXME: нужно нормально обработать
    
		Integer pointPosition = go.getPosition();
		NGramKind ngramKind =  getNGram(pointPosition);
		String value = ngramKind.pack();
		ImmutableList<SentenceKind> sentenceKinds = ngramKind.getContendKinds();

		ArrayList<String> content = new ArrayList<>();
		for (SentenceKind k: sentenceKinds)
		  content.add(k.getSentence());
		
		WordDataValue r = new WordDataValue(value, content, pointPosition);
		
		// 
		r.setImportance(ngramKind.getImportance());
		// max import
		r.setMaxImportance(getMaxImportancy());
		
		return Optional.of(r);
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
  
  private Integer getAmongCurrentActivePoints() {
  	return getGeneratorCache().getActiveCount();
  }
  
  private void setVolume(Integer val) {
  	referenceVolume = val;
  }
  
  private void setDistribution(ArrayList<DistributionElement> d) {
  	checkDistributionInvariant(d);
  	getGeneratorCache().resetDistribution(d);
  }
   
  private void moveBoundary() {
  	Integer currentVolume = getAmongCurrentActivePoints();
  	
  	if (currentVolume < SWITCH_THRESHOLD * referenceVolume) {
  		// FIXME: no exception safe
  		// перезагружаем генератор
  		Integer currBoundary = boundaryPtr;
  		IncBoundary(); 
  		
  		if (getAmongCurrentActivePoints() < 2) 
  			IncBoundary();  // пока один раз
  		 
  		// подошли к концу
  		if (!currBoundary.equals(boundaryPtr)) {
  			ArrayList<DistributionElement> d = getDistribution();
  			d = applyBoundary(d);
  			
  			setDistribution(d);
  			setVolume(getAmongCurrentActivePoints());
  		}
  		
  		// state change
  		persist(); 
  	}
  }
  
  public void disablePoint(PathValue p) {
  	Integer pos = p.pointPos;
  	// лучше здесь
  	if (referenceVolume.equals(0)) {
     	if (getAmongCurrentActivePoints() < 2)
     		throw new IllegalStateException();
     	
   		// создаем первый объем
   		setVolume(getAmongCurrentActivePoints());
  	}
  	moveBoundary();

  	// Читаем заново
  	GeneratorKind g = getGeneratorCache();
  	
  	checkAccessIndex(pos);
  	
		g.disablePoint(pos);
		
		// Если накопили все в пределах границы сделано, то нужно сдвинуть границу и перегрузить генератор.
		persist();
  }

  private void persist() {
  	final PageKind p = this;
  	
  	// execution on dal - можно транслировать ошибку нижнего слоя
  	store.transact(new VoidWork() {
	    public void vrun() {
	    	ofy().save().entity(p.getGeneratorCache()).now();
	    	ofy().save().entity(p).now();
	    }
		});
  }
  
  public void deleteFromStore_strong() {
  	final PageKind p = this;
  	store.transact(new VoidWork() {
	    public void vrun() {
	    	ofy().delete().entity(p.getGeneratorCache()).now();
	    	ofy().delete().entity(p).now();
	    }
		});
  }
}
