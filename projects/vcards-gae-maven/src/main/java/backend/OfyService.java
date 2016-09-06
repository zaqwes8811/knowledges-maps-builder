package backend;

// Run outside 'normal'
// https://groups.google.com/forum/#!topic/objectify-appengine/fZltoWFwbrs

import com.google.common.base.Optional;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;

import java.util.List;

public class OfyService {
  static {
//    factory().register(GoogleTranslatorRecord.class);
    factory().register(KindPage.class);
    factory().register(KindGenerator.class);
    factory().register(UserKind.class);
    factory().register(KindDictionary.class);
    //factory().register(DistributionGen.class);  // интерфейс не регистрируетс
  }

  public static Objectify ofy() {
    //ObjectifyService.run()
    return ObjectifyService.ofy();//begin();
  }

  public static ObjectifyFactory factory() {
    return ObjectifyService.factory();
  }
  
  public static void clearStore() {
  	ofy().delete().keys(ofy().load().type(KindPage.class).keys()).now();
  	ofy().delete().keys(ofy().load().type(KindGenerator.class).keys()).now();
  	ofy().delete().keys(ofy().load().type(UserKind.class).keys()).now();
//    ofy().delete().keys(ofy().load().type(GoogleTranslatorRecord.class).keys()).now();
    ofy().delete().keys(ofy().load().type(KindDictionary.class).keys()).now();
  }

  // generic not work
  //public static <T> Optional<T> getPageKind(
    public static Optional<KindDictionary> getPageKind(
            String pageName) {
        List<KindDictionary> kinds =
                OfyService.ofy().transactionless().load().type(KindDictionary.class)
                        .filter("name = ", pageName)
                        .list();

        if (kinds.size() > 1) {
            throw new StoreIsCorruptedException();
        }

        if (kinds.size() == 0)
            return Optional.absent();

        return Optional.of(kinds.get(0));
    }


    	/*
	// FIXME: Dev server
   You're right, as usual. My current
   Test Case says INFO: Local Datastore initialized: Type:
   Master/Slave Storage: In-memory if i change the configuration with
   LocalDatastoreServiceTestConfig config =
   new LocalDatastoreServiceTestConfig().setDefaultHighRepJobPolicyUnappliedJobPercenta‌​ge(100); says INFO:
   Local Datastore initialized: Type: High Replication Storage: In-memory But all test fails :( –  Fuzzo Feb 7 '13 at 14:31
   * */


    // FIXME: create-if-not-exist
    // как создать и получить информационный объект
    // нужен кроссгрупповой запрос в транзакции
    // http://stackoverflow.com/questions/22362192/create-or-err-with-objectify
    //
    // https://groups.google.com/forum/#!topic/objectify-appengine/Xt7HJMppcZ4
    // The username must be the primary key of the entity
    // https://sites.google.com/site/io/under-the-covers-of-the-google-app-engine-datastore
    //
    //
    //На локальной машине, либо с первого раза, либо никогда - on GAE - хз
    // срабатывает либо быстро, либо очень долго, так что ждем немного
    // https://groups.google.com/forum/#!msg/objectify-appengine/p4UylG6jTwU/qIT8sqrPBokJ
    // FIXME: куча проблем с удалением и консистентностью
    // http://stackoverflow.com/questions/14651998/objects-not-saving-using-objectify-and-storeAccessManager
    // Но как обрабатываются ошибки?
    // now не всегда работает
    //
    // eventually consistent:
    //   http://habrahabr.ru/post/100891/
    //   https://www.udacity.com/course/viewer#!/c-ud859/l-1219418587/m-1497718612
    //
    // Transactions
    //   http://stackoverflow.com/questions/14730601/how-to-enable-objectify-xa-transaction
    //   https://code.google.com/p/objectify-appengine/wiki/Transactions
    //   https://groups.google.com/forum/#!topic/objectify-appengine/UqxDRRXJMJ8
    // xg on eclipse http://stackoverflow.com/questions/10453035/google-app-engine-hrd-not-working-in-eclipse-development-environment
    //
    // Это бесполезно. Тут должно конечное приложение обеспечивать.
    //private static int TIME_STEP_MS = 200;
    //private static int COUNT_TRIES = 12;
    //
    // Nontransactional (non-ancestor) store_specific -
    //
    // Strong consistency:
    //   https://cloud.google.com/appengine/docs/java/datastore/structuring_for_strong_consistency
    //   "Queries inside transactions must include ancestor filters"

    // FIXME: вообще, то что читаю в цикле мало что значит в многопользовательском режиме
    //   для исследования возможно так и нужно, но вообще нет.

    // FIXME: make синхронизирующий вызов
    //
    // "This approach achieves strong consistency by writing to a single entity group per guestbook, but it also
    // limits changes to the guestbook to no more than 1 write per second (the supported limit for entity groups)."
    //
    // Вобщем если что-то включить в EG то писать можно будет только раз в секунду - сохранять например.
    //@Deprecated
    public static int COUNT_REPEATS = 3;

    // r/w limit 1Mb? Or?
    // http://stackoverflow.com/questions/9127982/avoiding-memcache-1m-limit-of-values
    // http://stackoverflow.com/questions/5522804/1mb-quota-limit-for-a-blobstore-object-in-google-app-engine
    // FIXME: may store in blob store but how access to it?
    public static long LIMIT_DATA_STORE_SIZE = 1000000;  // bytes
}