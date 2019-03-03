package backend;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import org.apache.log4j.Logger;
import org.javatuples.Triplet;
import org.tartarus.snowball.ext.englishStemmer;
import backend.nlp.PlainTextTokenizer;
import backend.text_extractors.Converter;
import backend.text_extractors.SubtitlesToPlainText;

import java.util.*;

public class TextPipeline {
  private static Logger log = Logger.getLogger(TextPipeline.class.getName());

  private Converter converter = new SubtitlesToPlainText();
	private PlainTextTokenizer tokenizer = new PlainTextTokenizer();
	private StatisticCollector statisticCollector = new StatisticCollector();
	
  private String removeFormatting(String rawText) {
  	return converter.convert(rawText);
  }

  private ArrayList<ContentItem> packSentences(ImmutableList<String> sentences) {
    ArrayList<ContentItem> r = new ArrayList<ContentItem>();
    for (String sentence: sentences)
      r.add(new ContentItem(sentence));
    return r;
  }
  
  private ArrayList<UniGram> sortByImportance(ArrayList<UniGram> kinds) {
  	Collections.sort(kinds, UniGram.createImportanceComparator());
    Collections.reverse(kinds);
    return kinds;
  }

  private ArrayList<UniGram> unpackHisto(
  		Multimap<String, ContentItem> histo,
  		Multimap<String, String> sourcesHist) {
  	ArrayList<UniGram> kinds = new ArrayList<UniGram>();
  	
    for (String stem: histo.keySet()) {
    	Set<String> s = new HashSet<String>(sourcesHist.get(stem));
    	
      Collection<ContentItem> content = histo.get(stem);
      int rawFrequency = content.size();
      UniGram kind = UniGram.create(stem, content, rawFrequency);
      
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
  
  private ArrayList<UniGram> calcImportances(ArrayList<UniGram> kinds) {
  	for (UniGram k: kinds)
  		k.calcImportance();

    // FIXME: for stems set sum frequency - word remain but change frequency
    //   It's change source distribution but wight grow two times.
    Multimap<String, Triplet<String, Integer, Integer>> statistic = HashMultimap.create();

    {
      Integer position = 0;
      for (UniGram k: kinds) {
        String stem = getStem(k.getValue());
        statistic.put(stem, Triplet.with(k.getValue(), k.getImportance(), position));
        position++;
      }
    }

    //if (statistic.size() < gae_related.size())
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
  
  public Set<String> getNGrams(ArrayList<ContentItem> kinds) {
  	Multimap<String, ContentItem> histo = statisticCollector.buildNGramHisto(kinds);
  	return histo.keySet();
  }
  
  // Now no store operations
  // Performance:
  //   text size around 1 Mb calc near 16 sec. - 4/2 1.6 GHz
  static <T> T pageName(T t){  return t; }
  static <T> T source(T t){  return t; }
  static <T> T kinds(T t){  return t; }

  public ValuePipeline pass(String pageName, String rawText) {
  	String pureText = removeFormatting(rawText);  // FIXME: very slow!!
  	
  	ImmutableList<String> sentences = tokenizer.getSentences(pureText);
  	
  	// Не очень логично, но важно соединить слова с контекстом, так что на обработку
  	//  передаем не чисто предложения, а недообработанные
  	ArrayList<ContentItem> sentencesKinds = packSentences(sentences);
  	
    // Assemble statistic
    Multimap<String, ContentItem> histo = statisticCollector.buildNGramHisto(sentencesKinds);
    Multimap<String, String> sources = statisticCollector.buildStemSourceHisto(sentencesKinds);

    ArrayList<UniGram> UniGrams = unpackHisto(histo, sources);
    
    UniGrams = calcImportances(UniGrams);

    // Sort words by frequency
    UniGrams = sortByImportance(UniGrams);

    // Can filter it now - not here - best assemble full statistic

    return new ValuePipeline(pageName, rawText, sentencesKinds, UniGrams);
  }
}
