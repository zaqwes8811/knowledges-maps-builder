package dal.gae_kinds;

import business.mapreduce.CountReducer;
import business.mapreduce.CounterMapper;
import business.math.GeneratorAnyDistributionImpl;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Load;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static dal.gae_kinds.OfyService.ofy;

/**
 * About: Отражает один элемент данный пользователя, например, один файл субтитров.
 */
@Entity
public class ContentPage {
  @Id
  Long id;

  @Index
  String name;
  @Load
  List<Key<Word>> words = new ArrayList<Key<Word>>();
  @Load
  List<Key<ContentItem>> items = new ArrayList<Key<ContentItem>>();

  // TODO: troubles. Может добавить метод выкалывания точек?
  // TODO: Может лучше сделать ссылкой-ключом?
  GeneratorAnyDistributionImpl gen;  // TODO: Как быть с ним? Они логическое целое.

  /// Methods
  private ContentPage() { }

  public static ContentPage create(String name, ArrayList<ContentItem> contentItems) {
    Multimap<String, ContentItem> wordHistogram = HashMultimap.create();
    CountReducer reducer = new CountReducer(wordHistogram);
    CounterMapper mapper = new CounterMapper(reducer);

    // Split
    mapper.map(contentItems);  // TODO: implicit, but be so

    // WARNING: Порядок важен!
    // Persist content items
    ofy().save().entities(contentItems).now();

    // Persist words
    ArrayList<Word> words = new ArrayList<Word>();
    for (String word: wordHistogram.keySet()) {
      Collection<ContentItem> value = wordHistogram.get(word);
      Word wordObj = Word.create(word, value);
      words.add(wordObj);
    }

    // Sort words by frequency and assign idx
    Collections.sort(words, Word.createFreqComparator());
    Collections.reverse(words);
    ArrayList<Integer> distribution = new ArrayList<Integer>();
    for (int i = 0; i < words.size(); i++) {
      words.get(i).setSortedIdx(i);

      // нужны частоты для распределения
      distribution.add(words.get(i).getFrequency());
    }

    ofy().save().entities(words).now();
    return new ContentPage(name, contentItems, words);
  }

  private boolean isEmpty() {
    return false;
  }

  // TODO: возможно нужен кеш. см. Guava cache.
  public ImmutableList<GeneratorAnyDistributionImpl.DistributionElement> getDistribution() {
    return null;//ImmutableList.copyOf(new GeneratorAnyDistributionImpl.DistributionElement());
  }

  private ContentPage(String name, List<ContentItem> list, List<Word> words) {
    this.name = name;
    this.setWords(words);
    this.setItems(list);
  }

  // TODO: Удяляет на что ссылается из хранилища.
  public void empty() {

  }

  // TODO: Возможно нужно довосстановит состояние объекта после чтения из storage.
  public void reanimatePageAfterLoad() {

  }

  private void setItems(List<ContentItem> list) {
    for (ContentItem item: list) {
      items.add(Key.create(item));
    }
  }

  public List<Key<ContentItem>> getItems() {
    return items;
  }

  private void setWords(List<Word> words) {
    for (Word word: words) {
      this.words.add(Key.create(word));
    }
  }
}
