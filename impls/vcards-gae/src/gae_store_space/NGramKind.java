package gae_store_space;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import pipeline.estimators.AdvImportanceProcessor;
import pipeline.estimators.ImportanceProcessor;
import pipeline.estimators.SimpleImportanceProcessor;

import com.google.common.collect.ImmutableList;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

// TODO: Переименовать. Вообще хранятся не слова, а, например, стемы.
@Entity
public class NGramKind {
	private NGramKind() { }
	public static final Integer MAX_CONTENT_ITEMS_IN_PACK = 5;
	
	// FIXME: inject it?
	private ImportanceProcessor estimator = new AdvImportanceProcessor();
	//SimpleImportanceProcessor();
	
  @Id
  Long id;

  // TODO: может хранится стем или пара-тройка слов.
  private String nGram;

  // TODO: возможно лучше хранить логарифм от нормированной частоты
  // Сколько раз встретилось слово.
  private Integer rawFrequency = 0;  // это и есть важность, но пока это частота  
  
  private Integer importance = 0;
  
  // May be make final
  private Set<SentenceKind> sentences = new HashSet<SentenceKind>();

  public String getValue() {
  	return nGram;
  }

  public Integer getImportance() {
    return importance;
  }
  
  public void calcImportance() {
  	importance = estimator.process(rawFrequency, sentences);
  }

  public static NGramKind create(String ngramValue, Collection<SentenceKind> sentences, int rawFrequency)	{
    return new NGramKind(ngramValue, sentences, rawFrequency);
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

  public NGramKind(String nGram, Collection<SentenceKind> sentencess, int rawFrequency) {
    this.nGram = nGram;
    
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
      return o1.getImportance().compareTo(o2.getImportance());
    }
  }

  public static Comparator<NGramKind> createImportanceComparator() {
    return new ImportanceComparator();
  }
}
