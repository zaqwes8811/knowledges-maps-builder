package dal_gae_kinds;
// Tasks:
//   Сперва подключить кеш,
//   Затем думать о распределении
//   Затем думать об удалении.

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
  // In storage
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

  // Own
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
  public ImmutableList<GeneratorDistributions.DistributionElement> getStatistic() {
    List<Word> words = ofy().load().type(Word.class)
      .filterKey("in", this.words).list();

    // TODO: Отосортировать при выборке если можно
    Collections.sort(words, Word.createFreqComparator());
    Collections.reverse(words);

    // Формируем результат
    ArrayList<GeneratorDistributions.DistributionElement> distribution =
      new ArrayList<GeneratorDistributions.DistributionElement>();
    for (Word word : words) {
      // нужны частоты для распределения
      // TODO: true -> enabled
      GeneratorDistributions.DistributionElement elem =
        new GeneratorDistributions.DistributionElement(word.getFrequency(), true);
      distribution.add(elem);
    }

    return ImmutableList.copyOf(distribution);
  }

  public ContentPage(String name, List<ContentItem> items, List<Word> words) {
    this.name = name;
    this.setWords(words);
    this.setItems(items);
  }

  // TODO: Функция очистки данных связанных со страницей, себя не удаляет.

  public ImmutableList<GeneratorDistributions.DistributionElement> disableWord(Integer idx) {
    // TODO: Проверка границ - это явно ошибка

    // TODO: Похоже нужна non-XG - транзакция. Кажется может возникнуть исключение.
    return null;
  }

  public ImmutableList<GeneratorDistributions.DistributionElement> enableWord(Integer idx) {
    return null;
  }

  private void setItems(List<ContentItem> list) {
    for (ContentItem item: list) items.add(Key.create(item));
  }

  public List<Key<ContentItem>> getItems() {
    return items;
  }

  private void setWords(List<Word> words) {
    for (Word word: words) this.words.add(Key.create(word));
  }
}
