package gae_store_space;

import com.google.common.collect.ImmutableList;
import com.googlecode.objectify.annotation.Entity;
import pipeline.estimators.AdvImportanceProcessor;
import pipeline.estimators.ImportanceProcessor;

import java.util.*;

// TODO: Переименовать. Вообще хранятся не слова, а, например, стемы.
@Entity
public class NGramKind {
	public static final Integer MAX_CONTENT_ITEMS_IN_PACK = 2;
	
	// FIXME: inject it?
	private final ImportanceProcessor estimator = new AdvImportanceProcessor();

  @Deprecated
	private final Set<String> sources;
	
	//@Deprecated
  //@Id
  //Long id;

  // TODO: может хранится стем или пара-тройка слов.
  private String nGram;

  // TODO: возможно лучше хранить логарифм от нормированной частоты
  // Сколько раз встретилось слово.
  private Integer rawFrequency = 0;  // это и есть важность, но пока это частота  
  
  private Integer importance = 0;
  
  // May be make final
  private Set<SentenceKind> sentences = new HashSet<>();

  public String getNGram() {
  	return nGram;
  }
  
  public String pack() {
  	//ArrayList<String> tmp = new ArrayList<String>(sources);
  	//Collections.shuffle(tmp);
    //return nGram;
  	return nGram;//Joiner.on(" / ").join(tmp);
  }

  public Integer getImportance() {
    return importance;
  }
  
  public void calcImportance() {
  	importance = estimator.process(rawFrequency, sentences);
  }

  // wrong but need
  public void setImportance(Integer value) {
    importance = value;
  }

  public static NGramKind create(
  		String ngramValue, 
  		Collection<SentenceKind> sentences, 
  		int rawFrequency,
  		Set<String> s) {
    return new NGramKind(ngramValue, sentences, rawFrequency, s);
  }

  public ImmutableList<SentenceKind> getContendKinds() {
  	// берем часть
  	// FIXME: делать выборки с перемешиванием 	
  	ArrayList<SentenceKind> tmp = new ArrayList<>(sentences);
  	
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

  public NGramKind(
  		String nGram, 
  		Collection<SentenceKind> sentences,
  		int rawFrequency,
  		Set<String> sources) {
    this.nGram = nGram;
    
    // Частоту берем из списка ссылок.
    this.rawFrequency = rawFrequency;
    
    this.sources = sources;

    // FIXME: Ссылки должны быть уникальными. Но уникальны ли они тут?
    this.sentences.addAll(sentences);
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
