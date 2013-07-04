package parsers;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import common.utils;
import crosscuttings.AppConstants;

import java.util.ArrayList;
import java.util.List;

public class ImmutableBECParser {
  private ImmutableBECParser() {}

  private static final Integer KEY_POS = 0;

  private static final Optional<List<String>> CONTENT;
  // Индексты тоже сделать такими же
  private static final Optional<List<String>> SORTED_WORDS_ALPH;
  private static final Optional<List<String>> WORDS_TRANSLATES;

  static {
    CONTENT = utils.file2list(
        Joiner.on(AppConstants.PATH_SPLITTER)
          .join(
              "statistic-data",
              "vocabularity-folded.txt"));
    List<String> sortedWordsAlph = new ArrayList<String>();

    if (CONTENT.isPresent()) {
      for (String record: CONTENT.get()) {
        List<String> parsedLine = Lists.newArrayList(Splitter.on("@").split(record));
        if (!parsedLine.isEmpty()) {
          sortedWordsAlph.add(parsedLine.get(KEY_POS));
        }
      }
      SORTED_WORDS_ALPH = Optional.of(sortedWordsAlph);
    } else {
      SORTED_WORDS_ALPH = Optional.absent();
      utils.print("Can't read file");
    }
  }

  public static void main(String[] args) {

  }
}
