package gae_store_space;

import static gae_store_space.OfyService.ofy;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import pipeline.TextPipeline;
import pipeline.math.DistributionElement;

import com.google.common.base.Optional;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.UncheckedExecutionException;
import com.google.common.util.concurrent.UncheckedTimeoutException;
import com.googlecode.objectify.Key;

import cross_cuttings_layer.CrossIO;

import servlets.protocols.PageSummaryValue;
import servlets.protocols.PathValue;

public class AppInstance {
	private static final Integer CACHE_SIZE = 5;
	
	// На локальной машине, либо с первого раза, либо никогда - on GAE - хз
	// срабатывает либо быстро, либо очень долго, так что ждем немного
	// https://groups.google.com/forum/#!msg/objectify-appengine/p4UylG6jTwU/qIT8sqrPBokJ
	// FIXME: куча проблем с удалением и консистентностью
	// http://stackoverflow.com/questions/14651998/objects-not-saving-using-objectify-and-gae
	// Но как обрабатываются ошибки?
	// now не всегда работает
	//
	// eventually consistent:
	//   http://habrahabr.ru/post/100891/
	//
	static int TIME_STEP_MS = 200;
	static int COUNT_TRIES = 10;  
	
	static public String getTestFileName() {
    return "./test_data/korra/etalon.srt";
  }
	
	// FIXME: если кеш убрать работает много стабильнее
	LoadingCache<String, Optional<PageKind>> pagesCache = CacheBuilder.newBuilder()
			.maximumSize(CACHE_SIZE)
			.build(
					new CacheLoader<String, Optional<PageKind>>() {
						public Optional<PageKind> load(String key) {
							return PageKind.restore(key);
						}
					});
	
	public ImmutableList<DistributionElement> getDistribution(PathValue path) {
		if (!(path.getPageName().isPresent() && path.getGenName().isPresent())) 
			throw new IllegalArgumentException();
			
		// Срабатывает только один раз
		// TODO: Генератора реально может и не быть, или не найтись. Тогда лучше вернуть не ноль, а что-то другое 
		// FIXME: страница тоже может быть не найдена
  	Optional<PageKind> page = getPage(path.pageName); 
  	if (!page.isPresent())
  		throw new IllegalStateException();
  	
  	GeneratorKind gen = page.get().getGenerator(path.genName).get();
  	return gen.getDistribution();
  }
	
	// скорее исследовательский метод	
	public void createOrRecreatePage(String name, String text) {	
		fullDeletePage(name);
		
		pagesCache.invalidate(name);
		
		int i = 0;
		while (true) {
			if (i > COUNT_TRIES)
				throw new IllegalStateException();
			
			try {
				createPageIfNotExist(name, text);
				break;
			} catch (IllegalArgumentException e) {
				try {
	        Thread.sleep(TIME_STEP_MS);
        } catch (InterruptedException e1) {
	        throw new RuntimeException(e1);
        }
				i++;
			}
		}
	}
	
	private void fullDeletePage(String name) {
		try {
			Optional<PageKind> page = getPage(name);
			if (page.isPresent()) {
				page.get().deleteGenerators();  // это нужно вызвать, но при этом удаляется генератор новой страницы
				//ofy().delete().type(PageKind.class).id(page.get().id).now();  // non delete!
				ofy().delete().keys(ofy().load().type(PageKind.class).filter("name = ", name).keys()).now();
			}
		} catch (UncheckedExecutionException e) {
			// удаляем все лишние
			// FIXME: leak in store - active generators
			CrossIO.print("doubles finded");
			ofy().delete().keys(ofy().load().type(PageKind.class).filter("name = ", name).keys()).now();
		}
	}

	public PageKind createPageIfNotExist(String name, String text) {
		// FIXME: add user info
		List<PageKind> pages = 
			ofy().load().type(PageKind.class).filter("name = ", name).list();
		
		if (pages.isEmpty()) {
			TextPipeline processor = new TextPipeline();
	  	PageKind page = processor.pass(name, text);
	  	
	  	GeneratorKind defaultGenerator = 
	  			GeneratorKind.create(page.getRawDistribution(), TextPipeline.defaultGenName);
	  	defaultGenerator.persist();  	
	  	
	  	page.addGenerator(defaultGenerator);
	  	page.persist();

	  	int i = 0;
			while (true) {
				if (i > COUNT_TRIES)
					throw new IllegalStateException();
				
				try {
					Optional<PageKind> page_readed = getPage(name); 
			  	if (!page_readed.isPresent())
			  		continue;
					break;
				} catch (IllegalArgumentException e) {
					try {
		        Thread.sleep(TIME_STEP_MS);
	        } catch (InterruptedException e1) {
		        throw new RuntimeException(e1);
	        }
					i++;
				}
			}
			
			// убеждаемся что генератор тоже сохранен
			i = 0;
			while (true) {
				if (i > COUNT_TRIES)
					throw new IllegalStateException();
				
				try {
					Optional<GeneratorKind> g = page.getGenerator(TextPipeline.defaultGenName);
					if (!g.isPresent())
			  		continue;
					break;
				} catch (IllegalArgumentException e) {
					try {
		        Thread.sleep(TIME_STEP_MS);
	        } catch (InterruptedException e1) {
		        throw new RuntimeException(e1);
	        }
					i++;
				}
			}

			return page;
		} else {
			throw new IllegalArgumentException();
		}
	}
	
	public void resetFullStore() {
		// Call in case change store schema 
		//
		// пока создаем один раз и удаляем. классы могут менятся, лучше так, чтобы не было 
		//   конфликтов.
		//
  	ofy().delete().keys(ofy().load().type(PageKind.class).keys()).now();
  	ofy().delete().keys(ofy().load().type(GeneratorKind.class).keys()).now();
  	ofy().delete().keys(ofy().load().type(WordKind.class).keys()).now();
  	
  	// clear cache
  	pagesCache.cleanUp();
  	
  	createDefaultPages();  // нельзя это в конструктор
	}
	
	private void createDefaultPages() {
		{
	  	// Own tables
	  	// FIXME: GAE can't read file.
  		String name = TextPipeline.defaultPageName;
  		
  		String text = CrossIO.getGetPlainTextFromFile(getTestFileName());
  		createPageIfNotExist(name, text);
	 	}
  	
  	{
  		String name = TextPipeline.defaultPageName+"Copy";
  		String text = CrossIO.getGetPlainTextFromFile(getTestFileName());
  		createPageIfNotExist(name, text);
  	}
  	
  	int i = 0;
  	while (true) {
  		// Остались ли попытки
  		if (i > COUNT_TRIES)
  			throw new IllegalStateException();
  		
	  	// Try read
	  	// Скрыл, так как падало, но должно работать!!
	  	List<PageKind> pages = 
	  			ofy().load().type(PageKind.class).filter("name = ", TextPipeline.defaultPageName).list();
	  	
	  	// FIXME: иногда страбатывает - почему - не ясно - список пуст, все вроде бы синхронно
	  	if (pages.isEmpty()) {
	  		try {
	        Thread.sleep(TIME_STEP_MS);
        } catch (InterruptedException e) {
	        throw new RuntimeException(e);
        }
	  		i++;
	  		continue;
	  	}
	  	
	  	if (pages.size() > 1) 
	  		throw new IllegalStateException();
  	}
	}
	
	public AppInstance() {
		
	}
	
	// FIXME: may be non thread safe. Да вроде бы должно быть база то потокобезопасная?
	public Optional<PageKind> getPage(String pageName) {
	  return PageKind.restore(pageName);
		
	  /*
	  try {
			
			return pagesCache.get(pageName);
    
		} catch (ExecutionException e) {
    	throw new RuntimeException(e);
    }
    //*/
	}

	public static class Holder {
		static final AppInstance w = new AppInstance();
	}
	
	public static AppInstance getInstance() {
		return Holder.w;
	} 
	
	// пока не ясно, что за идентификация будет для пользователя
	// данных может и не быть, так что 
	public List<PageSummaryValue> getUserInformation(String userId) {
		// FIXME: add user info
		List<PageKind> pages = ofy().load().type(PageKind.class).list();
		
		List<PageSummaryValue> r = new ArrayList<PageSummaryValue>();
		for (PageKind page: pages) {
			r.add(PageSummaryValue.create(page.getName(), page.getGenNames()));
		}
		
		return r;
	}
	
	public void disablePoint(PathValue p) {
		PageKind page = getPage(p.pageName).get();
		GeneratorKind g = page.getGenerator(p.genName).get();
		g.disablePoint(p.pointPos);
		
		ofy().save().entity(g).now();
	} 
}
