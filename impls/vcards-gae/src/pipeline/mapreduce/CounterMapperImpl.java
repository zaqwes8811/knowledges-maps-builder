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
  private final CountReducer reducer_;
  public CounterMapperImpl(CountReducer reducer) {
    reducer_ = reducer;
  }

  private void emit(String key, SentenceKind value) {
    reducer_.reduce(key, value);
  }
  
  englishStemmer stemmer = new englishStemmer();
  
  @Override
  public void map(List<SentenceKind> contentItemKinds) {
    SentenceTokenizer tokenizer = new SentenceTokenizer();
    for (SentenceKind item : contentItemKinds) {
      List<String> words = tokenizer.getWords(item.getSentence());
      for (String word: words) {
      	String lowWord = word.toLowerCase();
      	stemmer.setCurrent(lowWord);
        if (stemmer.stem()){
            emit(stemmer.getCurrent(), item);
        } else {
        	// FIXME: нужна компрессия. Пока что все перевел в нижний регистр.
        	// .toLowerCase()
        	// FIXME: возможно фильтрация - но думаю в другом классе
          emit(word, item);
        }
      }
      
      // Устанавливаем длину предложения
      item.setCountWords(words.size());
    }
  }
}
