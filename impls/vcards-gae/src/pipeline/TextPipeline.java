package pipeline;

import gae_store_space.NGramKind;
import gae_store_space.PageKind;
import gae_store_space.SentenceKind;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import pipeline.nlp.PlainTextTokenizer;
import pipeline.statistics_collectors.StatisticCollector;
import pipeline.text_extractors.Convertor;
import pipeline.text_extractors.SubtitlesToPlainText;

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
  
  public Set<String> getNGrams(ArrayList<SentenceKind> kinds) {
  	Multimap<String, SentenceKind> histo = statisticCollector.buildNGramHisto(kinds);
  	return histo.keySet();
  }
  
  // Now no store operations
  public PageKind pass(String pageName, String rawText) {
  	String pureText = removeFormatting(rawText);
  	
  	ImmutableList<String> sentences = tokenizer.getSentences(pureText);
  	
  	// Не очень логично, но важно соединить слова с контекстом, так что на обработку
  	//  передаем не чисто предложения, а недообработанные
  	ArrayList<SentenceKind> sentencesKinds = packSentences(sentences);
  	
    // Assemble statistic
    Multimap<String, SentenceKind> histo = statisticCollector.buildNGramHisto(sentencesKinds);

    ArrayList<NGramKind> nGramKinds = unpackHisto(histo);
    
    nGramKinds = calcImportancies(nGramKinds);

    // Sort words by frequency
    nGramKinds = sortByImportance(nGramKinds);

    return new PageKind(pageName, sentencesKinds, nGramKinds, rawText);
  }
}
