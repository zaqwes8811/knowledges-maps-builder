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

import static gae_store_space.OfyService.ofy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.jcip.annotations.NotThreadSafe;
import pipeline.TextPipeline;
import pipeline.math.DistributionElement;
import servlets.protocols.WordDataValue;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Ignore;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Load;


@NotThreadSafe
@Entity
public class PageKind {
  private PageKind() { }

  public @Id Long id;

  @Index String name;
  
  String rawSource;  // для обновленной версии

  // Формированием не управляет, но остальным управляет.
  // Обязательно отсортировано
  @Ignore private ArrayList<NGramKind> unigramKinds = new ArrayList<NGramKind>();
  @Ignore private List<SentenceKind> sentencesKinds = new ArrayList<SentenceKind>();

  // FIXME: почему отношение не работает?
  // Попытка сделать так чтобы g не стал нулевым указателем
  // все равно может упасть. с единичным ключем фигня какая-то
  // FIXME: вообще это проблема
  @Load  
  private List<Key<GeneratorKind>> generators = new ArrayList<Key<GeneratorKind>>();  
  
  public String getName() { return name; }
  
  public void deleteGenerators() {
  	ofy().delete().keys(generators).now();
  }
  
  // FIXME: если появится пользователи, то одного имени будет мало
  public static Optional<PageKind> restore(String pageName) {
  	List<PageKind> pages = 
    		ofy().load().type(PageKind.class).filter("name = ", pageName).list();
    
  	int i = 0;
		while (true) {
			if (i > GAESpecific.COUNT_TRIES)
				throw new IllegalStateException();
			pages = ofy().load().type(PageKind.class).filter("name = ", pageName).list();
			
			if (pages.size() > 1 || pages.size() == 0) {
				try {
					Thread.sleep(GAESpecific.TIME_STEP_MS);
				} catch (InterruptedException e1) {
					throw new RuntimeException(e1);
				}
				i++;
				continue;
		  }
			break;
		}
		
    if (pages.size() == 0)
    	return Optional.absent();
    
    PageKind barePage = pages.get(0);
    
    String rawSource = barePage.rawSource;
    
    // обрабатываем
    TextPipeline pipeline = new TextPipeline();
    PageKind page = pipeline.pass(barePage.name, rawSource);
    
    // теперь нужно запустить процесс обработки,
    barePage.assign(page);
    
    return Optional.fromNullable(barePage);  // 1 item
  }
  
  private void assign(PageKind rhs) {
  	unigramKinds = rhs.unigramKinds;
  	sentencesKinds = rhs.sentencesKinds;
  }
  
  public void persist() {
  	ofy().save().entity(this).now();
  }

  // TODO: перенести бы в класс генератора, но!! это затрудняет выборку, т.к. имя не уникально 
  //
  // throws: 
  //   IllegalStateException - генератор не найден. Система замкнута, если 
  //     по имение не нашли генератора - это нарушение консистентности. Имена генереторов
  //     вводится только при создании, потом они только читаются.
  public Optional<GeneratorKind> getGenerator(String name) { 
  	if (name == null)
  		throw new IllegalArgumentException();
  	
  	if (generators.isEmpty())
  		throw new IllegalStateException();
  	
  	List<GeneratorKind> gen = 
  			ofy().load().type(GeneratorKind.class)
	  			.filterKey("in", generators)
	  			.filter("name = ", name)
	  			.list();

  	if (gen.isEmpty())
  		return Optional.absent();
  		
  	if (gen.size() > 1)
  		throw new IllegalStateException(name);
  	
  	gen.get(0).restore();
  	
  	return Optional.fromNullable(gen.get(0));
  }
  
  public List<String> getGenNames() {
  	List<GeneratorKind> gen = 
  			ofy().load().type(GeneratorKind.class)
	  			.filterKey("in", generators)
	  			.list();
  	
  	List<String> r = new ArrayList<String>();
  	for (GeneratorKind g: gen) {
  		r.add(g.getName()); 
  	}	
  	return r;
  }

  public void addGenerator(GeneratorKind gen) {
  	Key<GeneratorKind> k = Key.create(gen);
    generators.add(k);
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

  // About: Возвращать частоты, сортированные по убыванию.
  public ArrayList<DistributionElement> getRawDistribution() {
    // Сортируем - элементы могут прийти в случайном порядке
    Collections.sort(unigramKinds, NGramKind.createFrequencyComparator());
    Collections.reverse(unigramKinds);

    // Form result
    ArrayList<DistributionElement> distribution = new ArrayList<DistributionElement>();
    for (NGramKind word : unigramKinds)
      distribution.add(new DistributionElement(word.getRawFrequency()));

    return distribution;
  }
   
  public Optional<WordDataValue> getWordData(String genName) {
  	GeneratorKind go = getGenerator(genName).get();
    
		Integer pointPosition = go.getPosition();
		NGramKind wordKind =  getWordKind(pointPosition);
		String word = wordKind.getWord();
		ImmutableList<SentenceKind> contentKinds = wordKind.getContendKinds();

		ArrayList<String> content = new ArrayList<String>(); 
		for (SentenceKind e: contentKinds)
		  content.add(e.getSentence());
		
		return Optional.of(new WordDataValue(word, content, pointPosition));
  }
  
  // FIXME: а логика разрешает Отсутствующее значение?
  // http://stackoverflow.com/questions/2758224/assertion-in-java
  // генераторы могут быть разными, но набор слов один.
  private NGramKind getWordKind(Integer pos) {
  	if (! (pos < this.unigramKinds.size()))
  		throw new IllegalArgumentException();
  	
		return unigramKinds.get(pos);
  }
}
