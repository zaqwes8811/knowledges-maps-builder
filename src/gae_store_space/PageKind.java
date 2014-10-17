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

import servlets.protocols.WordDataValue;
import net.jcip.annotations.NotThreadSafe;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Load;

import core.math.DistributionElement;

@NotThreadSafe
@Entity
public class PageKind {
  private PageKind() { }

  @Id Long id;

  @Index String name;

  // Формированием не управляет, но остальным управляет.
  private List<Key<WordKind>> wordKeys = new ArrayList<Key<WordKind>>();
  private List<Key<SentenceKind>> contentItems = new ArrayList<Key<SentenceKind>>();
  
  public List<Key<WordKind>> getWordKeys() {
  	return this.wordKeys;
  }
  
  // FIXME: почему отношение не работает?
  // Попытка сделать так чтобы g не стал нулевым указателем
  @Load  // все равно может упасть
  Key<GeneratorKind> g;  // FIXME: вообще это проблема!!
  
  public String getName() { return name; }

  // throws: 
  //   IllegalStateException - генератор не найден. Система замкнута, если 
  //     по имение не нашли генератора - это нарушение консистентности. Имена генереторов
  //     вводится только при создании, потом они только читаются.
  public GeneratorKind getGenerator(String name) {  
  	if (g == null) {
  		throw new IllegalStateException();
  	}
  	
  	GeneratorKind gen = ofy().load().key(g).now();
  	
  	if (gen == null)
  		throw new IllegalStateException();
  	
  	gen.reset();
  	
  	return gen;
  }
  
  public List<String> getGenNames() {
  	GeneratorKind g = getGenerator(null);
  	List<String> r = new ArrayList<String>();
  	r.add(g.name); 	
  	return r;
  }
  
  // Пока поиска по словам нет, удаляем по позиции
  public void markDone(Integer pos) {
  	throw new UnsupportedOperationException();
  }

  public void setGenerator(GeneratorKind gen) {
    g = Key.create(gen);
  }

  public PageKind(String name, ArrayList<SentenceKind> items, ArrayList<WordKind> words) {
    this.name = Optional.of(name).get();
    for (WordKind word: words) this.wordKeys.add(Key.create(word));
    for (SentenceKind item: items) this.contentItems.add(Key.create(item));
  }

  // About: Возвращать частоты, сортированные по убыванию.
  public ArrayList<DistributionElement> getRawDistribution() {
    // TODO: Отосортировать при выборке если можно
    // TODO: может при запросе можно отсортировать?
    List<WordKind> wordKinds = ofy().load().type(WordKind.class).filterKey("in", this.wordKeys).list();

    // Сортируем - элементы могут прийти в случайном порядке
    Collections.sort(wordKinds, WordKind.createFrequencyComparator());
    Collections.reverse(wordKinds);

    // Form result
    ArrayList<DistributionElement> distribution = new ArrayList<DistributionElement>();
    for (WordKind word : wordKinds)
      distribution.add(new DistributionElement(word.getRawFrequency()));

    return distribution;
  }
   
  public Optional<WordDataValue> getWordData(String genName) {
  	GeneratorKind go = getGenerator(genName);
    
		Integer pointPosition = go.getPosition();
		WordKind wordKind =  getWordKind(pointPosition);
		ImmutableList<SentenceKind> contentKinds = wordKind.getContendKinds();

		ArrayList<String> content = new ArrayList<String>(); 
		for (SentenceKind e: contentKinds)
		  content.add(e.getSentence());
		
		return Optional.of(new WordDataValue(wordKind.getWord(), content, pointPosition));
  }
  
  // FIXME: а логика разрешает Отсутствующее значение?
  // http://stackoverflow.com/questions/2758224/assertion-in-java
  // генераторы могут быть разными, но набор слов один.
  private WordKind getWordKind(Integer pos) {
  	if (!(pos < this.wordKeys.size()))
  		throw new IllegalArgumentException();
  	
  	List<WordKind> kinds = 
				ofy().load().type(WordKind.class)
		    .filterKey("in", wordKeys).filter("pointPos =", pos)
		    .list();
  	
  	// FIXME: over pro. It's illegal state checking - не должно быть такого
  	//  Исключения лепить не хочется, но хотя исключения для искл ситуаций.
  	// Optional затрудняет поиск ситуаций - низкое разрешение по типам ошибок.
  	//
  	// It's IO - DbC wrong here.
  	if (kinds == null)
  		throw new IllegalStateException();
  	
  	// не прошли не свои страницы
  	if (kinds.size() != 1) 
  		throw new IllegalStateException();
  	
		return kinds.get(0);
  }
}
