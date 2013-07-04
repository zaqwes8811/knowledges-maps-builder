package parsers;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import com.google.common.collect.*;
import common.utils;
import crosscuttings.AppConstants;

import java.util.ArrayList;
import java.util.List;


// Наверное случайный выборщик будет оборачивать это класс, а не будет встроенным в него.
// Если его использовать статическим, то его нельзя будет передать!
public class ImmutableBECParser {
  public static final Optional<ImmutableBECParser> getInstance () {
    return INSTANCE;
  }

  public static final Optional<ImmutableBECParser> INSTANCE = Optional.of(new ImmutableBECParser());

  private ImmutableBECParser() {}

  private static final Integer KEY_POS = 0;
  private static final String SPLITTER = "@";
  private static final String FAKE_TRANSLATE = "No";
  private static final String PATH = "statistic-data";
  private static final String FILENAME = "vocabularity-folded.txt";
  private static final String FULL_FILENAME = Joiner.on(AppConstants.PATH_SPLITTER).join(PATH, FILENAME);


  private static final Optional<ImmutableList<String>> CONTENT;
  private static final Integer COUNT_WORDS;  // Нужно будет для генератора случайных числел
      //   но хранится будет здесь.
  // Индексты тоже сделать такими же
  private static final Optional<ImmutableList<String>> SORTED_WORDS_ALPH;
  private static final Optional<ImmutableMultimap<String, String>> WORDS_TRANSLATES;
  private static final Optional<ImmutableMultimap<String, String>> WORDS_CONTENT;

  static {
    Optional<ImmutableList<String>> content = utils.file2list(FULL_FILENAME);

    if (content.isPresent()) {
      CONTENT = Optional.of(ImmutableList.copyOf(content.get()));

      List<String> cashWords = new ArrayList<String>();
      Multimap<String, String> cashTranslate = HashMultimap.create();
      Multimap<String, String> cashContent = HashMultimap.create();

      for (String record: CONTENT.get()) {
        List<String> parsedLine = Lists.newArrayList(Splitter.on(SPLITTER).split(record));
        if (!parsedLine.isEmpty()) {
          String word = parsedLine.get(KEY_POS);
          cashWords.add(word);
          cashTranslate.put(word, FAKE_TRANSLATE);
          cashContent.putAll(word, parsedLine.subList(KEY_POS, parsedLine.size()));
        }
      }

      WORDS_TRANSLATES = Optional.of(ImmutableMultimap.copyOf(cashTranslate));
      SORTED_WORDS_ALPH = Optional.of(ImmutableList.copyOf(cashWords));
      WORDS_CONTENT = Optional.of(ImmutableMultimap.copyOf(cashContent));
      COUNT_WORDS = SORTED_WORDS_ALPH.get().size();
    } else {
      CONTENT = Optional.absent();
      SORTED_WORDS_ALPH = Optional.absent();
      WORDS_TRANSLATES = Optional.absent();
      WORDS_CONTENT = Optional.absent();
      COUNT_WORDS = 0;
      utils.print("Can't read file - "+FULL_FILENAME);
    }
  }

  public static void main(String[] args) {
    //Optional<ImmutableBECParser> parser = ImmutableBECParser.getInstance();

    utils.print(WORDS_TRANSLATES);
  }
}
