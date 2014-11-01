package gae_store_space;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Ignore;
import com.googlecode.objectify.annotation.Index;

// TODO: Переименовать. Вообще хранятся не слова, а, например, стемы.
@Entity
public class NGramKind {
	private NGramKind() { }
	public static final Integer MAX_CONTENT_ITEMS_IN_PACK = 5;
	 
  @Override
  public String toString() {
    return "("+nGram+", fr: "+ rawFrequency.toString()+", pos: "+ importancePosition.toString()+")";
  }

  @Id
  Long id;

  // TODO: может хранится стем или пара-тройка слов.
  private String nGram;

  // TODO: возможно лучше хранить логарифм от нормированной частоты
  // Сколько раз встретилось слово.
  @Index private Integer rawFrequency;  // это и есть важность, но пока это частота  
  
  // Можно и не сортировать, можно при выборке получать отсорт., но это доп. время.
  // Нужно для генератора распределения
  // 0-N в порядке возрастания по rawFrequency
  // По нему будет делаться выборка
  @Index
  private Integer importancePosition;
  @Ignore Integer importance;  // важно
  
  // TODO: Как откешировать? Какой допустимый период между запросами?
  // https://developers.google.com/appengine/pricing
  // Вроде бы нет ограничения между запросами.
  //@Load private Set<Key<SentenceKind>> sentences = new HashSet<Key<SentenceKind>>();
  //
  // May be make final
  @Ignore private Set<SentenceKind> sentences = new HashSet<SentenceKind>();

  
  
  public String getWord() {
  	return nGram;
  }

  public Integer getRawFrequency() {
    return rawFrequency;
  }

  public static NGramKind create(
  		String wordValue, Collection<SentenceKind> sentencess, int rawFrequency) 
  	{
    return new NGramKind(wordValue, sentencess, rawFrequency);
  }

  public void setPointPos(Integer value) {
    importancePosition = value;
  }

  public ImmutableList<SentenceKind> getContendKinds() {
  	// берем часть
  	// FIXME: делать выборки с перемешиванием 	
  	ArrayList<SentenceKind> tmp = new ArrayList<SentenceKind>(sentences);
  	
  	long seed = System.nanoTime();
  	Collections.shuffle(tmp, new Random(seed));
  	
  	if (tmp.isEmpty())
  		throw new IllegalStateException();
  	
  	Integer toIndex = MAX_CONTENT_ITEMS_IN_PACK;
  	if (tmp.size() < MAX_CONTENT_ITEMS_IN_PACK)
  		toIndex = tmp.size();
  	
  	if (tmp.isEmpty())
  		toIndex = 0;
  	
  	return ImmutableList.copyOf(tmp.subList(0, toIndex));
  }

  // TODO: Хорошо бы сохранять их, а не просто слова. Почитать Effective java.
  // Идея на этапе mapreduce передавать в качестве ключей объекты слов, и пользоваться
  // ими как ключами. Но слова имеют ссылки, то сразу большие проблемы с эквивалетностью!
  // На этапе формирования индекса может все не так просто, но потом метод equals будет работать очень странно.
  // TODO: Stop it!
  // equals()
  // hashCode() - need it?
  public NGramKind(String word, Collection<SentenceKind> sentencess, int rawFrequency) {
    this.nGram = word;
    importancePosition = -1;

    // Частоту берем из списка ссылок.
    this.rawFrequency = rawFrequency;

    // FIXME: Ссылки должны быть уникальными. Но уникальны ли они тут?
    sentences.addAll(sentencess);
  }

  private static class ImportanceComparator implements Comparator<NGramKind> {
    // http://stackoverflow.com/questions/10017381/compareto-method-java
    //
    // In "Effective Java"
    @Override
    public int compare(NGramKind o1, NGramKind o2) {
      return o1.getRawFrequency().compareTo(o2.getRawFrequency());
    }
  }

  public static Comparator<NGramKind> createFrequencyComparator() {
    return new ImportanceComparator();
  }
}
