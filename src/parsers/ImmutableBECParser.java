package parsers;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import com.google.common.collect.*;
import common.utils;
import crosscuttings.AppConstants;

import java.util.ArrayList;
import java.util.List;

public class ImmutableBECParser {
  private ImmutableBECParser() {}

  private static final Integer KEY_POS = 0;

  private static final Optional<ImmutableList<String>> CONTENT;
  // Индексты тоже сделать такими же
  private static final Optional<ImmutableList<String>> SORTED_WORDS_ALPH;
  private static final Optional<ImmutableMultimap<String, String>> WORDS_TRANSLATES;

  static {

    // Самое тонкое место!
    Optional<ImmutableList<String>> tmp = utils.file2list(
      Joiner.on(AppConstants.PATH_SPLITTER)
        .join(
          "statistic-data",
          "vocabularity-folded.txt"));

    if (tmp.isPresent()) {
      CONTENT = Optional.of(ImmutableList.copyOf(tmp.get()));
    } else {
      CONTENT = Optional.absent();
    }

    // Конкретные данные.
    List<String> sortedWordsAlph = new ArrayList<String>();
    Multimap<String, String> cashTranslate = HashMultimap.create();

    if (CONTENT.isPresent()) {
      for (String record: CONTENT.get()) {
        List<String> parsedLine = Lists.newArrayList(Splitter.on("@").split(record));
        if (!parsedLine.isEmpty()) {
          String word = parsedLine.get(KEY_POS);
          sortedWordsAlph.add(word);
          cashTranslate.put(word, "No");
        }
      }
      WORDS_TRANSLATES = Optional.of(ImmutableMultimap.copyOf(cashTranslate));
      SORTED_WORDS_ALPH = Optional.of(ImmutableList.copyOf(sortedWordsAlph));
    } else {
      SORTED_WORDS_ALPH = Optional.absent();
      WORDS_TRANSLATES = Optional.absent();
      utils.print("Can't read file");
    }
  }

  public static void main(String[] args) {
    utils.print(WORDS_TRANSLATES);
  }
}
