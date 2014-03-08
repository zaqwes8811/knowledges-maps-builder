package business.parsers;

import com.google.common.base.CharMatcher;
import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Closer;
import common.Utils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.io.*;

public class SubtitlesParserTest {
  @Test
  public void testCreate() throws IOException {
    char SPECIAL_SYMBOL = '’';  // TODO: it's trouble. Константа в коде - юникодная.

    // Пока файл строго юникод - UTF-8
    Closer closer = Closer.create();
    try {
      String filename = "statistic/Frozen.2013.CAMRIP.CHiLLYWiLLY.srt";
      InputStream in = closer.register(new FileInputStream(new File(filename)));

      BufferedReader reader = new BufferedReader(new InputStreamReader(in
        ,
        //Charsets.US_ASCII
        Charsets.UTF_8
      ));

      String buffer;
      StringBuilder result = new StringBuilder();
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
            && !(splitted.size() == 1
              && StringUtils.isNumeric(joinString)))
          {
          buffer = CharMatcher.is(SPECIAL_SYMBOL).replaceFrom(buffer, "'");
          Utils.print(buffer);
          result.append(buffer).append('\n');
        }
      }
    } catch (Throwable e) { // must catch Throwable
      throw closer.rethrow(e);
    } finally {
      closer.close();
    }
  }
}
