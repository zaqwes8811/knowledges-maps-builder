package third_party_probes;

import com.google.common.io.Closer;
import org.junit.Test;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

// https://code.google.com/p/jcsv/
public class CSVTest {

  @Test
  public void testJCSVReader() throws IOException {
    // jdk6
    // jdk7 have try-with-resources block
    Closer closer = Closer.create();
    try {
      Reader reader = new FileReader("src/test/resources/Phrasebook - Sheet 1.csv");
      closer.register(reader);

    } catch (Throwable e) {
      closer.rethrow(e);
    } finally {
      closer.close();
    }
  }
}
