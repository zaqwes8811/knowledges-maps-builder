package pipeline.mapreduce;

import gae_store_space.SentenceKind;

import java.util.List;

import pipeline.nlp.SentenceTokenizer;

public class SourceMapper implements CounterMapper {
	private final CountReducer<String> reducer_;
  public SourceMapper(CountReducer<String> reducer) {
    reducer_ = reducer;
  }

  private void emit(String key, String value) {
    reducer_.reduce(key, value);
  }
   
  @Override
  public void map(List<SentenceKind> contentItemKinds) {
    SentenceTokenizer tokenizer = new SentenceTokenizer();
    for (SentenceKind item : contentItemKinds) {
      List<String> tokens = tokenizer.getWords(item.getSentence());
      for (String token: tokens) {
        Word w = Word.build(token);
        emit(w.stem, w.source);
      }
      
      // Устанавливаем длину предложения
      item.setCountWords(tokens.size());
    }
  }
}
