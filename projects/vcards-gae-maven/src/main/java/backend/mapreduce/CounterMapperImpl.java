package backend.mapreduce;


import backend.ContentItem;

import java.util.List;

import net.jcip.annotations.NotThreadSafe;

import backend.nlp.SentenceTokenizer;

/**
 https://hadoop.apache.org/docs/r1.2.1/mapred_tutorial.html

 Fake MapReduce - try make similar
 */

@NotThreadSafe
public class CounterMapperImpl implements CounterMapper {
  private final CountReducer<ContentItem> reducer_;
  public CounterMapperImpl(CountReducer<ContentItem> reducer) {
    reducer_ = reducer;
  }

  private void emit(String key, ContentItem value) {
    reducer_.reduce(key, value);
  }
   
  @Override
  public void map(List<ContentItem> contentItemKinds) {
    SentenceTokenizer tokenizer = new SentenceTokenizer();
    for (ContentItem item : contentItemKinds) {
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
