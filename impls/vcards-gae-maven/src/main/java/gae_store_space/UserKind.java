package gae_store_space;

import com.google.common.base.Optional;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Work;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Ignore;
import com.googlecode.objectify.annotation.Serialize;
import cross_cuttings_layer.GlobalIO;
import instances.AppInstance;
import org.apache.log4j.Logger;
import web_relays.protocols.PageSummaryValue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import static gae_store_space.OfyService.ofy;

// Must be full thread-safe
//
// Очень важен - попробую им гарантировать согласованность
@Entity
public class UserKind {
  private static Logger log = Logger.getLogger(UserKind.class.getName());
  //private  // FIXME:
  public UserKind() { }

  // FIXME: external lock
  // http://stackoverflow.com/questions/13197756/synchronized-method-calls-itself-recursively-is-this-broken

  // user is unique! can't do that with pages!
  @Id private String id;
  @Serialize private Set<String> pageNamesRegister;
  // FIXME: keys for pages!
  Set<Key<PageKind>> pageKeys = new HashSet<>();
  // FIXME: keys for filters!

  // FIXME: если кеш убрать работает много стабильнее
  private static final Integer CACHE_SIZE = 5;
  @Ignore
  LoadingCache<String, Optional<PageKind>> pagesCache = CacheBuilder.newBuilder()
    .maximumSize(CACHE_SIZE)
    .build(
      new CacheLoader<String, Optional<PageKind>>() {
        @Override
        public Optional<PageKind> load(String key) { return PageKind.restore(key); }
      });

  @Ignore
  GAEStoreAccessManager store = new GAEStoreAccessManager();

  public String getId() {
    return id;
  }

  private void checkPersisted() {
    if (id == null)
      throw new IllegalStateException();
  }

  private void reset() {
    if (!Optional.fromNullable(pageNamesRegister).isPresent())
      pageNamesRegister = new HashSet<>();
  }

  public static UserKind createOrRestoreById(final String id) {
    UserKind r = ofy().transact(new Work<UserKind>() {
      @Override
      public UserKind run() {
        UserKind th = ofy().load().type(UserKind.class).id(id).now();
        if (th == null) {
          th = new UserKind();
          th.id = id;
          ofy().save().entity(th);
        }
        return th;
      }
    });

    r.checkPersisted();  // Это должно быть здесь
    r.reset();
    return r;
  }

  private void checkPageName(String pageName) {
    if (pageName == null)
      throw new IllegalArgumentException();
  }

  public synchronized boolean tryPushPage(String pageName) {
    checkPageName(pageName);
    boolean wasInserted = false;
    if (!pageNamesRegister.contains(pageName)) {
      pageNamesRegister.add(pageName);
      wasInserted = true;
    }
    return wasInserted;
  }

  public synchronized boolean removePage(String pageName) {
    checkPageName(pageName);
    return pageNamesRegister.remove(pageName);
  }

  public synchronized boolean isContain(String pageName) {
    checkPageName(pageName);
    return pageNamesRegister.contains(pageName);
  }

  // скорее исследовательский метод
  // https://code.google.com/p/objectify-appengine/wiki/Transactions
  // FIXME: вот тут важна транзактивность
  public void createOrReplacePage(String name, String text) {
    // check register

    // check key set

    // create and persist - store access

    // finally rollback if failure - in memory simple rollback

    Optional<PageKind> page = getPage(name);
    if (page.isPresent())
      page.get().deleteFromStore_strong();

    pagesCache.invalidate(name);

    PageKind.createPageIfNotExist_eventually(name, text);
  }

  public PageKind getPagePure(String pageName) {
    // check register

    return getPage(pageName).get();
  }

  // FIXME: may be non thread safe. Да вроде бы должно быть база то потокобезопасная?
  private Optional<PageKind> getPage(String pageName) {
    try {
      return pagesCache.get(pageName);
    } catch (ExecutionException e) {
      throw new RuntimeException(e);
    }
  }

  public void createDefaultPage() {
    pagesCache.cleanUp();
    String name = AppInstance.defaultPageName;
    String text = GlobalIO.getGetPlainTextFromFile(AppInstance.getTestFileName());
    PageKind.createPageIfNotExist_eventually(name, text);
  }

  public List<PageSummaryValue> getUserInformation() {
    List<PageKind> pages = store.getAllPages_eventually();

    List<PageSummaryValue> r = new ArrayList<>();
    for (PageKind page: pages)
      r.add(PageSummaryValue.create(page.getName(), page.getGenNames()));

    return r;
  }
}
