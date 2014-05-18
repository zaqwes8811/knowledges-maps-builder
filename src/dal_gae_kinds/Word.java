package dal_gae_kinds;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Load;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

// TODO: Переименовать. Вообще хранятся не слова, а, например, стемы.
@Entity
public class Word {
  /// Persistent
  @Id Long id;

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
  @Index
  Integer sortedIdx;  // 0-N в порядке возрастания по rawFrequency

  private Word() {}

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

  public static Word create(String wordValue, Collection<ContentItem> items) {
    Word word = new Word(wordValue);

    // Частоту берем из списка ссылок.
    word.setRawFrequency(items.size());

    // Ссылки должны быть уникальными
    Set<ContentItem> itemSet = new HashSet<ContentItem>();
    itemSet.addAll(items);
    word.setContentItems(itemSet);
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
  public Word(String word) {
    this.word = word;
    sortedIdx = -1;
  }

  private static class WordFreqComparator implements Comparator<Word> {
    // http://stackoverflow.com/questions/10017381/compareto-method-java
    //
    // In "Effective Java"
    @Override
    public int compare(Word o1, Word o2) {
      return o1.getRawFrequency().compareTo(o2.getRawFrequency());
    }
  }

  public static Comparator<Word> createFreqComparator() {
    return new WordFreqComparator();
  }
}
