package caches;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import common.Util;
import crosscuttings.AppConfigurator;
import crosscuttings.AppConstants;
import crosscuttings.CrosscuttingsException;
import idx_coursors.IdxAccessor;

// @Immutable
//
// TODO(zaqwes) TOTH: Вот словарь это индекс или фильтр?
public class BECCache {
  public static Optional<BECCache> create() {
    Optional<BECCache> instance = Optional.absent();
    try {
      instance = Optional.of(new BECCache());
    } catch (CrosscuttingsException e) {
      Util.log(e.getMessage());
    }
    return instance;
  }

  // Да, лучше передать, тогда будет Стратегией?
  private BECCache() throws CrosscuttingsException {
    String nodeName = "bec-node";
    final String pathToAppFolder = AppConfigurator.getPathToAppFolder();

    String filename = Joiner.on(AppConstants.PATH_SPLITTER)
      .join(
          pathToAppFolder,
          nodeName,
          AppConstants.SORTED_IDX_FILENAME);

    IDX_SORTED = IdxAccessor.getSortedIdx(filename);
    COUNT_WORDS = IDX_SORTED.get().size();
  }

  // Похоже тут violate не нужно.
  private final Optional<ImmutableList<String>> IDX_SORTED;
  private final Integer COUNT_WORDS;

  // Получить слово по индексу. Нужно для генератора случайных чисел.
  // 0 - CountWords
  //
  // Если использовать Optional, то нужно где-то все равно переходить к исключениям.
  public String getWordByIdx(Integer idx) throws VParserException {
    // Проверяем границы.
    // Вычитать 1 нужно, так как нумерация с нуля.
    if (!(idx < 0 || idx > COUNT_WORDS-1)) {
      return IDX_SORTED.get().get(idx);
    } else {
      throw new VParserException("Out of range.");
    }
  }

  public Integer getSizeIndexes() {
    return COUNT_WORDS;
  }
}
