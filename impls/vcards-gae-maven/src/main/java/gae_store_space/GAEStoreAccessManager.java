package gae_store_space;

import com.google.common.base.Optional;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.LoadResult;
import com.googlecode.objectify.VoidWork;
import com.googlecode.objectify.Work;
import com.googlecode.objectify.cmd.Query;

import java.util.List;

import static gae_store_space.OfyService.ofy;

public class GAEStoreAccessManager {
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
  private static int COUNT_REPEATS = 3;

	// r/w limit 1Mb? Or?
	// http://stackoverflow.com/questions/9127982/avoiding-memcache-1m-limit-of-values
	// http://stackoverflow.com/questions/5522804/1mb-quota-limit-for-a-blobstore-object-in-google-app-engine
	// FIXME: may store in blob store but how access to it?
	public static long LIMIT_DATA_STORE_SIZE = 1000000;  // bytes
	
	public void asyncPersist(PageKind kind) {
		ofy().save().entity(kind).now();
	}
	
	public void asyncPersist(GeneratorKind kind) {
		ofy().save().entity(kind).now();
	}
	
	public void asyncDeleteGenerators(List<Key<GeneratorKind>> generators) {
		ofy().delete().keys(generators).now();
	}
	
	public Optional<GeneratorKind> restoreGenerator_eventually(Key<GeneratorKind> g) {
		return GeneratorKind.restoreById(g.getId());
	}
	
	// FIXME: можно прочитать только ключи, а потом делать выборки
	// FIXME: bad design
	public PageKind restorePageByName(String name) {
   	List<PageKind> pages = 
   			ofy().transactionless().load().type(PageKind.class).filter("name = ", name).list();
 		
 		if (pages.size() > 1)
 			throw new StoreIsCorruptedException();
 		
		if (pages.size() == 0)
			throw new IllegalStateException();	//return Optional.absent();
		 
		return pages.get(0);
	}
	
	public List<PageKind> getPagesByName_eventually(String name) {
		return ofy().transactionless().load().type(PageKind.class).filter("name = ", name).list();
	}
	
	public List<PageKind> getAllPages_eventually() {
		return ofy().load().type(PageKind.class).list();
	}
	
	
	public PageKind firstPersist(Work<PageKind> work) {
		return ofy().transactNew(COUNT_REPEATS, work);
	}
	
	public void transact(VoidWork work) {
		ofy().transactNew(COUNT_REPEATS, work);
	}
}
