package gae_store_space;

import com.google.common.base.Optional;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Work;
import com.googlecode.objectify.annotation.Ignore;
import cross_cuttings_layer.GlobalIO;
import instances.AppInstance;
import org.apache.log4j.Logger;
import org.javatuples.Pair;
import web_relays.protocols.PageSummaryValue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static gae_store_space.OfyService.ofy;

public class UserFrontend {
  private static Logger log = Logger.getLogger(UserKind.class.getName());

  UserFrontend() {}

  private UserKind kind = null;

  private UserKind get() {
    return Optional.fromNullable(kind).get();
  }

  public static UserFrontend createOrRestoreById(String id) {
    UserKind kind = UserKind.createOrRestoreById(id);
    UserFrontend frontend = new UserFrontend();
    frontend.kind = kind;
    frontend.reset();
    return frontend;
  }

  // Frontend

  // FIXME: если кеш убрать работает много стабильнее
  private static final Integer CACHE_SIZE = 5;
  @Ignore
  LoadingCache<String, Optional<PageKind>> pagesCache = null;

  private void reset() {
    if (pagesCache == null)
      pagesCache = CacheBuilder.newBuilder()
        .maximumSize(CACHE_SIZE)
        .build(
          new CacheLoader<String, Optional<PageKind>>() {
            @Override
            public Optional<PageKind> load(String key) {
              //pageKeys;
              return PageKind.restore(key, get().getPageKeys());
            }
          });
  }

  public void clear() {
    get().getPageNamesRegister().clear();
    get().getPageKeys().clear();  // FIXME: may be leak
  }

  private void checkPageName(String pageName) {
    if (pageName == null)
      throw new IllegalArgumentException();
  }

  private boolean removePage(String pageName) {
    checkPageName(pageName);
    return get().getPageNamesRegister().remove(pageName);
  }

  private boolean isContain(String pageName) {
    checkPageName(pageName);
    return get().getPageNamesRegister().contains(pageName);
  }

  private void checkPagesInvariant() {
    if (get().getPageNamesRegister().size() != get().getPageKeys().size()) {
      log.info(get().getPageNamesRegister());
      log.info(get().getPageKeys());
      throw new AssertionError();
    }
  }

  private void checkTrue(boolean value) {
    if (!value)
      throw new AssertionError();
  }

  // скорее исследовательский метод
  // https://code.google.com/p/objectify-appengine/wiki/Transactions
  // FIXME: вот тут важна транзактивность
  public synchronized PageKind replacePage(String pageName, String text) {
    // check register
    if (isContain(pageName)) {
      // страница была сохранена до этого
      PageKind page = getPage(pageName).get();
      removePage(pageName);
      page.atomicDelete();

      // нужно как-то удалить ключ
      checkTrue(get().getPageKeys().remove(Key.create(page)));

      pagesCache.invalidate(pageName);
    }

    // Нужно чтобы ни в памяти, ни в хранилище не было пар!
    // это проверка только из памяти!!
    checkNotContain(pageName);
    checkPagesInvariant();

    boolean success = false;
    try {
      Pair<PageKind, GeneratorKind> pair = PageKind.process(pageName, text);
      final PageKind page = pair.getValue0();
      final GeneratorKind g = pair.getValue1();
      final UserKind user = get();

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
          user.pageKeys.add(key);  // FIXME: а откатит ли? думаю нет

          // need to save user!
          ofy().save().entity(user).now();
          return page;
        }
      };

      get().getPageNamesRegister().add(pageName);
      // FIXME: база данный в каком состоянии будет тут? согласованном?
      // check here, but what can do?
      PageKind r = ofy().transactNew(GAEStoreAccessManager.COUNT_REPEATS, work);
      checkPagesInvariant();

      success = true;
      return r;
    } finally {
      if (!success) {
        //pageKeys.remove()
        get().getPageNamesRegister().remove(pageName);
      }

      checkPagesInvariant();
    }
  }

  public synchronized PageKind getPagePure(String pageName) {
    // check register
    Optional<PageKind> r = getPage(pageName);
    if (!r.isPresent())
      throw new IllegalArgumentException();

    return r.get();
  }

  private void checkPageIsActive(Optional<PageKind> o) {
    if (!o.isPresent())
      throw new AssertionError();
  }

  // FIXME: may be non thread safe. Да вроде бы должно быть база то потокобезопасная?
  private Optional<PageKind> getPage(String pageName) {
    if (!isContain(pageName)) {
      return Optional.absent();
    }

    Optional<PageKind> r = Optional.absent();
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
    for (String page: get().getPageNamesRegister())
      r.add(PageSummaryValue.create(page, AppInstance.defaultGeneratorName));

    return r;
  }
}
