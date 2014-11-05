package gae_store_space.queries;

import gae_store_space.GeneratorKind;
import gae_store_space.PageKind;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Optional;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.Query;

import static gae_store_space.queries.OfyService.ofy;

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
	
	public void persist(GeneratorKind kind) {
		ofy().save().entity(kind).now();
	}
	
	public void deleteGenerators(List<Key<GeneratorKind>> generators) {
		ofy().delete().keys(generators).now();
	}
	
	public void deletePage(PageKind p) {
		ofy().delete().type(PageKind.class).id(p.getId()).now();
	}
	
	public Optional<GeneratorKind> getGenerator(List<Key<GeneratorKind>> generators, String name) {
		if (name == null)
  		throw new IllegalArgumentException();
  	
  	if (generators.isEmpty())
  		throw new IllegalStateException();
  	
  	Optional<GeneratorKind> g = Optional.absent();
  	
  	int i = 0;
 		while (true) {
 			if (i > GAESpecific.COUNT_TRIES)
				throw new IllegalStateException();
 			
 			List<GeneratorKind> gen = 
	  			ofy().load().type(GeneratorKind.class)
		  			.filterKey("in", generators)
		  			.filter("name = ", name)
		  			.list();
	
	  	if (!gen.isEmpty()) {
	  		if (gen.size() > 1)
	    		throw new IllegalStateException(name);
	    	
	    	g = Optional.of(gen.get(0));
	    	g.get().restore();
	  	} else {
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
  	
  	return g;
	}
	
	public void syncCreateInStore(GeneratorKind kind) {
		persist(kind);
  	
  	// FIXME: Почему иногда все равно по запросу генератора еще нет?
  	
		// убеждаемся что генератор тоже сохранен
		// это нельзя сделать в этом методе! Мы не проверим если не создаем
		int i = 0;
		while (true) {
			if (i > GAESpecific.COUNT_TRIES)
				throw new IllegalStateException();
			
			// FIXME: что-то не то. похоже запросы нужно делать по полному пути!
			Optional<GeneratorKind> g = Optional.fromNullable(ofy().load().type(GeneratorKind.class).id(kind.getId()).now());
			if (!g.isPresent()) {
				i++;
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
	
	public List<String> getGenNames(List<Key<GeneratorKind>> generators) {
  	List<GeneratorKind> gs = 
  			ofy().load().type(GeneratorKind.class)
	  			.filterKey("in", generators).list();
  	
  	List<String> r = new ArrayList<String>();
  	for (GeneratorKind g: gs) 
  		r.add(g.getName()); 

  	return r;
  }
	
	public List<PageKind> getPages() {
		return ofy().load().type(PageKind.class).list();
	}
	
	public List<PageKind> getPages(String name) {
		return ofy().load().type(PageKind.class).filter("name = ", name).list();
	}
}
