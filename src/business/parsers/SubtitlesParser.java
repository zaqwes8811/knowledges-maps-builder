package business.parsers;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.ParseContext;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

public class SubtitlesParser implements org.apache.tika.parser.Parser {
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