package parsers;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import com.google.common.collect.*;
import common.Utils;
import crosscuttings.AppConstants;

import java.io.IOError;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


// Наверное случайный выборщик будет оборачивать это класс, а не будет встроенным в него.
// Если его использовать статическим, то его нельзя будет передать!
//
// Option - что-то вроде альтернативы обработки ошибок, но она не гибкая, не дает никакой информации.
//   зато дает ответ - Да(результат)/Нет(ничего)
public final class ImmutableBECParser {
  public static final Optional<ImmutableBECParser> getDefaultInstance () {
    return INSTANCE;
  }

  // Возвращает экземпляр парсера BEC
  //
  // Излишняя потокозащита. Она нужна только для доступа к файлу. Хотя это общая защита при создании
  //   и возможно защиты только непосредственного доступа к файлу недостаточно.
  public static synchronized final Optional<ImmutableBECParser> getInstance(String fullFilename) {
    // No lazily initializing!
    Optional<ImmutableBECParser> tmp;
    try {
      tmp = Optional.of(new ImmutableBECParser(fullFilename));
    } catch (IOException e) {
      // Не сконструирован. Использовать нельзя. Ноль, не ноль не важно.
      //   Не совсем ясно что с полями.
      Utils.print(e.getMessage());
      tmp = Optional.absent();
    }
    return tmp;
  }

  // Похоже конструкторы параметры не передать. Поэтому путь жесткая константа.
  private static final Optional<ImmutableBECParser> INSTANCE;

  static {
    final String PATH = "statistic-data";
    final String FILENAME = "vocabularity-folded.txt";
    final String FULL_FILENAME = Joiner.on(AppConstants.PATH_SPLITTER).join(PATH, FILENAME);
    // No lazily initializing!
    Optional<ImmutableBECParser> tmp;
    try {
      tmp = Optional.of(new ImmutableBECParser(FULL_FILENAME));
    } catch (IOException e) {
      // Не сконструирован. Использовать нельзя. Ноль, не ноль не важно.
      //   Не совсем ясно что с полями.
      Utils.print(e.getMessage());
      tmp = Optional.absent();
    }
    INSTANCE = tmp;
  }

  private ImmutableBECParser(String fullFilename) throws IOException {
    ImmutableList<String> content = Utils.file2list(fullFilename);

    CONTENT = ImmutableList.copyOf(content);

    List<String> cashWords = new ArrayList<String>();
    Multimap<String, String> cashTranslate = HashMultimap.create();
    Multimap<String, String> cashContent = HashMultimap.create();

    for (final String record: CONTENT) {
      List<String> parsedLine = Lists.newArrayList(Splitter.on(SPLITTER).split(record));
      if (!parsedLine.isEmpty()) {
        String word = parsedLine.get(KEY_POS);
        cashWords.add(word);
        cashTranslate.put(word, FAKE_TRANSLATE);
        cashContent.putAll(word, parsedLine.subList(KEY_POS, parsedLine.size()));
      }
    }

    WORDS_TRANSLATES = ImmutableMultimap.copyOf(cashTranslate);
    SORTED_WORDS_ALPH = ImmutableList.copyOf(cashWords);
    WORDS_CONTENT = ImmutableMultimap.copyOf(cashContent);
    COUNT_WORDS = SORTED_WORDS_ALPH.size();
  }

  private final Integer KEY_POS = 0;
  private final String SPLITTER = "@";
  private final String FAKE_TRANSLATE = "No";

  // Допустим испльзуем не исключение при чтении файла, возвращаем пустой список.
  // Если коллекция пустая, то возможно было пустой файл, а так сразу ясно, что что-то не так
  //   но появляние ошибки растянуто в времени.
  private final ImmutableList<String> CONTENT;
  private final Integer COUNT_WORDS;  // Нужно будет для генератора случайных числел
      //   но хранится будет здесь.
  // Индексты тоже сделать такими же
  private final ImmutableList<String> SORTED_WORDS_ALPH;
  private final ImmutableMultimap<String, String> WORDS_TRANSLATES;
  private final ImmutableMultimap<String, String> WORDS_CONTENT;


  // Получить слово по индексу. Нужно для генератора случайных чисел.
  // 0 - CountWords
  //
  // Если использовать Optional, то нужно где-то все равно переходить к исключениям.
  public String getWordByIdx(Integer idx) throws VParserException {
    // Проверяем границы.
    // Вычитать 1 нужно, так как нумерация с нуля.
    if (!(idx < 0 || idx > COUNT_WORDS-1)) {
       return SORTED_WORDS_ALPH.get(idx);
    } else {
      throw new VParserException("Out of range.");
    }
  }

  public static void main(String[] args) {
    try {
      String fullFilename = Joiner.on(AppConstants.PATH_SPLITTER).join("statistic-data", "vocabularity-folded.txt");
      Utils.print(ImmutableBECParser.getInstance(fullFilename).get().getWordByIdx(1110));
    } catch (VParserException e) {
      Utils.print(e.getMessage());
    } catch (IllegalStateException e) {
      Utils.print(e.getMessage());
    }
  }
}
