package pipeline;

import gae_store_space.NGramKind;
import gae_store_space.PageKind;
import gae_store_space.SentenceKind;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import pipeline.nlp.PlainTextTokenizer;
import pipeline.statistics_collectors.StatisticCollector;
import pipeline.text_extractors.Converter;
import pipeline.text_extractors.SubtitlesToPlainText;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;

public class TextPipeline {
  private Converter converter = new SubtitlesToPlainText();
	private PlainTextTokenizer tokenizer = new PlainTextTokenizer();
	private StatisticCollector statisticCollector = new StatisticCollector();
	
  private String removeFormatting(String rawText) {
  	return converter.convert(rawText);
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

  private ArrayList<NGramKind> unpackHisto(
  		Multimap<String, SentenceKind> histo,
  		Multimap<String, String> sourcesHist) {
  	ArrayList<NGramKind> kinds = new ArrayList<NGramKind>();
  	
    for (String stem: histo.keySet()) {
    	Set<String> s = new HashSet<String>(sourcesHist.get(stem));
    	
      Collection<SentenceKind> content = histo.get(stem);
      int rawFrequency = content.size();
      NGramKind kind = NGramKind.create(stem, content, rawFrequency, s);
      
      kinds.add(kind);
    }
    return kinds;
  }
  
  private ArrayList<NGramKind> calcImportances(ArrayList<NGramKind> kinds) {
  	for (NGramKind k: kinds)
  		k.calcImportance();
  	return kinds;
  }
  
  public Set<String> getNGrams(ArrayList<SentenceKind> kinds) {
  	Multimap<String, SentenceKind> histo = statisticCollector.buildNGramHisto(kinds);
  	return histo.keySet();
  }
  
  // Now no store operations
  // Performance:
  //   text size around 1 Mb calc near 16 sec. - 4/2 1.6 GHz
  public PageKind pass(String pageName, String rawText) {
  	String pureText = removeFormatting(rawText);
  	
  	ImmutableList<String> sentences = tokenizer.getSentences(pureText);
  	
  	// Не очень логично, но важно соединить слова с контекстом, так что на обработку
  	//  передаем не чисто предложения, а недообработанные
  	ArrayList<SentenceKind> sentencesKinds = packSentences(sentences);
  	
    // Assemble statistic
    Multimap<String, SentenceKind> histo = statisticCollector.buildNGramHisto(sentencesKinds);
    Multimap<String, String> sources = statisticCollector.buildStemSourceHisto(sentencesKinds);

    ArrayList<NGramKind> nGramKinds = unpackHisto(histo, sources);
    
    nGramKinds = calcImportances(nGramKinds);

    // Sort words by frequency
    nGramKinds = sortByImportance(nGramKinds);

    return new PageKind(pageName, sentencesKinds, nGramKinds, rawText);
  }
}
