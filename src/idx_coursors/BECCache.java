package idx_coursors;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import common.Util;
import crosscuttings.AppConfigurator;
import crosscuttings.AppConstants;
import crosscuttings.CrosscuttingsException;
//import idx_coursors.IdxAccessor;

import java.util.List;

// @Immutable
//
// TODO(zaqwes) TOTH: Вот словарь это индекс или фильтр?
public class BECCache {
  /*
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
    final String splitter = AppConstants.PATH_SPLITTER;
    final String pathToAppFolder = AppConfigurator.getPathToAppFolder();

    final String pathToNode = Joiner.on(AppConstants.PATH_SPLITTER)
      .join(
        pathToAppFolder,
        nodeName);

    SENTENCES_PTRS = IdxAccessor.getSentencesKeys(
        Joiner.on(splitter).join(pathToNode, AppConstants.FILENAME_SENTENCES_IDX));
    IDX_SORTED = IdxAccessor.getSortedIdx(
        Joiner.on(splitter).join(pathToNode, AppConstants.SORTED_IDX_FILENAME));
    COUNT_WORDS = IDX_SORTED.get().size();
  }

  // Похоже тут violate не нужно.
  private final Optional<ImmutableList<String>> IDX_SORTED;
  private final Optional<ImmutableMap<String, List<Integer>>> SENTENCES_PTRS;
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

  //private List<Integer>
  //public List<String> getAssociatedContent(String key) {

  //}

  public Integer getSizeIndexes() {
    return COUNT_WORDS;
  }  */
}
