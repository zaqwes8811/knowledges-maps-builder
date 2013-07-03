package parsers;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import common.utils;
import crosscuttings.AppConstants;

import java.util.List;

public class BECParser {


  public static void main(String[] args) {
    String filename = "vocabularity-folded.txt";
    String path = "statistic-data";
    Optional<List<String>> content =
        utils.file2list(
            Joiner.on(AppConstants.PATH_SPLITTER)
                .join(path, filename));

    if (content.isPresent()) {
      for (String record: content.get()) {
       utils.print(Splitter.on("@").split(record));
      }
    } else {
      utils.print("Can't read file");
    }
  }
}
