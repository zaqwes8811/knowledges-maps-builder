package business.parsers;

/**
 * Created with IntelliJ IDEA.
 * User: кей
 * Date: 08.03.14
 * Time: 15:29
 * To change this template use File | Settings | File Templates.
 */
import com.google.common.base.Charsets;
import com.google.common.io.Closer;
import common.Utils;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.ParseContext;
import org.junit.Test;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import java.io.*;
import java.util.Set;



public class SubtitlesParserTest {
  @Test
  public void testCreate() throws IOException {
    Closer closer = Closer.create();
    try {
      String filename = "statistic/Frozen.2013.CAMRIP.CHiLLYWiLLY.srt";
      InputStream in = closer.register(new FileInputStream(new File(filename)));

      BufferedReader reader = new BufferedReader(new InputStreamReader(in
        ,
        Charsets.US_ASCII
        //Charsets.UTF_8
      ));

      String buffer;
      StringBuilder result = new StringBuilder();
      while ((buffer = reader.readLine()) != null) {
        result.append(buffer+'\n');
      }

      Utils.print(result);

    } catch (Throwable e) { // must catch Throwable
      throw closer.rethrow(e);
    } finally {
      closer.close();
    }

  }
}
