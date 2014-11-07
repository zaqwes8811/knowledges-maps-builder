package gae_store_space.queries;

import static gae_store_space.queries.OfyService.ofy;
import gae_store_space.GeneratorKind;
import gae_store_space.PageKind;

import java.util.List;

import com.google.common.base.Optional;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.VoidWork;
import com.googlecode.objectify.Work;

class StoreException {
	
}

public final class GAESpecific {
//На локальной машине, либо с первого раза, либо никогда - on GAE - хз
	// срабатывает либо быстро, либо очень долго, так что ждем немного
	// https://groups.google.com/forum/#!msg/objectify-appengine/p4UylG6jTwU/qIT8sqrPBokJ
	// FIXME: куча проблем с удалением и консистентностью
	// http://stackoverflow.com/questions/14651998/objects-not-saving-using-objectify-and-gae
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
	// Nontransactional (non-ancestor) queries - 
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
	
	public void asyncPersist(PageKind kind) {
		ofy().save().entity(kind).now();
	}
	
	public void asyncPersist(GeneratorKind kind) {
		ofy().save().entity(kind).now();
	}
	
	public void asyncDeleteGenerators(List<Key<GeneratorKind>> generators) {
		ofy().delete().keys(generators).now();
	}
	
	public Optional<GeneratorKind> restoreGenerator_evCons(Key<GeneratorKind> g) {
		Optional<GeneratorKind> r = 
				Optional.fromNullable(ofy().load().type(GeneratorKind.class).id(g.getId()).now());
		return r;
	}
	
	// FIXME: можно прочитать только ключи, а потом делать выборки
	// FIXME: bad design
	public Optional<PageKind> restorePageByName_evCons(String name) {
	
   	List<PageKind> pages = 
   			ofy().transactionless().load().type(PageKind.class).filter("name = ", name).list();
 		
 		if (pages.size() > 1)
 			throw new IllegalStateException();
 		
		if (pages.size() == 0)
		 	return Optional.absent();
		 
		return Optional.of(pages.get(0));
	}
	
	public List<PageKind> getPagesByName_evCons(String name) {
		return ofy().transactionless().load().type(PageKind.class).filter("name = ", name).list();
	}
	
	public List<PageKind> getAllPages_evCons() {
		return ofy().load().type(PageKind.class).list();
	}
	
	
	public PageKind firstPersist(Work<PageKind> work) {
  	PageKind r = ofy().transactNew(COUNT_REPEATS, work);
  	return r;
	}
	
	public void transact(VoidWork work) {
		ofy().transactNew(COUNT_REPEATS, work);
	}
}
