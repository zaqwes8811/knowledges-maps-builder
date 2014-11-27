package gae_store_space;

import com.google.common.base.Optional;
import com.googlecode.objectify.Key;
import pipeline.TextPipeline;

import java.util.Set;

public class PageBuilder {
  public static TextPipeline buildPipeline() {
    return new TextPipeline();
  }

  private static void checkNonEmpty(Set<Key<PageKind>> keys) {
    if (keys.isEmpty())
      throw new AssertionError();
  }

  // Транзакцией сделать нельзя - поиск это сразу больше 5 EG
  // Да кажется можно, просто не ясно зачем
  // DANGER: если не удача всегда! кидается исключение, это не дает загрузиться кешу!
  public static Optional<PageFrontend> restore(String pageName, Set<Key<PageKind>> keys) {
    checkNonEmpty(keys);
    Optional<PageFrontend> r = Optional.absent();
    try {
      // Load page data from store
      Optional<PageKind> rawPage = PageKind.getPageKind(pageName, keys);

      // Conditional processing raw page
      //if (true)
      {
        if (rawPage.isPresent()) {
          PageKind p = rawPage.get();
          PageFrontend tmp = buildPipeline().pass(p.name, p.rawSource);

          PageFrontend frontend = PageFrontend.buildEmpty();

          frontend.assign(tmp);
          GeneratorKind g = GeneratorKind.restoreById(p.generator.getId()).get();
          frontend.setGeneratorCache(g);
          frontend.set(p);

          r = Optional.of(frontend);
        }
      }

      return r;
    } catch (StoreIsCorruptedException ex) {
      throw new RuntimeException(ex.getCause());
    }
  }

}
