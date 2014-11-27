package gae_store_space;

import com.google.common.base.Optional;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Work;
import cross_cuttings_layer.GlobalIO;
import instances.AppInstance;
import net.jcip.annotations.GuardedBy;
import org.apache.log4j.Logger;
import org.javatuples.Pair;
import pages.PageBuilder;
import pages.PageFrontend;
import pages.PageWithBoundary;
import pipeline.math.DistributionElement;
import web_relays.protocols.PageSummaryValue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static gae_store_space.OfyService.ofy;

public class UserFrontend {
  // State
  private static final Integer CACHE_SIZE = 5;
  private @GuardedBy("this") LoadingCache<String, Optional<PageFrontend>> pagesCache = null;
  private @GuardedBy("this") UserKind kind = null;

  // Services
  private static Logger log = Logger.getLogger(UserKind.class.getName());

  // Actions
  UserFrontend() {}
  private UserKind getRaw() {
    return Optional.fromNullable(kind).get();
  }

  public static UserFrontend createOrRestoreById(String id) {
    UserKind kind = UserKind.createOrRestoreById(id);
    UserFrontend frontend = new UserFrontend();
    frontend.kind = kind;
    frontend.reset();
    return frontend;
  }

  private void reset() {
    if (pagesCache == null)
      pagesCache = CacheBuilder.newBuilder()
        .maximumSize(CACHE_SIZE)
        .build(
          new CacheLoader<String, Optional<PageFrontend>>() {
            @Override
            public Optional<PageFrontend> load(String key) {
              //pageKeys;
              return PageBuilder.restore(key, getRaw().getPageKeys());
            }
          });
  }

  // Удаляет только ключи, базу не трогает
  public void clear() {
    getRaw().getPageNamesRegister().clear();
    getRaw().getPageKeys().clear();  // FIXME: may be leak
  }

  private void checkPageName(String pageName) {
    if (pageName == null)
      throw new IllegalArgumentException();
  }

  private boolean removePage(String pageName) {
    checkPageName(pageName);
    return getRaw().getPageNamesRegister().remove(pageName);
  }

  private boolean isContain(String pageName) {
    checkPageName(pageName);
    return getRaw().getPageNamesRegister().contains(pageName);
  }

  private void checkPagesInvariant() {
    if (getRaw().getPageNamesRegister().size() != getRaw().getPageKeys().size()) {
      throw new AssertionError();
    }
  }

  private void checkTrue(boolean value) {
    if (!value)
      throw new AssertionError();
  }

  public static Pair<PageKind, GeneratorKind> process(String name, String text) {
    if (text.length() > GAEStoreAccessManager.LIMIT_DATA_STORE_SIZE)
      throw new IllegalArgumentException();

    // FIXME: need processing but only for fill generator
    PageWithBoundary page = PageBuilder.buildPipeline().pass(name, text);
    PageKind raw = page.getRawPage();

    // FIXME: how to know object size - need todo it
    if (raw.getPageByteSize() > GAEStoreAccessManager.LIMIT_DATA_STORE_SIZE)
      throw new IllegalArgumentException();

    ArrayList<DistributionElement> d = page.buildImportanceDistribution();
    return Pair.with(raw, GeneratorKind.create(d));
  }

  // скорее исследовательский метод
  // https://code.google.com/p/objectify-appengine/wiki/Transactions
  // FIXME: вот тут важна транзактивность
  public synchronized PageKind replacePage(String pageName, String text) {
    // check register
    if (isContain(pageName)) {
      // страница была сохранена до этого
      PageFrontend page = getPage(pageName).get();
      removePage(pageName);
      page.atomicDeleteRawPage();

      PageKind rawPage = page.getRawPage();

      // нужно как-то удалить ключ
      checkTrue(getRaw().getPageKeys().remove(Key.create(rawPage)));

      pagesCache.invalidate(pageName);
    }

    // Нужно чтобы ни в памяти, ни в хранилище не было пар!
    // это проверка только из памяти!!
    checkNotContain(pageName);
    checkPagesInvariant();

    boolean success = false;
    try {
      Pair<PageKind, GeneratorKind> pair = process(pageName, text);
      final PageKind page = pair.getValue0();
      final GeneratorKind g = pair.getValue1();
      final UserKind user = getRaw();

      // transaction boundary
      Work<PageKind> work = new Work<PageKind>() {
        @Override
        public PageKind run() {
          ofy().save().entity(g).now();

          // нельзя не сохраненны присоединять - поэтому нельзя восп. сущ. методом
          page.setGenerator(g);

          ofy().save().entity(page).now();

          // can add key
          Key<PageKind> key = Key.create(page);
          // FIXME: а откатит ли? думаю нет
          // FIXME: а если ключ уже был? Не может быть - создаем с нуля
          user.getPageKeys().add(key);

          // need to save user!
          ofy().save().entity(user).now();
          return page;
        }
      };

      getRaw().getPageNamesRegister().add(pageName);
      // FIXME: база данный в каком состоянии будет тут? согласованном?
      // check here, but what can do?
      PageKind r = ofy().transactNew(GAEStoreAccessManager.COUNT_REPEATS, work);

      checkPagesInvariant();

      success = true;
      return r;
    } finally {
      if (!success) {
        // FIXME:
        //pageKeys.remove()
        getRaw().getPageNamesRegister().remove(pageName);
      }

      checkPagesInvariant();
    }
  }

  public synchronized PageFrontend getPagePure(String pageName) {
    // check register
    Optional<PageFrontend> r = getPage(pageName);
    if (!r.isPresent())
      throw new IllegalArgumentException();

    return r.get();
  }

  private void checkPageIsActive(Optional<PageFrontend> o) {
    if (!o.isPresent())
      throw new AssertionError();
  }

  // FIXME: may be non thread safe. Да вроде бы должно быть база то потокобезопасная?
  private Optional<PageFrontend> getPage(String pageName) {
    if (!isContain(pageName)) {
      return Optional.absent();
    }

    Optional<PageFrontend> r = Optional.absent();
    // FIXME: danger but must work
    Integer countTries = 1000;
    while (true) {
      try {
        r = pagesCache.get(pageName);
        //} catch (UncheckedExecutionException ex) {

      } catch (ExecutionException ex) { }

      if (r.isPresent())
        break;

      // insert into cache but absent
      pagesCache.invalidate(pageName);
      countTries--;
      if (countTries < 0)
        throw new IllegalStateException(pageName);
    }

    checkPageIsActive(r);

    return r;
  }

  public synchronized void createDefaultPage() {
    String pageName = AppInstance.defaultPageName;
    String text = GlobalIO.getGetPlainTextFromFile(AppInstance.getTestFileName());
    replacePage(pageName, text);
  }

  private void checkNotContain(String pageName) {
    if (isContain(pageName))
      throw new AssertionError();
  }

  public List<PageSummaryValue> getUserInformation() {
    List<PageSummaryValue> r = new ArrayList<>();
    for (String page: getRaw().getPageNamesRegister())
      r.add(PageSummaryValue.create(page, AppInstance.defaultGeneratorName));

    return r;
  }
}
