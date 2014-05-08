package business.text_extractors;

import com.google.common.base.CharMatcher;
import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.StringUtils;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.ParseContext;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SubtitlesParser implements org.apache.tika.parser.Parser {
  @Override
  public Set<MediaType> getSupportedTypes(ParseContext parseContext) {
    return null;
  }

  private final char SPECIAL_SYMBOL = '’';  // TODO: it's trouble. Константа в коде - юникодная.
  private final char REPLACE_SYMBOL = '\'';
  // TODO: it's weak. 00:31:19,764 --> 00:31:22,823
  // TODO: Усилить регулярным выражением
  private final String TIME_TICKET_SIGN = "-->";

  // Передать любой handler нельзя.
  @Override
  public void parse(
      InputStream stream, ContentHandler handler,
      Metadata metadata, ParseContext context) throws IOException, SAXException, TikaException
    {
    BufferedReader reader = new BufferedReader(new InputStreamReader(stream
        ,
        //Charsets.US_ASCII
        Charsets.UTF_8
      ));

    String buffer;
    List<String> result = new ArrayList<String>();
    handler.startDocument();
    while ((buffer = reader.readLine()) != null) {
      // TODO: not effective

      // Делим пробелами, а по всем пробельным символам
      // TODO: разделить не только пробелами
      ImmutableList<String> splitted = ImmutableList.copyOf(
        Splitter.on(CharMatcher.WHITESPACE)
          .trimResults()
          .omitEmptyStrings()
          .split(buffer));

      // Удаляем просто числа
      String joinString = Joiner.on("").join(splitted);

      if (!splitted.isEmpty()
          && !(splitted.size() == 1 && StringUtils.isNumeric(joinString))
          && !buffer.contains(TIME_TICKET_SIGN))
        {
        buffer = CharMatcher.is(SPECIAL_SYMBOL).replaceFrom(buffer, REPLACE_SYMBOL);
        result.add(buffer);
      }
    }
    // Summary
    handler.endDocument();
  }
}