package business.parsers;

import com.google.common.base.Charsets;
import org.junit.Test;
import org.xml.sax.ContentHandler;

import java.util.ArrayList;
import java.util.List;

/**
 */
public class ContentHandlerImplTest {
  @Test
  public void testCharacters() throws Exception {
    List<String> sink = new ArrayList<String>();
    ContentHandler handler = new ContentHandlerImpl(sink);
    String step0 = "hello";
    char [] bytes = step0.toCharArray();

    handler.characters(bytes, 0, bytes.length);

    assert sink.size() == 1;
  }
}
