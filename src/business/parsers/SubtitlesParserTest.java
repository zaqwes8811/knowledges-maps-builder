package business.parsers;

/**
 * Created with IntelliJ IDEA.
 * User: кей
 * Date: 08.03.14
 * Time: 15:29
 * To change this template use File | Settings | File Templates.
 */
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.ParseContext;
import org.junit.Test;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

class SubtitlesParser implements org.apache.tika.parser.Parser {
  @Override
  public Set<MediaType> getSupportedTypes(ParseContext parseContext) {
    return null;
  }

  @Override
  public void parse(
    InputStream stream, ContentHandler handler,
    Metadata metadata, ParseContext context)
    throws IOException, SAXException, TikaException {

  }
}

public class SubtitlesParserTest {
  @Test
  public void testCreate() {

  }
}
