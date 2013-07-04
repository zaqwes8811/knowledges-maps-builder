package parsers;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import common.utils;
import crosscuttings.AppConstants;

import java.util.List;

public class ImmutableBECParser {
  private static final Optional<List<String>> CONTENT;
  static {
    CONTENT = utils.file2list(
        Joiner.on(AppConstants.PATH_SPLITTER)
          .join(
              "statistic-data",
              "vocabularity-folded.txt"));
  }

  public static void main(String[] args) {
    if (CONTENT.isPresent()) {
      for (String record: CONTENT.get()) {
        utils.print(Splitter.on("@").split(record));
      }
    } else {
      utils.print("Can't read file");
    }
  }
}
