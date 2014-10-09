package store_gae_stuff;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Load;

import java.util.*;

// TODO: Переименовать. Вообще хранятся не слова, а, например, стемы.
@Entity
public class WordItem {
  /// Persistent
  @Id
  Long id;

  // TODO: может хранится стем или пара-тройка слов.
  @Index
  String word;

  // TODO: возможно лучше хранить логарифм от нормированной частоты
  @Index
  Integer rawFrequency;  // Сколько раз встретилось слово.

  // TODO: Как откешировать? Какой допустимый период между запросами?
  // https://developers.google.com/appengine/pricing
  // Вроде бы нет ограничения между запросами.
  @Load
  Set<Key<ContentItem>> items = new HashSet<Key<ContentItem>>();

  // Можно и не сортировать, можно при выборке получать отсорт., но это доп. время.
  // Нужно для генератора распределения
  // 0-N в порядке возрастания по rawFrequency
  // По нему будет делаться выборка
  @Index
  Integer sortedIdx;

  private WordItem() {}

  /// Own
  @Override
  public String toString() {
    return "("+word+", "+ rawFrequency.toString()+", "+sortedIdx.toString()+")";
  }

  public void setRawFrequency(Integer value) {
    rawFrequency = value;
  }

  public Integer getRawFrequency() {
    return rawFrequency;
  }

  public static WordItem create(String wordValue, Collection<ContentItem> items, int rawFrequency) {
    WordItem word = new WordItem(wordValue);

    // Частоту берем из списка ссылок.
    word.setRawFrequency(rawFrequency);

    // Ссылки должны быть уникальными
    Set<ContentItem> itemSet = new HashSet<ContentItem>();
    itemSet.addAll(items);
    word.setContentItems(itemSet);
    return word;
  }

  public static WordItem create(String wordValue, int rawFrequency) {
    WordItem word = new WordItem(wordValue);

    // Частоту берем из списка ссылок.
    word.setRawFrequency(rawFrequency);
    return word;
  }

  public void setSortedIdx(Integer value) {
    sortedIdx = value;
  }

  // List coupled content items.
  public void setContentItems(Set<ContentItem> item) {
    for (ContentItem value: item) {
      this.items.add(Key.create(value));
    }
  }

  public Set<Key<ContentItem>> getItems() {
    return items;
  }

  // TODO: Хорошо бы сохранять их, а не просто слова. Почитать Effective java.
  // Идея на этапе mapreduce передавать в качестве ключей объекты слов, и пользоваться
  // ими как ключами. Но слова имеют ссылки, то сразу большие проблемы с эквивалетностью!
  // На этапе формирования индекса может все не так просто, но потом метод equals будет работать очень странно.
  // TODO: Stop it!
  // equals()
  // hashCode() - need it?
  public WordItem(String word) {
    this.word = word;
    sortedIdx = -1;
  }

  private static class WordFreqComparator implements Comparator<WordItem> {
    // http://stackoverflow.com/questions/10017381/compareto-method-java
    //
    // In "Effective Java"
    @Override
    public int compare(WordItem o1, WordItem o2) {
      return o1.getRawFrequency().compareTo(o2.getRawFrequency());
    }
  }

  public static Comparator<WordItem> createFrequencyComparator() {
    return new WordFreqComparator();
  }

  public static class WordValue {
    final Integer frequency;
    final String word;
    final Collection<ContentItem> items;

    WordValue(String word, Integer frequency, Collection<ContentItem> c) {
      this.word = word;
      this.frequency = frequency;
      this.items = c;
    }
  }

  public static class WordValueFrequencyComparator implements Comparator<WordValue> {
    @Override
    public int compare(WordValue o1, WordValue o2) {
      return o1.frequency.compareTo(o2.frequency);
    }
  }
}
