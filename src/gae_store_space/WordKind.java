package gae_store_space;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Load;

import java.util.*;

// TODO: Переименовать. Вообще хранятся не слова, а, например, стемы.
@Entity
public class WordKind {
  @Override
  public String toString() {
    return "("+word+", fr: "+ rawFrequency.toString()+", pos: "+ pointPos.toString()+")";
  }

  @Id
  Long id;

  // TODO: может хранится стем или пара-тройка слов.
  //@Index  // пока не нему не ищем
  String word;

  // TODO: возможно лучше хранить логарифм от нормированной частоты
  @Index
  Integer rawFrequency;  // Сколько раз встретилось слово.

  // TODO: Как откешировать? Какой допустимый период между запросами?
  // https://developers.google.com/appengine/pricing
  // Вроде бы нет ограничения между запросами.
  @Load
  Set<Key<ContentItemKind>> items = new HashSet<Key<ContentItemKind>>();

  // Можно и не сортировать, можно при выборке получать отсорт., но это доп. время.
  // Нужно для генератора распределения
  // 0-N в порядке возрастания по rawFrequency
  // По нему будет делаться выборка
  @Index
  Integer pointPos;
  
  public String getWord() {
  	return word;
  }

  private WordKind() {}

  public void setRawFrequency(Integer value) {
    rawFrequency = value;
  }

  public Integer getRawFrequency() {
    return rawFrequency;
  }

  public static WordKind create(String wordValue, Collection<ContentItemKind> items, int rawFrequency) {
    WordKind word = new WordKind(wordValue);

    // Частоту берем из списка ссылок.
    word.setRawFrequency(rawFrequency);

    // Ссылки должны быть уникальными
    Set<ContentItemKind> itemSet = new HashSet<ContentItemKind>();
    itemSet.addAll(items);
    word.setContentItems(itemSet);
    return word;
  }

  public static WordKind create(String wordValue, int rawFrequency) {
    WordKind word = new WordKind(wordValue);

    // Частоту берем из списка ссылок.
    word.setRawFrequency(rawFrequency);
    return word;
  }

  public void setPointPos(Integer value) {
    pointPos = value;
  }

  // List coupled content contentItems.
  public void setContentItems(Set<ContentItemKind> item) {
    for (ContentItemKind value: item)
      items.add(Key.create(value));
  }

  public Set<Key<ContentItemKind>> getItems() {
    return items;
  }

  // TODO: Хорошо бы сохранять их, а не просто слова. Почитать Effective java.
  // Идея на этапе mapreduce передавать в качестве ключей объекты слов, и пользоваться
  // ими как ключами. Но слова имеют ссылки, то сразу большие проблемы с эквивалетностью!
  // На этапе формирования индекса может все не так просто, но потом метод equals будет работать очень странно.
  // TODO: Stop it!
  // equals()
  // hashCode() - need it?
  public WordKind(String word) {
    this.word = word;
    pointPos = -1;
  }

  private static class WordFreqComparator implements Comparator<WordKind> {
    // http://stackoverflow.com/questions/10017381/compareto-method-java
    //
    // In "Effective Java"
    @Override
    public int compare(WordKind o1, WordKind o2) {
      return o1.getRawFrequency().compareTo(o2.getRawFrequency());
    }
  }

  public static Comparator<WordKind> createFrequencyComparator() {
    return new WordFreqComparator();
  }

  public static class WordValue {
    public final Integer frequency;
    public final String word;
    public final Collection<ContentItemKind> items;

    public WordValue(String word, Integer frequency, Collection<ContentItemKind> c) {
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
