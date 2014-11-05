package gae_store_space;

import java.util.List;

import com.google.common.base.Optional;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.Query;

import static gae_store_space.OfyService.ofy;

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
	public static int TIME_STEP_MS = 200;
	public static int COUNT_TRIES = 12; 
	
	// FIXME: вообще, то что читаю в цикле мало что значит в многопользовательском режиме
	//   для исследования возможно так и нужно, но вообще нет.
	
	// FIXME: make синхронизирующий вызов
	
	public void persist(PageKind kind) {
		ofy().save().entity(kind).now();
	}
	
	public void deleteGenerators(List<Key<GeneratorKind>> gens) {
		ofy().delete().keys(gens).now();
	}
	
	public void syncCreateInStore(PageKind kind) {
		persist(kind);
  	
  	int j = 0;
		while (true) {
			if (j > GAESpecific.COUNT_TRIES)
				throw new IllegalStateException();
			
			Optional<PageKind> page_readed = Optional.of(ofy().load().type(PageKind.class).id(kind.getId()).now()); 
	  	if (!page_readed.isPresent()) {
	  		j++;
	  		try {
	        Thread.sleep(GAESpecific.TIME_STEP_MS);
        } catch (InterruptedException e1) {
	        throw new RuntimeException(e1);
        }
	  		continue;
	  	}
			break;
		}
	}
	
	public Optional<PageKind> getPage(String name) {
	// FIXME: можно прочитать только ключи, а потом делать выборки
   	List<PageKind> pages = ofy().load().type(PageKind.class).filter("name = ", name).list();
   	
   	int i = 0;
 		while (true) {
 			if (i > GAESpecific.COUNT_TRIES)
 				//if (pages.size() != 0)  // зависает но почему?
 					throw new IllegalStateException();
 			
 			// FIXME: не ясно нужно ли создавать каждый раз или можно реюзать
 			Query<PageKind> q = ofy().load().type(PageKind.class).filter("name = ", name);
 			pages = q.list();
 			
 			if (pages.size() > 1 || pages.size() == 0) {
 				try {
 					Thread.sleep(GAESpecific.TIME_STEP_MS);
 				} catch (InterruptedException e1) {
 					throw new RuntimeException(e1);
 				}
 				i++;
 				continue;
 		  }
 			break;
 		}
 		
		if (pages.size() == 0)
		 	return Optional.absent();
		 
		return Optional.of(pages.get(0));
	}
}
