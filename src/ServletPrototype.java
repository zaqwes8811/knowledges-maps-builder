import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import common.Utils;
import crosscuttings.AppConstants;
import parsers.ImmutableBECParser;

import java.io.IOException;
import java.util.List;

public class ServletPrototype {
    public static void main(String[] args) {

      // Читаем файл с сформатированными данными.
      // word@content item1@content item2
      // перевода пока нет
      String fullBECFilename =
          Joiner.on(AppConstants.PATH_SPLITTER).join("statistic-data", "vocabularity-folded.txt");
      try {
        ImmutableList<String> content = Utils.file2list(fullBECFilename);
        // Парсим
        ImmutableBECParser parser = ImmutableBECParser.create(content);
        // Инициализируем генератор случайных чисел.

        // Передаем извлекателю

      } catch (IOException e) {
        // Нужно как-то оповестить пользователя.
        Utils.print(e.getMessage());
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
