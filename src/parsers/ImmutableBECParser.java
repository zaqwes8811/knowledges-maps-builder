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
  // TODO(zaqwes) TOTH: Кажется синхронизация не нужна.
  public static ImmutableBECParser create(ImmutableList<String> fileContent) {
     return new ImmutableBECParser(fileContent);
  }

  // Данные для парсера передаем извне. Чтение внутри конструктора сомнительно очень.
  private ImmutableBECParser(ImmutableList<String> fileContent) {

    // Какие исключения могут генерировать списки, мапы,...
    CONTENT = ImmutableList.copyOf(fileContent);

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

  public static void main(String[] args) {
    try {
      String fullFilename = Joiner.on(AppConstants.PATH_SPLITTER).join("statistic-data", "vocabularity-folded.txt");
      ImmutableList<String> content = Utils.file2list(fullFilename);
      ImmutableBECParser cash = ImmutableBECParser.create(content);

    } catch (IOException e) {
      Utils.print(e.getMessage());
    //} catch (VParserException e) {
    //  Utils.print(e.getMessage());
    } catch (IllegalStateException e) {
      Utils.print(e.getMessage());
    }
  }
}
