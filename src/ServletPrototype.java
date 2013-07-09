import caches.BECCache;
import com.google.common.base.Optional;
import common.Util;
import common.math.Randomizer;
import common.math.Randomizers;

import java.util.Random;

public class ServletPrototype {
  public static void main(String[] args) {

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
}
