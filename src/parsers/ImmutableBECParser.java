package parsers;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import com.google.common.collect.*;
import common.utils;
import crosscuttings.AppConstants;

import java.io.IOError;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


// Наверное случайный выборщик будет оборачивать это класс, а не будет встроенным в него.
// Если его использовать статическим, то его нельзя будет передать!
public class ImmutableBECParser {
  public static final ImmutableBECParser getInstance () {
    return INSTANCE;
  }

  // Похоже конструкторы параметры не передать. Поэтому путь жесткая константа.
  private static final ImmutableBECParser INSTANCE;

  static {
    INSTANCE = new ImmutableBECParser();
  }

  private ImmutableBECParser() {

    // Доступ может быть конкурентым, если создается несколько объектов.
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
      // Если здесь catch, то пишет что нельзя иниц. дважды, если убрать пишет что не иниц.
      CONTENT = Optional.absent();
      SORTED_WORDS_ALPH = Optional.absent();
      WORDS_TRANSLATES = Optional.absent();
      WORDS_CONTENT = Optional.absent();
      COUNT_WORDS = 0;
    }
  }

  private final Integer KEY_POS = 0;
  private final String SPLITTER = "@";
  private final String FAKE_TRANSLATE = "No";
  private final String PATH = "statistic-data";
  private final String FILENAME = "vocabularit_y-folded.txt";
  private final String FULL_FILENAME = Joiner.on(AppConstants.PATH_SPLITTER).join(PATH, FILENAME);

  // Допустим испльзуем
  // Если коллекция пустая, то возможно было пустой файл, а так сразу ясно, что что-то не так
  //   но появляние ошибки растянуто в времени.
  private final Optional<ImmutableList<String>> CONTENT;
  private final Integer COUNT_WORDS;  // Нужно будет для генератора случайных числел
      //   но хранится будет здесь.
  // Индексты тоже сделать такими же
  private final Optional<ImmutableList<String>> SORTED_WORDS_ALPH;
  private final Optional<ImmutableMultimap<String, String>> WORDS_TRANSLATES;
  private final Optional<ImmutableMultimap<String, String>> WORDS_CONTENT;

  public static void main(String[] args) {
    //Optional<ImmutableBECParser> parser = ImmutableBECParser.getInstance();

  }
}
