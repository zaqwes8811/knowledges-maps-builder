package caches;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import common.Util;
import crosscuttings.AppConfigurator;
import crosscuttings.AppConstants;
import crosscuttings.CrosscuttingsException;
import idx_coursors.IdxAccessor;

//
public class UniformBECGetter {
  public static Optional<UniformBECGetter> create() {
    Optional<UniformBECGetter> instance = Optional.absent();
    try {
      instance = Optional.of(new UniformBECGetter());
    } catch (CrosscuttingsException e) {
      Util.log(e.getMessage());
    }
    return instance;
  }

  // Да, лучше передать, тогда будет Стратегией?
  private UniformBECGetter() throws CrosscuttingsException {
    String nodeName = "bec-node";
    final String pathToAppFolder = AppConfigurator.getPathToAppFolder();

    String filename = Joiner.on(AppConstants.PATH_SPLITTER)
      .join(
          pathToAppFolder,
          nodeName,
          AppConstants.SORTED_IDX_FILENAME);

    IDX_SORTED = IdxAccessor.getSortedIdx(filename);
  }

  // Похоже тут violate не нужно.
  private final Optional<ImmutableList<String>> IDX_SORTED;

  // Runner
  public static void main(String [] args) {

  }
}
