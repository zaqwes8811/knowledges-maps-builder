package gae_store_space.high_perf;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Closer;

import common.Tools;
import core.nlp.PlainTextTokenizer;
import core.text_extractors.SpecialSymbols;
import core.text_extractors.SubtitlesContentHandler;
import core.text_extractors.SubtitlesParser;

import org.apache.tika.parser.Parser;
import org.xml.sax.ContentHandler;

import gae_store_space.ContentItemKind;
import gae_store_space.ContentPageKind;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class OnePageProcessor {
	public static final String defailtPageName = "Korra";
	public static final String defaultGenName = "Gen";
	public static final String defaultUserId = "user";
	
  private String getGetPlainTextFromSubtitlesFile(String filename) {
  	try {
  		ArrayList<String> lines = new ArrayList<String>(Tools.fileToList(filename).asList());
  		return convertSubtitlesToPlainText(lines);
  	} catch(IOException e) {
  		throw new RuntimeException(e);
  	}
  }
  
  private String convertSubtitlesToPlainText(String lines) {
  	try {
      // Пока файл строго юникод - UTF-8
  	    Closer closer = Closer.create();
  	    try {
  	      // http://stackoverflow.com/questions/247161/how-do-i-turn-a-string-into-a-stream-in-java
  	      InputStream in = closer.register(new ByteArrayInputStream(lines.getBytes(Charsets.UTF_8)));
  	      Parser parser = new SubtitlesParser();
  	      List<String> sink = new ArrayList<String>();
  	      ContentHandler handler = new SubtitlesContentHandler(sink);
  	      parser.parse(in, handler, null, null);
  	
  	      // Получили список строк.
  	      SpecialSymbols symbols = new SpecialSymbols();
  	      return Joiner.on(symbols.WHITESPACE_STRING).join(sink);
  	    } catch (Throwable e) {
  	      throw closer.rethrow(e);
  	    } finally {
  	      closer.close();
  	    }
    	} catch(IOException e) {
    		throw new RuntimeException(e);
    	}
  }
  
  private String convertSubtitlesToPlainText(ArrayList<String> lines) {
  	String rawText = Joiner.on('\n').join(lines);
  	return convertSubtitlesToPlainText(rawText);  	
  }

  public ContentPageKind buildContentPage(String pageName) {
    String filename = getTestFileName();
    
    // Phase I
    String plainText = getGetPlainTextFromSubtitlesFile(filename);

    // Phase II не всегда они разделены, но с случае с субтитрами точно разделены.
    ArrayList<ContentItemKind> contentElements = getContentElements(plainText);

    // Last - Persist page
    return new ContentPageBuilder().build(pageName, contentElements);
  }

  private ArrayList<ContentItemKind> getContentElements(String text) {
    ImmutableList<String> sentences = new PlainTextTokenizer().getSentences(text);

    // Пакуем
    ArrayList<ContentItemKind> contentElements = new ArrayList<ContentItemKind>();
    for (String sentence: sentences)
      contentElements.add(new ContentItemKind(sentence));

    return contentElements;
  }

  private String getPlainText() {
    return
      "Born of cold and Winter air And mountain rain combining, This icy force" +
        "both foul and fair Has a frozen heart worth mining. Cut through the heart, Cold and Clear. Strike for love And" +
        "Strike for fear. See the beauty Sharp and Sheer.  Split the ice apart" +
        "And break the frozen heart. Hup! Ho! Watch your step! Let it go! Hup! Ho! " +
        "Watch your step! Let it go! Beautiful! Powerful! Dangerous! Cold!";
  }

  private String getTestFileName() {
  	// FIXME: troubles on GAE
    return "./test_data/korra/data.srt";
  }
}
