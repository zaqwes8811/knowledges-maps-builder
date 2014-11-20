package instances;

import gae_store_space.PageKind;
import gae_store_space.GAEStoreAccessManager;
import gae_store_space.OfyService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import pipeline.TextPipeline;
import pipeline.math.DistributionElement;
import web_relays.protocols.PageSummaryValue;
import web_relays.protocols.PathValue;

import com.google.common.base.Optional;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;

import cross_cuttings_layer.GlobalIO;

public class AppInstance {
	private static final Integer CACHE_SIZE = 5;	
	
	GAEStoreAccessManager store = new GAEStoreAccessManager();
	
	static public String getTestFileName() {
    return "./fakes/lor.txt";
  }
	
	// FIXME: если кеш убрать работает много стабильнее
	LoadingCache<String, Optional<PageKind>> pagesCache = CacheBuilder.newBuilder()
			.maximumSize(CACHE_SIZE)
			.build(
					new CacheLoader<String, Optional<PageKind>>() {
						@Override
						public Optional<PageKind> load(String key) { return PageKind.restore(key); }
					});
	
	public ImmutableList<DistributionElement> getDistribution(PathValue path) {
		if (!(path.getPageName().isPresent() && path.getGenName().isPresent())) 
			throw new IllegalArgumentException();
			
		// Срабатывает только один раз
		// TODO: Генератора реально может и не быть, или не найтись. Тогда лучше вернуть не ноль, а что-то другое 
		// FIXME: страница тоже может быть не найдена
  	Optional<PageKind> page = getPage(path.getPageName().get()); 
  	if (!page.isPresent())
  		throw new IllegalStateException();

  	return ImmutableList.copyOf(page.get().getDistribution());
  }
	
	// скорее исследовательский метод
	// https://code.google.com/p/objectify-appengine/wiki/Transactions
	// FIXME: вот тут важна транзактивность
	public void createOrRecreatePage(String name, String text) {	
		fullDeletePage(name);
		pagesCache.invalidate(name);
		PageKind.createPageIfNotExist_eventually(name, text);
		pagesCache.invalidate(name);
	}

	private void fullDeletePage(String name) {
		//try {
			Optional<PageKind> page = getPage(name);
			if (page.isPresent())
				page.get().deleteFromStore_strong();
		//} 
		//catch (UncheckedExecutionException e) {
		//	throw new IllegalStateException();
		//}
	}
	
	public void resetFullStore() {
		// Call in case change store schema 
		//
		// пока создаем один раз и удаляем. классы могут менятся, лучше так, чтобы не было 
		//   конфликтов.
		//
		OfyService.clearStore();
  	
  	// clear cache
  	pagesCache.cleanUp();
  	
  	createDefaultPage();  // нельзя это в конструктор
	}
	
	private void createDefaultPage() {
		String name = TextPipeline.defaultPageName;
		String text = GlobalIO.getGetPlainTextFromFile(getTestFileName());
		PageKind.createPageIfNotExist_eventually(name, text);
	}
	
	public AppInstance() { }
	
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
		
		List<PageKind> pages = store.getAllPages_evCons();
		
		List<PageSummaryValue> r = new ArrayList<>();
		for (PageKind page: pages) 
			r.add(PageSummaryValue.create(page.getName(), page.getGenNames()));
		
		return r;
	}
	
	public void disablePoint(PathValue p) {
		PageKind page = getPage(p.getPageName().get()).get();
		page.disablePoint(p);		
	} 
}
