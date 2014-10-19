package gae_store_space;

import static gae_store_space.OfyService.ofy;

import com.google.common.collect.ImmutableList;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Ignore;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Load;

import java.util.*;

// TODO: Переименовать. Вообще хранятся не слова, а, например, стемы.
@Entity
public class WordKind {
	private WordKind() { }
	public static final Integer MAX_CONTENT_ITEMS_IN_PACK = 10;
	 
  @Override
  public String toString() {
    return "("+word+", fr: "+ rawFrequency.toString()+", pos: "+ pointPos.toString()+")";
  }

  @Id
  Long id;

  // TODO: может хранится стем или пара-тройка слов.
  //@Index  // пока не нему не ищем
  private String word;

  // TODO: возможно лучше хранить логарифм от нормированной частоты
  // Сколько раз встретилось слово.
  @Index private Integer rawFrequency;  

  // TODO: Как откешировать? Какой допустимый период между запросами?
  // https://developers.google.com/appengine/pricing
  // Вроде бы нет ограничения между запросами.
  //@Load private Set<Key<SentenceKind>> sentences = new HashSet<Key<SentenceKind>>();
  //
  // May be make final
  @Ignore private Set<SentenceKind> sentences = new HashSet<SentenceKind>();

  // Можно и не сортировать, можно при выборке получать отсорт., но это доп. время.
  // Нужно для генератора распределения
  // 0-N в порядке возрастания по rawFrequency
  // По нему будет делаться выборка
  @Index
  private Integer pointPos;
  
  public String getWord() {
  	return word;
  }

  public Integer getRawFrequency() {
    return rawFrequency;
  }

  public static WordKind create(
  		String wordValue, Collection<SentenceKind> sentencess, int rawFrequency) 
  	{
    return new WordKind(wordValue, sentencess, rawFrequency);
  }

  public void setPointPos(Integer value) {
    pointPos = value;
  }

  public ImmutableList<SentenceKind> getContendKinds() {
  	// берем часть
  	// FIXME: делать выборки с перемешиванием
  	return ImmutableList.copyOf(sentences);
  }

  // TODO: Хорошо бы сохранять их, а не просто слова. Почитать Effective java.
  // Идея на этапе mapreduce передавать в качестве ключей объекты слов, и пользоваться
  // ими как ключами. Но слова имеют ссылки, то сразу большие проблемы с эквивалетностью!
  // На этапе формирования индекса может все не так просто, но потом метод equals будет работать очень странно.
  // TODO: Stop it!
  // equals()
  // hashCode() - need it?
  public WordKind(String word, Collection<SentenceKind> sentencess, int rawFrequency) {
    this.word = word;
    pointPos = -1;

    // Частоту берем из списка ссылок.
    this.rawFrequency = rawFrequency;

    // FIXME: Ссылки должны быть уникальными. Но уникальны ли они тут?
    sentences.addAll(sentencess);
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
    public final Collection<SentenceKind> sentences;

    public WordValue(String word, Integer frequency, Collection<SentenceKind> c) {
      this.word = word;
      this.frequency = frequency;
      this.sentences = c;
    }
  }

  public static class WordValueFrequencyComparator implements Comparator<WordValue> {
    @Override
    public int compare(WordValue o1, WordValue o2) {
      return o1.frequency.compareTo(o2.frequency);
    }
  }
}
