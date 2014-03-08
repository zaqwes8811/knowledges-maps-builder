package business.parsers;

import com.google.common.io.Closer;
import org.apache.tika.parser.Parser;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class SubtitlesParserTest {
  @Test
  public void testCreate() throws IOException {
    String filename = "statistic/Frozen.2013.CAMRIP.CHiLLYWiLLY.srt";

    // Пока файл строго юникод - UTF-8
    Closer closer = Closer.create();
    try {
      InputStream in = closer.register(new FileInputStream(new File(filename)));
      Parser parser = new SubtitlesParser();
      parser.parse(in, null, null, null);

    } catch (Throwable e) { // must catch Throwable
      throw closer.rethrow(e);
    } finally {
      closer.close();
    }
  }
}
