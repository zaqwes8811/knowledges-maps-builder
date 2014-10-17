package gae_store_space.high_perf;

import static gae_store_space.OfyService.ofy;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import com.google.common.io.Closer;

import common.Tools;
import core.mapreduce.CountReducer;
import core.mapreduce.CounterMapper;
import core.nlp.PlainTextTokenizer;
import core.text_extractors.SpecialSymbols;
import core.text_extractors.SubtitlesContentHandler;
import core.text_extractors.SubtitlesParser;

import org.apache.tika.parser.Parser;
import org.xml.sax.ContentHandler;

import gae_store_space.SentenceKind;
import gae_store_space.PageKind;
import gae_store_space.WordKind;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class OnePageProcessor {
	public static final String defaultPageName = "Korra";
	public static final String defaultGenName = "Default";
	public static final String defaultUserId = "User";
	
	private SubtitlesToPlainText convertor = new SubtitlesToPlainText();
	
  public String getGetPlainTextFromFile(String filename) {
  	try {
  		ArrayList<String> lines = new ArrayList<String>(Tools.fileToList(filename).asList());
  		return convertToPlainText(lines);
  	} catch(IOException e) {
  		throw new RuntimeException(e);
  	}
  }

  private String convertToPlainText(ArrayList<String> lines) {
  	String rawText = Joiner.on('\n').join(lines);
  	return convertor.convert(rawText);  	
  }
 
  public PageKind buildPageKind(String pageName, String filename) {  
    // Phase I
    String plainText = getGetPlainTextFromFile(filename);
    // Last - Persist page
    return buildPageKindFromPlainText(pageName, plainText);
  }
  
  public PageKind buildPageKindFromPlainText(String pageName, String plainText) {  
    // Phase II не всегда они разделены, но с случае с субтитрами точно разделены.
    ArrayList<SentenceKind> contentElements = getContentElements(plainText);

    // Last - Persist page
    return build(pageName, contentElements);
  }
  
  //public PageKind buildContentPageFromPlainText() { }

  private ArrayList<SentenceKind> getContentElements(String text) {
    ImmutableList<String> sentences = new PlainTextTokenizer().getSentences(text);

    // Пакуем
    ArrayList<SentenceKind> contentElements = new ArrayList<SentenceKind>();
    for (String sentence: sentences)
      contentElements.add(new SentenceKind(sentence));

    return contentElements;
  }

  public String getTestFileName() {
    return "./test_data/korra/data.srt";
  }
  
  // FIXME: to expensive
  private PageKind build(String name, ArrayList<SentenceKind> contentElements) {
  	// FIXME: убрать отсюда весь доступ к хранилищу
  	
    // TODO: BAD! В страницу собрана обработка
    Multimap<String, SentenceKind> wordHistogramSink = HashMultimap.create();
    CountReducer reducer = new CountReducer(wordHistogramSink);
    CounterMapper mapper = new CounterMapper(reducer);

    // Split
    mapper.map(contentElements);  // TODO: implicit, but be so

    ArrayList<WordKind.WordValue> value = new ArrayList<WordKind.WordValue>();
    for (String word: wordHistogramSink.keySet()) {
      Collection<SentenceKind> content = wordHistogramSink.get(word);
      int rawFrequency = content.size();
      value.add(new WordKind.WordValue(word, rawFrequency, content));
    }

    // Sort words by frequency and assign idx
    Collections.sort(value, new WordKind.WordValueFrequencyComparator());
    Collections.reverse(value);

    ArrayList<WordKind> words = new ArrayList<WordKind>();
    {
      // WARNING: Порядок важен! Важно сохранить елементы до того как присоединять
      //   к словам.
      //
      // Persist content contentItems
      // TODO: как вынести в транзакцию?
      ofy().save().entities(contentElements).now();

      for (int i = 0; i < value.size(); i++) {
        WordKind.WordValue v = value.get(i);
        words.add(WordKind.create(v.word, v.items, v.frequency));
        words.get(i).setPointPos(i);
      }

      // TODO: вынести все операции с базой данных сюда
      ofy().save().entities(words).now();
    }

    // Слова сортированы
    return new PageKind(name, contentElements, words);
  }
}
