import caches.BECCache;
import com.google.common.base.Optional;
import common.Util;
import common.math.Randomizer;
import common.math.Randomizers;
import parsers.VParserException;

import java.util.*;

public class ServletPrototype {
  public static void main(String[] args) throws VParserException {
    Optional<FormerPackagesForWeb> generator = FormerPackagesForWeb.create();
    Util.print(generator.get().getPackage());
  }
}

// Пакет - N уникальных слов с контекстом и переводом.
class FormerPackagesForWeb {
  private FormerPackagesForWeb(Randomizer randomizer, BECCache cash) {
    RANDOMIZER = randomizer;
    CASH = cash;
  }

  private final Randomizer RANDOMIZER;
  private final BECCache CASH;

  private final Integer COUNT_RECORDS_IN_PACKAGE = 6;

  public static Optional<FormerPackagesForWeb> create() {
    Optional<BECCache> cash = BECCache.create();
    Optional<FormerPackagesForWeb> instance = Optional.absent();
    if (cash.isPresent()) {
      Randomizer randomizer = Randomizers.create(cash.get().getSizeIndexes());
      instance = Optional.of(new FormerPackagesForWeb(randomizer, cash.get()));
    } else {
      Util.log("Cache is absent.");
    }
    return instance;
  }

  public Map<String, String> getPackage() throws VParserException {
    Set<String> keys = new HashSet<String>();
    int preserveVar = 0;
    while (true) {
      if (keys.size() == COUNT_RECORDS_IN_PACKAGE) break;
      int index = RANDOMIZER.getSample();
      String key = CASH.getWordByIdx(index);
      keys.add(key);
      ++preserveVar;
      if (preserveVar > 100) break;
    }
    Map<String, String> result = new HashMap<String, String>();
    for (final String key: keys) {
      result.put(key, key);
    }
    return result;
  }
}
