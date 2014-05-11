package business.text_extractors;

import business.nlp.SentencesSplitter;
import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Closer;
import common.Util;
import org.apache.tika.parser.Parser;
import org.junit.Test;
import org.xml.sax.ContentHandler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotEquals;

public class SubtitlesParserTest {
  @Test
  public void testCreate() throws IOException {
    String filename = "/home/zaqwes/work/statistic/the.legend.of.korra.a.new.spiritual.age.(2013).eng.1cd.(5474296)/" +
      "The Legend of Korra - 02x10 - A New Spiritual Age.WEB-DL.BS.English.HI.C.orig.Addic7ed.com.srt";

    // Пока файл строго юникод - UTF-8
    Closer closer = Closer.create();
    try {
      String hello = "hello.\n high.";
      InputStream in = closer.register(new ByteArrayInputStream(hello.getBytes(Charsets.UTF_8 )));
      //InputStream in = closer.register(new FileInputStream(new File(filename)));

      Parser parser = new SubtitlesParser();
      List<String> sink = new ArrayList<String>();
      ContentHandler handler = new ContentHandlerImpl(sink);
      parser.parse(in, handler, null, null);

      // Убираем знаки прямой речи

      // Получили список строк.
      String text = Joiner.on(" ").join(sink);
      assertNotEquals("", text);

      ImmutableList<String> sentences = new SentencesSplitter().getSentences(text);
      for (String sentence: sentences) {
        Util.print(sentence);
      }
    } catch (Throwable e) { // must catch Throwable
      throw closer.rethrow(e);
    } finally {
      closer.close();
    }
  }
}
