package gae_store_space;

import static gae_store_space.OfyService.ofy;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import pipeline.TextPipeline;
import pipeline.math.DistributionElement;
import servlets.protocols.PageSummaryValue;
import servlets.protocols.PathValue;

import com.google.common.base.Optional;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.UncheckedExecutionException;

import cross_cuttings_layer.CrossIO;

public class AppInstance {
	private static final Integer CACHE_SIZE = 5;
	
	
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
	// https://code.google.com/p/objectify-appengine/wiki/Transactions
	// FIXME: вот тут важна транзактивность
	public void createOrRecreatePage(String name, String text) {	
		fullDeletePage(name);
		pagesCache.invalidate(name);
		createPageIfNotExist_sync(name, text);
		pagesCache.invalidate(name);
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
			// FIXME: удаляем все лишние - leak in store - active generators
			ofy().delete().keys(ofy().load().type(PageKind.class).filter("name = ", name).keys()).now();
		}
	}

	// FIXME: вот эту операцию лучше синхронизировать. И пользователю высветить, что идет процесс
	//   Иначе будут гонки. А может быть есть транзации на GAE?
	public PageKind createPageIfNotExist_sync(String name, String text) {
		// FIXME: add user info
		List<PageKind> pages = 
			ofy().load().type(PageKind.class).filter("name = ", name).list();
		
		if (pages.isEmpty()) {
			TextPipeline processor = new TextPipeline();
			
	  	PageKind page = processor.pass(name, text);  // по сути нужно только для создание генератора
	  	
	  	GeneratorKind defaultGenerator = 
	  			GeneratorKind.create(page.getRawDistribution(), TextPipeline.defaultGenName);
	  	defaultGenerator.persist();  	
	  	
	  	page.addGenerator(defaultGenerator);
	  	page.persist();

	  	int j = 0;
			while (true) {
				if (j > GAESpecific.COUNT_TRIES)
					throw new IllegalStateException();
				
				Optional<PageKind> page_readed = getPage(name); 
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
			
			// убеждаемся что генератор тоже сохранен
			int i = 0;
			while (true) {
				if (i > GAESpecific.COUNT_TRIES)
					throw new IllegalStateException();
				
				Optional<GeneratorKind> g = page.getGenerator(TextPipeline.defaultGenName);
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
  	ofy().delete().keys(ofy().load().type(NGramKind.class).keys()).now();
  	
  	// clear cache
  	pagesCache.cleanUp();
  	
  	createDefaultPage();  // нельзя это в конструктор
	}
	
	private void createDefaultPage() {
		String name = TextPipeline.defaultPageName;
		String text = CrossIO.getGetPlainTextFromFile(getTestFileName());
		createPageIfNotExist_sync(name, text);
	}
	
	public AppInstance() {
		
	}
	
	// FIXME: may be non thread safe. Да вроде бы должно быть база то потокобезопасная?
	public Optional<PageKind> getPage(String pageName) {
	  try {
			return pagesCache.get(pageName);
		} catch (ExecutionException e) {
    	throw new RuntimeException(e);
    }
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
