package store_gae_stuff;

import com.google.appengine.repackaged.org.apache.http.annotation.NotThreadSafe;
import com.google.common.base.Optional;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.*;
import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static store_gae_stuff.OfyService.ofy;

// Tasks:
//   Сперва подключить кеш,
//   Затем думать о распределении
//   Затем думать об удалении.

// http://www.oracle.com/technetwork/java/javase/documentation/index-137868.html#styleguide

/**
 * About:
 *   Отражает один элемент данный пользователя, например, один файл субтитров.
 */
@NotThreadSafe
@Entity
public class ContentPage {
  /// Persist
  // TODO: http://www.oracle.com/technetwork/articles/marx-jpa-087268.html
  // TODO: скрыть персистентность в этом классе, пусть сам себя сохраняет и удаляет.
  // не хочется выносить ofy()... выше. Но может быть, если использовать класс пользователя, то он может.
  @Id
  Long id;

  @Index
  String name;
  // Формированием не управляет, но остальным управляет.
  @Load
  List<Key<WordItem>> words = new ArrayList<Key<WordItem>>();

  @Load
  List<Key<ContentItem>> items = new ArrayList<Key<ContentItem>>();

  // MUST BE!
  private ContentPage() { }

  // TODO: Добавить оценки текста

  // TODO: Как быть с полиморфизмом?
  @Load
  List<Key<GeneratorDistributions>> distributions = new ArrayList<Key<GeneratorDistributions>>();

  /// Own
  // Странице никчему знать про детали интерфейса генераторов

  // TODO: как быть с аргументами?
  // https://code.google.com/p/cofoja/wiki/HowtoWriteGoodContracts
  //@Requires("name != null")  // пока без проверки, но возможно удобно - визуально видно
  public ContentPage(String name, List<ContentItem> items, List<WordItem> words) {
    if (name == null)
      throw new IllegalArgumentException("Broken precondition on building page.");

    this.name = name;
    for (final WordItem word: words) this.words.add(Key.create(word));
    for (final ContentItem item: items) this.items.add(Key.create(item));
  }

  @Unindex
  private final LoadingCache<Integer, Optional<WordItem>> wordsCache = CacheBuilder.newBuilder()
    .expireAfterAccess(10, TimeUnit.MINUTES)  // TODO: Make by size.
    .build(
      new CacheLoader<Integer, Optional<WordItem>>() {
        public Optional<WordItem> load(Integer key) { // TODO: no checked exception
          return Optional.absent();//createExpensiveGraph(key);
        }
      });

  public Pair<Optional<WordItem>, Optional<ArrayList<ContentItem>>> get(Integer position) {
    try {
      // TODO: Получение элементов контекста
      Optional<WordItem> word = wordsCache.get(position);

      return Pair.with(word, Optional.<ArrayList<ContentItem>>absent());
    } catch (ExecutionException e) {
      throw new RuntimeException(e.getCause());
    }
  }

  // About: Возвращать частоты, сортированные по убыванию.
  public ImmutableList<Integer> getSortedFrequencies() {
    // TODO: Отосортировать при выборке если можно
    List<WordItem> words = ofy().load().type(WordItem.class).filterKey("in", this.words).list();

    // Сортируем
    Collections.sort(words, WordItem.createFrequencyComparator());
    Collections.reverse(words);

    // Формируем результат
    ArrayList<Integer> distribution = new ArrayList<Integer>();
    for (final WordItem word : words) distribution.add(word.getRawFrequency());

    return ImmutableList.copyOf(distribution);
  }

  // TODO: Функция очистки данных связанных со страницей, себя не удаляет.

  public List<Key<ContentItem>> getItems() {
    return items;
  }
}
