package store_gae_stuff.fakes;

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
import store_gae_stuff.ContentItemKind;
import store_gae_stuff.ContentPageBuilder;
import store_gae_stuff.ContentPageKind;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

//import static org.junit.Assert.assertFalse;

public class BuilderOneFakePage {
  private String getSubtitlesToPlainText(String filename) throws IOException {
    String rawText = Joiner.on('\n').join(Tools.fileToList(filename));
    //InputStream in = closer.register(new FileInputStream(new File(filename)));  // No in GAE

    // Пока файл строго юникод - UTF-8
    Closer closer = Closer.create();
    try {
      // http://stackoverflow.com/questions/247161/how-do-i-turn-a-string-into-a-stream-in-java
      InputStream in = closer.register(new ByteArrayInputStream(rawText.getBytes(Charsets.UTF_8)));
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
  }

  public ContentPageKind buildContentPage(String pageName) {
    String filename = getTestFileName();
    try {
      // Phase I
      String plainText = getSubtitlesToPlainText(filename);
      assert !plainText.isEmpty();

      // Phase II не всегда они разделены, но с случае с субтитрами точно разделены.
      ArrayList<ContentItemKind> contentElements = getContentElements(plainText);

      // Last - Persist page
      return new ContentPageBuilder().build(pageName, contentElements);
    } catch (IOException e)  {
      throw new RuntimeException(e);
    }
  }

  private ArrayList<ContentItemKind> getContentElements(String text) {
    ImmutableList<String> sentences = new PlainTextTokenizer().getSentences(text).subList(1, 50);
    assert !sentences.isEmpty();

    // Пакуем
    ArrayList<ContentItemKind> contentElements = new ArrayList<ContentItemKind>();
    for (String sentence: sentences) {
      contentElements.add(new ContentItemKind(sentence));
    }

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
    return "test_data/korra/The Legend of Korra - 02x10 - A New Spiritual Age.WEB-DL.BS.English.HI.C.orig.Addic7ed.com.srt";
  }
}
