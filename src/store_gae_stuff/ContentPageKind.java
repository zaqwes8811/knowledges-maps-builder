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

import business.math.DistributionElement;
import com.google.appengine.repackaged.org.apache.http.annotation.NotThreadSafe;
import com.google.common.base.Optional;
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
public class ContentPageKind {
  @Id
  Long id;

  @Index
  String name;
  // Формированием не управляет, но остальным управляет.
  @Load
  List<Key<WordItemKind>> words = new ArrayList<Key<WordItemKind>>();

  @Load
  List<Key<ContentItemKind>> items = new ArrayList<Key<ContentItemKind>>();

  private ContentPageKind() { }

  // TODO: Как быть с полиморфизмом? Не будет работать
  //@Load
  //List<Key<DistributionGen>> distributions = new ArrayList<Key<DistributionGen>>();

  // Странице никчему знать про детали интерфейса генераторов
  // TODO: как быть с аргументами?
  // https://code.google.com/p/cofoja/wiki/HowtoWriteGoodContracts
  public ContentPageKind(String name, List<ContentItemKind> items, List<WordItemKind> words) {
    this.name = Optional.of(name).get();
    for (final WordItemKind word: words) this.words.add(Key.create(word));
    for (final ContentItemKind item: items) this.items.add(Key.create(item));
  }

  // About: Возвращать частоты, сортированные по убыванию.
  public ArrayList<DistributionElement> getRawDistribution() {
    // TODO: Отосортировать при выборке если можно
    // TODO: может при запросе можно отсортировать?
    List<WordItemKind> wordItemKinds = ofy().load().type(WordItemKind.class).filterKey("in", this.words).list();

    // Сортируем - элементы могут прийти в случайном порядке
    Collections.sort(wordItemKinds, WordItemKind.createFrequencyComparator());
    Collections.reverse(wordItemKinds);

    // Формируем результат
    ArrayList<DistributionElement> distribution = new ArrayList<DistributionElement>();
    for (final WordItemKind word : wordItemKinds)
      distribution.add(new DistributionElement(word.getRawFrequency()));

    return distribution;
  }

  public List<Key<ContentItemKind>> getItems() {
    return items;
  }
}
