package third_party_probes;

import com.google.common.io.Closer;
import com.googlecode.jcsv.CSVStrategy;
import com.googlecode.jcsv.annotations.internal.ValueProcessorProvider;
import com.googlecode.jcsv.reader.CSVEntryParser;
import com.googlecode.jcsv.reader.CSVReader;
import com.googlecode.jcsv.reader.internal.AnnotationEntryParser;
import com.googlecode.jcsv.reader.internal.CSVReaderBuilder;
import org.aspectj.lang.annotation.Before;
import org.jsefa.Deserializer;
import org.jsefa.csv.CsvIOFactory;
import org.jsefa.csv.config.CsvConfiguration;
import org.junit.Test;

import java.io.*;
import java.util.List;

import static java.lang.System.out;



// https://code.google.com/p/jcsv/
public class CSVTest {
  private static final String readFile = "src/test/resources/Phrasebook - Sheet 1.csv";
  private static final String testFile = "src/test/resources/test.csv";

  @org.junit.Before
  public void  setUp() throws UnsupportedEncodingException {
    // http://stackoverflow.com/questions/2415597/java-how-to-detect-and-change-encoding-of-system-console/2415659#2415659
    //System.setProperty("console.encoding", "UTF-8"); "Cp866" "cp1252"
    //System.setOut(new PrintStream(System.out, true, "utf-8"));
    //System.setOut(new java.io.PrintStream(System.out, true, "Cp866"));  // вообще не отбражает
    //
    // Если ничего не далать, то отображает, но не все буквы
  }

  @Deprecated
  @Test
  public void testJCSVReader() throws IOException {
    // jdk6
    // jdk7 have try-with-resources block
    Closer closer = Closer.create();
    try {
      Reader reader = new FileReader("src/test/resources/Phrasebook - Sheet 1.csv");
      closer.register(reader);

      ValueProcessorProvider provider = new ValueProcessorProvider();

      CSVEntryParser<Person> entryParser = new AnnotationEntryParser<Person>(Person.class, provider);
      CSVReader<Person> csvPersonReader = new CSVReaderBuilder<Person>(reader)
        .strategy(CSVStrategy.UK_DEFAULT)
        .entryParser(entryParser)
        .build();

      // Format looks valid but failed on more then one field
      List<Person> persons = csvPersonReader.readAll();  // Failed
      for (Person p : persons) {
        out.println(p.translate);
      }
    } catch (Throwable e) {
      closer.rethrow(e);
    } finally {
      closer.close();
    }
  }

  @Test
  public void testJSefaReader() throws IOException {
    // jdk6
    // jdk7 have try-with-resources block
    Closer closer = Closer.create();
    try {
      //Reader reader = new FileReader(readFile);
      // encoding troubles
      // http://www.skipy.ru/technics/encodings_console_comp.html
      BufferedReader reader = new BufferedReader(
        new InputStreamReader(
          new FileInputStream(readFile), "UTF8"));
      closer.register(reader);

      // http://stackoverflow.com/questions/24400114/issue-with-bigedecimal-value-deserialization
      CsvConfiguration csvConfig = new CsvConfiguration();
      csvConfig.setFieldDelimiter(',');
      csvConfig.setLineBreak("\n");
      csvConfig.setQuoteCharacter('\"');

      Deserializer deserializer = CsvIOFactory.createFactory(csvConfig, Person.class).createDeserializer();
      //StringReader reader = new StringReader(writer.toString());
      deserializer.open(reader);

      out.println();
      while (deserializer.hasNext()) {
        Person p = deserializer.next();
        // do something useful with it
        //out.println(p.translate);
      }
      deserializer.close(true);

    } catch (Throwable e) {
      closer.rethrow(e);
    } finally {
      closer.close();
    }
  }
}
