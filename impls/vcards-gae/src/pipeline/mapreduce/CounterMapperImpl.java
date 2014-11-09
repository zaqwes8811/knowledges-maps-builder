package pipeline.mapreduce;


import org.checkthread.annotations.NotThreadSafe;
import org.tartarus.snowball.ext.englishStemmer;

import pipeline.nlp.SentenceTokenizer;

import gae_store_space.SentenceKind;

import java.util.List;

/**
 https://hadoop.apache.org/docs/r1.2.1/mapred_tutorial.html

 Fake MapReduce - try make similar
 */

@NotThreadSafe
public class CounterMapperImpl implements CounterMapper {
  private final CountReducer<SentenceKind> reducer_;
  public CounterMapperImpl(CountReducer<SentenceKind> reducer) {
    reducer_ = reducer;
  }

  private void emit(String key, SentenceKind value) {
    reducer_.reduce(key, value);
  }
  
  englishStemmer stemmer = new englishStemmer();
  
  private Word getWord(String value) {
  	String lowWord = value.toLowerCase();
  	stemmer.setCurrent(lowWord);
    if (stemmer.stem()) {
      return new Word(stemmer.getCurrent(), value);  
    } else {
      return new Word(value, value); 
    }
  }
  
  @Override
  public void map(List<SentenceKind> contentItemKinds) {
    SentenceTokenizer tokenizer = new SentenceTokenizer();
    for (SentenceKind item : contentItemKinds) {
      List<String> tokens = tokenizer.getWords(item.getSentence());
      for (String token: tokens) {
        Word w = getWord(token);
        emit(w.stem, item);
      }
      
      // Устанавливаем длину предложения
      item.setCountWords(tokens.size());
    }
  }
}
