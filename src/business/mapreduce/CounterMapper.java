package business.mapreduce;

import business.nlp.SentenceTokenizer;
import store_gae_stuff.ContentItemKind;
import org.checkthread.annotations.NotThreadSafe;

import java.util.List;

/**
 https://hadoop.apache.org/docs/r1.2.1/mapred_tutorial.html

 Fake MapReduce - try make similar
 */
@NotThreadSafe
public class CounterMapper {
  private final CountReducer reducer_;
  public CounterMapper(CountReducer reducer) {
    reducer_ = reducer;
  }

  private void emit(String key, ContentItemKind value) {
    reducer_.reduce(key, value);
  }

  public void map(List<ContentItemKind> contentItemKinds) {
    SentenceTokenizer tokenizer = new SentenceTokenizer();
    for (ContentItemKind item : contentItemKinds) {
      List<String> words = tokenizer.getWords(item.getSentence());
      for (String word: words)
        // TODO: Сделать все буквы маленькими. Здесь?
        emit(word, item);
    }
  }
}
