package pipeline;

import com.google.common.collect.HashMultimap;
import pages.PageWithBoundary;
import gae_store_space.SentenceKind;

import java.util.*;

import org.apache.log4j.Logger;
import org.javatuples.Triplet;
import org.tartarus.snowball.ext.englishStemmer;
import pipeline.nlp.PlainTextTokenizer;
import pipeline.statistics_collectors.StatisticCollector;
import pipeline.text_extractors.Converter;
import pipeline.text_extractors.SubtitlesToPlainText;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;

public class TextPipeline {
  private static Logger log = Logger.getLogger(TextPipeline.class.getName());

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
  
  private ArrayList<Unigram> sortByImportance(ArrayList<Unigram> kinds) {
  	Collections.sort(kinds, Unigram.createImportanceComparator());
    Collections.reverse(kinds);
    return kinds;
  }

  private ArrayList<Unigram> unpackHisto(
  		Multimap<String, SentenceKind> histo,
  		Multimap<String, String> sourcesHist) {
  	ArrayList<Unigram> kinds = new ArrayList<Unigram>();
  	
    for (String stem: histo.keySet()) {
    	Set<String> s = new HashSet<String>(sourcesHist.get(stem));
    	
      Collection<SentenceKind> content = histo.get(stem);
      int rawFrequency = content.size();
      Unigram kind = Unigram.create(stem, content, rawFrequency);
      
      kinds.add(kind);
    }
    return kinds;
  }

  private String getStem(String value) {
    englishStemmer stemmer = new englishStemmer();
    String lowWord = value.toLowerCase();
    stemmer.setCurrent(lowWord);
    if (stemmer.stem())
      return stemmer.getCurrent();
    return lowWord;
  }
  
  private ArrayList<Unigram> calcImportances(ArrayList<Unigram> kinds) {
  	for (Unigram k: kinds)
  		k.calcImportance();

    // FIXME: for stems set sum frequency - word remain but change frequency
    //   It's change source distribution but wight grow two times.
    Multimap<String, Triplet<String, Integer, Integer>> statistic = HashMultimap.create();

    {
      Integer position = 0;
      for (Unigram k: kinds) {
        String stem = getStem(k.getNGram());
        statistic.put(stem, Triplet.with(k.getNGram(), k.getImportance(), position));
        position++;
      }
    }

    //if (statistic.size() < kinds.size())
    //  throw new AssertionError();
    for (String stem: statistic.keySet()) {
      Integer volume = 0;
      for (Triplet<String, Integer, Integer> elem : statistic.get(stem)) {
        volume += elem.getValue1();
      }

      for (Triplet<String, Integer, Integer> elem : statistic.get(stem)) {
        Integer t = kinds.get(elem.getValue2()).getImportance();
        kinds.get(elem.getValue2()).setImportance(volume);
        //log.info(t-volume);
      }
    }

  	return kinds;
  }
  
  public Set<String> getNGrams(ArrayList<SentenceKind> kinds) {
  	Multimap<String, SentenceKind> histo = statisticCollector.buildNGramHisto(kinds);
  	return histo.keySet();
  }
  
  // Now no store operations
  // Performance:
  //   text size around 1 Mb calc near 16 sec. - 4/2 1.6 GHz
  public PageWithBoundary pass(String pageName, String rawText) {
  	String pureText = removeFormatting(rawText);
  	
  	ImmutableList<String> sentences = tokenizer.getSentences(pureText);
  	
  	// Не очень логично, но важно соединить слова с контекстом, так что на обработку
  	//  передаем не чисто предложения, а недообработанные
  	ArrayList<SentenceKind> sentencesKinds = packSentences(sentences);
  	
    // Assemble statistic
    Multimap<String, SentenceKind> histo = statisticCollector.buildNGramHisto(sentencesKinds);
    Multimap<String, String> sources = statisticCollector.buildStemSourceHisto(sentencesKinds);

    ArrayList<Unigram> unigrams = unpackHisto(histo, sources);
    
    unigrams = calcImportances(unigrams);

    // Sort words by frequency
    unigrams = sortByImportance(unigrams);

    //Quartet<>
    return new PageWithBoundary(pageName, rawText, sentencesKinds, unigrams);
  }
}
