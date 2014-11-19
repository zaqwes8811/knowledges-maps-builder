package third_party_probes;

import org.junit.Test;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

// https://code.google.com/p/jcsv/
public class CSVTest {

  @Test
  void testJCSVReader() throws IOException {
    Reader reader = new FileReader("persons.csv");
  }
}
