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

  private static final char APOSTROPHE_ = '’';  // TODO: it's trouble. Константа в коде - юникодная.
  private static final char ONE_QUOTE_ = '\'';
  private static final char LEFT_ANGLE_BRACKET_ = '[';
  private static final char DOT_ = '.';
  private static final char WHITESPACE_ = ' ';
  private static final char RIGHT_ANGLE_BRACKET_ = ']';

  // TODO: it's weak. 00:31:19,764 --> 00:31:22,823
  // TODO: Усилить регулярным выражением
  private static final String TIME_TICKET_SIGN_ = "-->";
  private static final String EMPTY_STRING_ = "";


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

    List<String> items = new ArrayList<String>();
    handler.startDocument();
    while (true) {
      String buffer;
      if ((buffer = reader.readLine()) == null)
        break;

      // TODO: not effective
      // TODO: Что? Делим пробелами, а по всем пробельным символам
      // TODO: разделить не только пробелами
      ImmutableList<String> list = ImmutableList.copyOf(
        Splitter.on(CharMatcher.WHITESPACE)
          .trimResults()
          .omitEmptyStrings()
          .split(buffer));

      // Удаляем просто числа
      String line = Joiner.on(EMPTY_STRING_).join(list);

      if (!list.isEmpty()
          && !(list.size() == 1 && StringUtils.isNumeric(line))
          && !buffer.contains(TIME_TICKET_SIGN_))
        {
        // Некоторые замены исходя из статистики
        buffer = CharMatcher.is(LEFT_ANGLE_BRACKET_).replaceFrom(buffer, WHITESPACE_);
        buffer = CharMatcher.is(RIGHT_ANGLE_BRACKET_).replaceFrom(buffer, DOT_);
        buffer = CharMatcher.is(APOSTROPHE_).replaceFrom(buffer, ONE_QUOTE_);

        handler.characters(buffer.toCharArray(), 0, buffer.length());
      }
    }
    // Summary
    handler.endDocument();
  }
}