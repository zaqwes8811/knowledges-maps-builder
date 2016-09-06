package http_api;

import backend.*;
import com.google.common.base.Optional;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Work;
import net.jcip.annotations.GuardedBy;
import org.apache.log4j.Logger;
import org.javatuples.Pair;
import backend.ValuePipeline;
import backend.TextPipeline;
import backend.math.DistributionElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class UserAccessor
{
    // State
    private static final Integer CACHE_SIZE = 5;
    private @GuardedBy("this")
    LoadingCache<String, Optional<PageWrapper>> pagesCache = null;
    private @GuardedBy("this")
    UserKind kind = null;

    // Services
    private static Logger log = Logger.getLogger(UserKind.class.getName());

    // Actions
    UserAccessor() {}

    public UserKind getUserKind() {
        return Optional.fromNullable(kind).get();
    }

    public static UserAccessor createOrRestoreById(String id) {
        UserAccessor frontend = new UserAccessor();
        frontend.kind = UserKind.createOrRestoreById(id);;
        frontend.initPageCache();
        return frontend;
    }

    // Транзакцией сделать нельзя - поиск это сразу больше 5 EG
    // Да кажется можно, просто не ясно зачем
    // DANGER: если не удача всегда! кидается исключение, это не дает загрузиться кешу!
    public static Optional<PageWrapper> restore(String pageName, Set<Key<KindPage>> keys)
    {
        if (keys.isEmpty())
            throw new AssertionError();

        Optional<PageWrapper> r = Optional.absent();
        try {
            // Load page data from store
            Optional<KindPage> rawPage = KindPage.getPageKind(pageName, keys);

            if (rawPage.isPresent()) {
                KindPage p = rawPage.get();
                ValuePipeline tmp = new TextPipeline().pass(p.getName(), p.getRawSource());
                PageWrapperBounded frontend = PageWrapperBounded.buildEmpty();

                frontend.assign(
                        new PageWrapperBounded(tmp.PAGE_NAME, tmp.SOURCE, tmp.SENTENCES_KINDS, tmp.UNIGRAMS));

                KindGenerator g = KindGenerator.restoreById(p.getGeneratorId()).get();
                frontend.setGeneratorCache(g);
                frontend.set(p);

                PageWrapper tmp0 = frontend;  // FIXME: polym. doesn't work
                r = Optional.of(tmp0);
            }

            return r;
        } catch (StoreIsCorruptedException ex) {
            throw new RuntimeException(ex.getCause());
        }
    }

    private void initPageCache()
    {
        if (pagesCache == null){
          pagesCache = CacheBuilder.newBuilder()
            .maximumSize(CACHE_SIZE)
            .build(
              new CacheLoader<String, Optional<PageWrapper>>() {
                @Override
                public Optional<PageWrapper> load(String key) {
                  return restore(key, getUserKind().getPageKeys());
                }
              });
        }
    }

    public static Pair<KindPage, KindGenerator> process(String name, String text) {
        if (text.length() > OfyService.LIMIT_DATA_STORE_SIZE)
            throw new IllegalArgumentException();

        // FIXME: need processing but only for fill generator
        ValuePipeline r = new TextPipeline().pass(name, text);
        PageWrapper page =
            new PageWrapperBounded(r.PAGE_NAME, r.SOURCE, r.SENTENCES_KINDS, r.UNIGRAMS);
        KindPage raw = page.getRawPage();

        // FIXME: how to know object size - need todo it
        if (raw.getPageByteSize() > OfyService.LIMIT_DATA_STORE_SIZE)
            throw new IllegalArgumentException();

        ArrayList<DistributionElement> d = page.buildImportanceDistribution();
        return Pair.with(raw, KindGenerator.create(d));
    }

    // скорее исследовательский метод
    // https://code.google.com/p/objectify-appengine/wiki/Transactions
    // FIXME: вот тут важна транзактивность
    public synchronized KindPage createOrReplacePage(String pageName, String text)
    {
        // check register
        if (getUserKind().isContain(pageName)) {
            // страница была сохранена до этого
            PageWrapper page = getPage(pageName).get();
            getUserKind().removePage(pageName);
            page.atomicDeleteRawPage();

            KindPage rawPage = page.getRawPage();

            // нужно как-то удалить ключ
            boolean res = getUserKind().getPageKeys().remove(Key.create(rawPage));

            pagesCache.invalidate(pageName);
        }

        // Нужно чтобы ни в памяти, ни в хранилище не было пар!
        // это проверка только из памяти!!
        getUserKind().checkPagesInvariant();

        boolean success = false;
        try {
            Pair<KindPage, KindGenerator> pair = process(pageName, text);
            final KindPage page = pair.getValue0();
            final KindGenerator g = pair.getValue1();
            final UserKind user = getUserKind();

            // transaction boundary
            Work<KindPage> work = new Work<KindPage>() {
              @Override
              public KindPage run() {
                OfyService.ofy().save().entity(g).now();

                // нельзя не сохраненны присоединять - поэтому нельзя восп. сущ. методом
                page.setGenerator(g);

                OfyService.ofy().save().entity(page).now();

                // can add key
                Key<KindPage> key = Key.create(page);
                // FIXME: а откатит ли? думаю нет
                // FIXME: а если ключ уже был? Не может быть - создаем с нуля
                user.getPageKeys().add(key);

                // need to save user!
                OfyService.ofy().save().entity(user).now();
                return page;
              }
            };

            getUserKind().pageNamesRegister.add(pageName);
            // FIXME: база данный в каком состоянии будет тут? согласованном?
            // check here, but what can do?
            KindPage r = OfyService.ofy().transactNew(OfyService.COUNT_REPEATS, work);

            getUserKind().checkPagesInvariant();

            success = true;
            return r;
        } finally {
            if (!success) {
              // FIXME:
              //pageKeys.remove()
              getUserKind().pageNamesRegister.remove(pageName);
            }
            getUserKind().checkPagesInvariant();
        }
    }

    public synchronized PageWrapper getPagePure(String pageName) {
      // check register
      Optional<PageWrapper> r = getPage(pageName);
      if (!r.isPresent())
        throw new IllegalArgumentException();

      return r.get();
    }

    private void checkPageIsActive(Optional<PageWrapper> o) {
      if (!o.isPresent())
        throw new AssertionError();
    }

    // FIXME: may be non thread safe. Да вроде бы должно быть база то потокобезопасная?
    private Optional<PageWrapper> getPage(String pageName) {
      if (!getUserKind().isContain(pageName)) {
        return Optional.absent();
      }

      Optional<PageWrapper> r = Optional.absent();
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
        createOrReplacePage(pageName, text);
    }

    public List<ValuePageSummary> getUserInformation() {
      List<ValuePageSummary> r = new ArrayList<>();
      for (String page: getUserKind().pageNamesRegister)
        r.add(ValuePageSummary.create(page, AppInstance.defaultGeneratorName));

      return r;
    }
}
