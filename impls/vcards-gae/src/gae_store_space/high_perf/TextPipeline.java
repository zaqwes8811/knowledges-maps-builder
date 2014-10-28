package gae_store_space.high_perf;

import gae_store_space.PageKind;
import gae_store_space.SentenceKind;
import gae_store_space.WordKind;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;

import core.mapreduce.CountReducerImpl;
import core.mapreduce.CounterMapperImpl;
import core.nlp.PlainTextTokenizer;
import core.text_extractors.SubtitlesToPlainText;

public class TextPipeline {
	public static final String defaultPageName = "Korra";
	public static final String defaultGenName = "Default";
	public static final String defaultUserId = "User";
	
	private SubtitlesToPlainText convertor = new SubtitlesToPlainText();
	//private CountReducer reducer = new CountReducer(wordHistogramSink);
  //private CounterMapper mapper = new CounterMapper(reducer);
	private PlainTextTokenizer tokenizer = new PlainTextTokenizer();
	
  private String removeFormatting(String rawText) {
  	return convertor.convert(rawText);  	
  }

  private ArrayList<SentenceKind> getContentElements(ImmutableList<String> sentences) {
    ArrayList<SentenceKind> contentElements = new ArrayList<SentenceKind>();
    for (String sentence: sentences)
      contentElements.add(new SentenceKind(sentence));
    return contentElements;
  }
  
  // Now no store operations
  public PageKind pass(String name, String text) {
  	// Remove formatting
  	String pureText = removeFormatting(text);
  	
  	// Tokenise
  	ImmutableList<String> sentences = tokenizer.getSentences(pureText);
  	
  	// FIXME: убрать отсюда весь доступ к хранилищу
  	ArrayList<SentenceKind> contentElements = getContentElements(sentences);
  	
    // TODO: BAD! В страницу собрана обработка
    Multimap<String, SentenceKind> wordHistogramSink = HashMultimap.create();
    
    CountReducerImpl reducer = new CountReducerImpl(wordHistogramSink);
    CounterMapperImpl mapper = new CounterMapperImpl(reducer);

    // Split
    mapper.map(contentElements);

    ArrayList<WordKind.WordValue> value = new ArrayList<WordKind.WordValue>();
    for (String word: wordHistogramSink.keySet()) {
      Collection<SentenceKind> content = wordHistogramSink.get(word);
      int rawFrequency = content.size();
      value.add(new WordKind.WordValue(word, rawFrequency, content));
    }

    // Sort words by frequency and assign idx
    Collections.sort(value, new WordKind.WordValueFrequencyComparator());
    Collections.reverse(value);

    // Элементы отсортированы и это важно
    ArrayList<WordKind> words = new ArrayList<WordKind>();
    for (int i = 0; i < value.size(); i++) {
      WordKind.WordValue v = value.get(i);
      words.add(WordKind.create(v.word, v.sentences, v.frequency));
      words.get(i).setPointPos(i);
    }

    // Слова сортированы
    return new PageKind(name, contentElements, words, text);
  }
}
