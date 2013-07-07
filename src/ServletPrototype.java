import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import common.Util;
import crosscuttings.AppConstants;
import parsers.ImmutableBECParser;

import java.io.IOException;
import java.util.List;

public class ServletPrototype {
    public static void main(String[] args) {

      // TODO(zaqwes): Сделать класс экстактор данных. Или аксцессор к базе знаний.
      //   Тогде класс будет не парсером, а как бы кэшем. Так и правильнее для дальнейшего расширения.
      // Читаем файл с сформатированными данными.
      // word@content item1@content item2
      // перевода пока нет
      String fullBECFilename =
          Joiner.on(AppConstants.PATH_SPLITTER).join("statistic-data", "vocabularity-folded.txt");
      try {
        ImmutableList<String> content = Util.file2list(fullBECFilename);
        // Парсим
        ImmutableBECParser parser = ImmutableBECParser.create(content);
        // Инициализируем генератор случайных чисел.

        // Передаем извлекателю

      } catch (IOException e) {
        // Нужно как-то оповестить пользователя.
        Util.print(e.getMessage());
      }
    }
}

interface Randomizer {
   Integer getSample();
}

class Randomizers {
  private Randomizers() {}

  public static Randomizer create(List<Integer> ox) {
    return new UniformRandomizer();
  }

  private static class UniformRandomizer implements Randomizer {
     @Override
     public Integer getSample() {
        return 0;
     }
  }

}
