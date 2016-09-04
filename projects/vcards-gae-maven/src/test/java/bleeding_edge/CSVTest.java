package bleeding_edge;

import com.google.common.io.Closer;
import org.jsefa.Deserializer;
import org.jsefa.csv.CsvIOFactory;
import org.jsefa.csv.config.CsvConfiguration;
import org.junit.Test;
import java.io.*;

import static java.lang.System.out;

// https://code.google.com/p/jcsv/
public class CSVTest {
  private static final String csvFn = "../../materials/Phrasebook - Sheet 1.csv";

  @Test
  public void testEnc(){
    // http://stackoverflow.com/questions/2415597/java-how-to-detect-and-change-encoding-of-system-console/2415659#2415659
    //System.setProperty("console.encoding", "UTF-8"); "Cp866" "cp1252"
    //System.setOut(new PrintStream(System.out, true, "utf-8"));
    //System.setOut(new java.io.PrintStream(System.out, true, "Cp866"));  // вообще не отбражает
    //
    // Если ничего не далать, то отображает, но не все буквы
  }

  @Test
  public void testJSefaReader() throws IOException {
    Closer closer = Closer.create();
    try {
        // fixme: Нужно сделать чтение не из файла, а из строки

      //Reader reader = new FileReader(csvFn);
      // encoding troubles
      // http://www.skipy.ru/technics/encodings_console_comp.html
      BufferedReader reader = new BufferedReader(
        new InputStreamReader(
          new FileInputStream(csvFn), "UTF8"));
      closer.register(reader);

      // http://stackoverflow.com/questions/24400114/issue-with-bigedecimal-value-deserialization
      CsvConfiguration csvConfig = new CsvConfiguration();
      csvConfig.setFieldDelimiter(',');
      csvConfig.setLineBreak("\n");
      csvConfig.setQuoteCharacter('\"');

      Deserializer deserializer = CsvIOFactory.createFactory(csvConfig,
              GoogleTranslatorRecord.class).createDeserializer();
      deserializer.open(reader);

      while (deserializer.hasNext()) {
        GoogleTranslatorRecord p = deserializer.next();
        if(p.from.equals("English")){
            out.println(p.from);
        }
      }
      deserializer.close(true);

    } catch (Throwable e) {
      closer.rethrow(e);
    } finally {
      closer.close();
    }
  }
}
