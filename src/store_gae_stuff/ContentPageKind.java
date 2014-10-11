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

import core.math.DistributionElement;

import com.google.common.base.Optional;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Load;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.jcip.annotations.NotThreadSafe;
import static store_gae_stuff.OfyService.ofy;

@NotThreadSafe
@Entity
public class ContentPageKind {
  private ContentPageKind() { }

  @Id Long id;

  @Index String name;

  // Формированием не управляет, но остальным управляет.
  List<Key<WordItemKind>> words = new ArrayList<Key<WordItemKind>>();
  List<Key<ContentItemKind>> contentItems = new ArrayList<Key<ContentItemKind>>();
  
  Key<ActiveDistributionGenKind> g;  // FIXME: почему отношение не работает?

  public ActiveDistributionGenKind getGenerator() {
    ActiveDistributionGenKind gens = ofy().load().type(ActiveDistributionGenKind.class)
    			.id(g.getId()).now();
    return gens; 
  }

  public void setGenerator(ActiveDistributionGenKind gen) {
    g = Key.create(gen);
  }

  public ContentPageKind(String name, ArrayList<ContentItemKind> items, ArrayList<WordItemKind> words) {
    this.name = Optional.of(name).get();
    for (WordItemKind word: words) this.words.add(Key.create(word));
    for (ContentItemKind item: items) this.contentItems.add(Key.create(item));
  }

  // About: Возвращать частоты, сортированные по убыванию.
  public ArrayList<DistributionElement> getRawDistribution() {
    // TODO: Отосортировать при выборке если можно
    // TODO: может при запросе можно отсортировать?
    List<WordItemKind> wordKinds = ofy().load().type(WordItemKind.class).filterKey("in", this.words).list();

    // Сортируем - элементы могут прийти в случайном порядке
    Collections.sort(wordKinds, WordItemKind.createFrequencyComparator());
    Collections.reverse(wordKinds);

    // Формируем результат
    ArrayList<DistributionElement> distribution = new ArrayList<DistributionElement>();
    for (WordItemKind word : wordKinds)
      distribution.add(new DistributionElement(word.getRawFrequency()));

    return distribution;
  }
}
