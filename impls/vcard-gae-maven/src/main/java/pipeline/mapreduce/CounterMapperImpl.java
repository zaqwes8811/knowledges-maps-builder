package pipeline.mapreduce;


import gae_store_space.SentenceKind;

import java.util.List;

import org.checkthread.annotations.NotThreadSafe;

import pipeline.nlp.SentenceTokenizer;

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
   
  @Override
  public void map(List<SentenceKind> contentItemKinds) {
    SentenceTokenizer tokenizer = new SentenceTokenizer();
    for (SentenceKind item : contentItemKinds) {
      List<String> tokens = tokenizer.getWords(item.getSentence());
      for (String token: tokens) {
        Word w = Word.build(token);
        emit(w.stem, item);
      }
      
      // Устанавливаем длину предложения
      item.setCountWords(tokens.size());
    }
  }
}
