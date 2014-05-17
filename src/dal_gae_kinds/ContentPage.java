package dal_gae_kinds;
// Tasks:
//   Сперва подключить кеш,
//   Затем думать о распределении
//   Затем думать об удалении.

import business.math.GeneratorAnyDistribution;
import business.math.GeneratorDistributions;
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
import java.util.concurrent.TimeUnit;

import static dal_gae_kinds.OfyService.ofy;

/**
 * About: Отражает один элемент данный пользователя, например, один файл субтитров.
 */
@NotThreadSafe
@Entity
public class ContentPage {
  /// Persist
  @Id
  Long id;
  @Index
  String name;
  // Формированием не управляет, но остальным управляет.
  @Load
  List<Key<Word>> words = new ArrayList<Key<Word>>();
  @Load
  List<Key<ContentItem>> items = new ArrayList<Key<ContentItem>>();

  // TODO: Notes!

  // TODO: Как быть с полиморфизмом?
  @Load
  List<Key<GeneratorDistributions>> distributions = new ArrayList<Key<GeneratorDistributions>>();

  /// Own
  // TODO: нужно сбрасывать запрещенные слова, чтобы грузились из хранилища. Хранить не нужно.
  // TODO: для кеша из Guava - invalidate
  @Unindex
  private final LoadingCache<Integer, Optional<Word>> wordsCache = CacheBuilder.newBuilder()
    .expireAfterAccess(10, TimeUnit.MINUTES)  // TODO: Make by size.
    .build(
      new CacheLoader<Integer, Optional<Word>>() {
        public Optional<Word> load(Integer key) { // no checked exception
          //
          return Optional.absent();//createExpensiveGraph(key);
        }
      });

  public Pair<Optional<Word>, Optional<ArrayList<ContentItem>>> get(Integer position) {
    /*try {
      return graphs.get(key);
    } catch (ExecutionException e) {
      throw new OtherException(e.getCause());
    }*/

    // TODO: Check position
    return Pair.with(Optional.<Word>absent(), Optional.<ArrayList<ContentItem>>absent());
  }

  // MUST BE!
  private ContentPage() { }

  // TODO: возможно нужен кеш. см. Guava cache.
  // TODO: возвращать только частоты.
  public ImmutableList<GeneratorAnyDistribution.DistributionElement> getSortedFrequencies() {
    List<Word> words = ofy().load().type(Word.class)
      .filterKey("in", this.words).list();

    // TODO: Отосортировать при выборке если можно
    Collections.sort(words, Word.createFreqComparator());
    Collections.reverse(words);

    // Формируем результат
    ArrayList<GeneratorAnyDistribution.DistributionElement> distribution =
      new ArrayList<GeneratorAnyDistribution.DistributionElement>();
    for (Word word : words) {
      // нужны частоты для распределения
      // TODO: true -> enabled - Нет! хранится это будет внутри распредления, а не в слове
      GeneratorAnyDistribution.DistributionElement elem =
        new GeneratorAnyDistribution.DistributionElement(word.getFrequency(), true);
      distribution.add(elem);
    }

    return ImmutableList.copyOf(distribution);
  }

  public ContentPage(String name, List<ContentItem> items, List<Word> words) {
    this.name = name;
    for (Word word: words) this.words.add(Key.create(word));
    for (ContentItem item: items) this.items.add(Key.create(item));
  }

  // TODO: Функция очистки данных связанных со страницей, себя не удаляет.

  public List<Key<ContentItem>> getItems() {
    return items;
  }
}
