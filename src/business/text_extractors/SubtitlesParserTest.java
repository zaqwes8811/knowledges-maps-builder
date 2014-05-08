package business.text_extractors;

import business.nlp.SentencesSplitter;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Closer;
import org.apache.tika.parser.Parser;
import org.junit.Test;
import org.xml.sax.ContentHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class SubtitlesParserTest {
  @Test
  public void testCreate() throws IOException {
    String filename = "statistic/Frozen.2013.CAMRIP.CHiLLYWiLLY.srt";

    // Пока файл строго юникод - UTF-8
    Closer closer = Closer.create();
    try {
      InputStream in = closer.register(new FileInputStream(new File(filename)));
      Parser parser = new SubtitlesParser();
      List<String> sink = new ArrayList<String>();
      ContentHandler handler = new ContentHandlerImpl(sink);
      parser.parse(in, handler, null, null);

      // Убираем знаки прямой речи

      // Получили список строк.
      String text = Joiner.on(" ").join(sink);
      ImmutableList<String> sentences = new SentencesSplitter().getSentences(text);
    } catch (Throwable e) { // must catch Throwable
      throw closer.rethrow(e);
    } finally {
      closer.close();
    }
  }
}
