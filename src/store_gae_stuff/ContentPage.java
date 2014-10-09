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

package store_gae_stuff;

import com.google.appengine.repackaged.org.apache.http.annotation.NotThreadSafe;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Load;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static store_gae_stuff.OfyService.ofy;

@NotThreadSafe
@Entity
public class ContentPage {
  @Id
  Long id;

  @Index
  String name;
  // Формированием не управляет, но остальным управляет.
  @Load
  List<Key<WordItem>> words = new ArrayList<Key<WordItem>>();

  @Load
  List<Key<ContentItem>> items = new ArrayList<Key<ContentItem>>();

  private ContentPage() { }

  // TODO: Как быть с полиморфизмом?
  @Load
  List<Key<Distributions>> distributions = new ArrayList<Key<Distributions>>();

  // Странице никчему знать про детали интерфейса генераторов
  // TODO: как быть с аргументами?
  // https://code.google.com/p/cofoja/wiki/HowtoWriteGoodContracts
  public ContentPage(String name, List<ContentItem> items, List<WordItem> words) {
    this.name = Optional.of(name).get();
    for (final WordItem word: words) this.words.add(Key.create(word));
    for (final ContentItem item: items) this.items.add(Key.create(item));
  }

  // About: Возвращать частоты, сортированные по убыванию.
  public ImmutableList<Integer> getRawDistribution() {
    // TODO: Отосортировать при выборке если можно
    // TODO: может при запросе можно отсортировать?
    List<WordItem> wordItems = ofy().load().type(WordItem.class).filterKey("in", this.words).list();

    // Сортируем - элементы могут прийти в случайном порядке
    Collections.sort(wordItems, WordItem.createFrequencyComparator());
    Collections.reverse(wordItems);

    // Формируем результат
    ArrayList<Integer> distribution = new ArrayList<Integer>();
    for (final WordItem word : wordItems)
      distribution.add(word.getRawFrequency());

    return ImmutableList.copyOf(distribution);
  }

  public List<Key<ContentItem>> getItems() {
    return items;
  }
}
