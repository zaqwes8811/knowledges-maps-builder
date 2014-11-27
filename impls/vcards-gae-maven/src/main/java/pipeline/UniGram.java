package pipeline;

import com.google.common.collect.ImmutableList;
import gae_store_space.SentenceKind;
import pipeline.estimators.AdvImportanceProcessor;
import pipeline.estimators.ImportanceProcessor;

import java.util.*;

// TODO: Переименовать. Вообще хранятся не слова, а, например, стемы.
// Хранить их точно не буду - съест лимиты
public class Unigram {
	public static final Integer MAX_CONTENT_ITEMS_IN_PACK = 3;
	
	// FIXME: inject it?
	private final ImportanceProcessor estimator = new AdvImportanceProcessor();

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

  public static Unigram create(
  		String ngramValue, 
  		Collection<SentenceKind> sentences, 
  		int rawFrequency) {
    return new Unigram(ngramValue, sentences, rawFrequency);
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

  public Unigram(
    String nGram,
    Collection<SentenceKind> sentences,
    int rawFrequency) {
    this.nGram = nGram;
    
    // Частоту берем из списка ссылок.
    this.rawFrequency = rawFrequency;

    // FIXME: Ссылки должны быть уникальными. Но уникальны ли они тут?
    this.sentences.addAll(sentences);
  }

  private static class ImportanceComparator implements Comparator<Unigram> {
    // http://stackoverflow.com/questions/10017381/compareto-method-java
    //
    // In "Effective Java"
    @Override
    public int compare(Unigram o1, Unigram o2) {
      return o1.getImportance().compareTo(o2.getImportance());
    }
  }

  public static Comparator<Unigram> createImportanceComparator() {
    return new ImportanceComparator();
  }
}
