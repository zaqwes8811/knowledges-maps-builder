package pipeline;

import gae_store_space.PageKind;
import gae_store_space.SentenceKind;
import gae_store_space.NGramKind;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import pipeline.mapreduce.CountReducer;
import pipeline.mapreduce.CountReducerImpl;
import pipeline.mapreduce.CounterMapper;
import pipeline.mapreduce.CounterMapperImpl;
import pipeline.nlp.PlainTextTokenizer;
import pipeline.statistics_collectors.StatisticCollector;
import pipeline.text_extractors.Convertor;
import pipeline.text_extractors.SubtitlesToPlainText;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;




public class TextPipeline {
	public static final String defaultPageName = "Korra";
	public static final String defaultGenName = "Default";
	public static final String defaultUserId = "User";
	
	private Convertor convertor = new SubtitlesToPlainText();
	private PlainTextTokenizer tokenizer = new PlainTextTokenizer();
	private StatisticCollector statisticCollector = new StatisticCollector();
	
  private String removeFormatting(String rawText) {
  	return convertor.convert(rawText);  	
  }

  private ArrayList<SentenceKind> packSentences(ImmutableList<String> sentences) {
    ArrayList<SentenceKind> r = new ArrayList<SentenceKind>();
    for (String sentence: sentences)
      r.add(new SentenceKind(sentence));
    return r;
  }
  
  private ArrayList<NGramKind> sortByImportance(ArrayList<NGramKind> kinds) {
  	Collections.sort(kinds, NGramKind.createImportanceComparator());
    Collections.reverse(kinds);
    return kinds;
  }

  private ArrayList<NGramKind> unpackHisto(Multimap<String, SentenceKind> histo) {
  	ArrayList<NGramKind> kinds = new ArrayList<NGramKind>();
    for (String word: histo.keySet()) {
      Collection<SentenceKind> content = histo.get(word);
      int rawFrequency = content.size();
      kinds.add(NGramKind.create(word, content, rawFrequency));
    }
    return kinds;
  }
  
  private ArrayList<NGramKind> calcImportancies(ArrayList<NGramKind> kinds) {
  	for (NGramKind k: kinds)
  		k.calcImportance();
  	return kinds;
  }
  
  // Now no store operations
  public PageKind pass(String name, String text) {
  	String pureText = removeFormatting(text);
  	
  	ImmutableList<String> sentences = tokenizer.getSentences(pureText);
  	
  	ArrayList<SentenceKind> items = packSentences(sentences);
  	
    // Assemble statistic
    Multimap<String, SentenceKind> histo = statisticCollector.buildHisto(items);

    ArrayList<NGramKind> ngramKinds = unpackHisto(histo);
    
    ngramKinds = calcImportancies(ngramKinds);

    // Sort words by frequency
    ngramKinds = sortByImportance(ngramKinds);

    return new PageKind(name, items, ngramKinds, text);
  }
}
