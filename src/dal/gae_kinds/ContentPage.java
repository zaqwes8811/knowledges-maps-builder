package dal.gae_kinds;

import business.mapreduce.CountReducer;
import business.mapreduce.CounterMapper;
import com.google.common.collect.HashMultimap;
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

  @Index String name;

  // TODO: troubles. Может добавить метод выкалывания точек?
  DistributionGenBuilder genBuilder;  // Распределение нужно менять.
  GeneratorAnyDistributionImpl gen;

  private ContentPage() {}

  private static ContentPage createFromComponents(String name,
                                                  List<ContentItem> list,
                                                  List<Word> words,
                                                  DistributionGenBuilder builder) {
    ContentPage page = new ContentPage(name, builder);
    page.setWords(words);
    page.setItems(list);

    return page;
  }

  public static ContentPage create(String name,
                                   List<ContentItem> contentItems,
                                   DistributionGenBuilder builder) {

    Multimap<String, ContentItem> wordHistogram = HashMultimap.create();
    CountReducer reducer = new CountReducer(wordHistogram);
    CounterMapper mapper = new CounterMapper(reducer);

    // Split
    mapper.map(contentItems);  // TODO: implicit, but be so

    // WARNING: Порядок важен!
    // Persist content items
    ofy().save().entities(contentItems).now();

    // Persist words
    List<Word> words = new ArrayList<Word>();
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

    // Заряжаем генератор
    //GeneratorAnyDistributionImpl gen = GeneratorAnyDistributionImpl.create(distribution);

    // Last - Persist page

    //ofy().save().entity(page).now();  // Persist externally
    return ContentPage.createFromComponents(name, contentItems, words, builder);
  }

  private boolean isEmpty() {
    return false;
  }


  private ContentPage(String name, DistributionGenBuilder builder) {
    this.name = name;
    //genBuilder = builder;
  }

  // TODO: Удяляет на что ссылается из хранилища.
  public void empty() {

  }

  // TODO: Возможно нужно довосстановит состояние объекта после чтения из storage.
  public void reanimatePageAfterLoad() {

  }

  // Content items
  @Load
  List<Key<ContentItem>> items = new ArrayList<Key<ContentItem>>();

  private void setItems(List<ContentItem> list) {
    for (ContentItem item: list) {
      items.add(Key.create(item));
    }
  }

  public List<Key<ContentItem>> getItems() { return items; }

  // Words
  @Load
  List<Key<Word>> words = new ArrayList<Key<Word>>();

  private void setWords(List<Word> words) {
    for (Word word: words) {
      this.words.add(Key.create(word));
    }
  }
}
