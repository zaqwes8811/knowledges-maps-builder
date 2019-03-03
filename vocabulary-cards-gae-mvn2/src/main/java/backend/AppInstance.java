package backend;

import java.util.List;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import http_api.*;
import backend.math.DistributionElement;

import com.google.common.collect.ImmutableList;

public class AppInstance {
	public static final String defaultPageName = "Default page";
	public static final String defaultGeneratorName = "Default generator";
	public static final String defaultUserId = "Default user";
	UserAccessor defaultUser = null;
	private static final Integer CACHE_SIZE = 5;
	LoadingCache<String, Integer> dictsCache = null;

	//===========================================================

	public static class Holder {
		static final AppInstance w = new AppInstance();
	}

	public AppInstance() {	}

	public ValueWordData getWordData(String pageName) {
		PageWrapper p = getPage(pageName);
		return p.getWordData().get();
	}

	private synchronized UserAccessor getUser() {
		if (defaultUser == null) {
			// http://stackoverflow.com/questions/7325579/java-lang-noclassdeffounderror-could-not-initialize-class-xxx
			defaultUser = UserAccessor.createOrRestoreById(defaultUserId);
		}
		return defaultUser;
	}

	public static AppInstance getInstance() {
		return Holder.w;
	}

	static public String getTestFileName() {
	return "./fakes/lor.txt";
	  }

	public ImmutableList<DistributionElement> getDistribution(ValuePath path) {
		if (!(path.getPageName().isPresent() && path.getGenName().isPresent())) 
			throw new IllegalArgumentException();
			
		// Срабатывает только один раз
		// TODO: Генератора реально может и не быть, или не найтись. Тогда лучше вернуть не ноль, а что-то другое 
		// FIXME: страница тоже может быть не найдена
		PageWrapper page = getUser().getPagePure(path.getPageName().get());
   		return ImmutableList.copyOf(page.getImportanceDistribution());
  	}

	public void eraseStore() {
		OfyService.clearStore();
		getUser().getUserKind().clear();
		getUser().createDefaultPage();
	}

	public void createOrReplacePage(String pageName, String text)
	{
		getUser().createOrReplacePage(pageName, text);
	}

	public PageWrapper getPage(String pageName) {
		return getUser().getPagePure(pageName);
	}

	// пока не ясно, что за идентификация будет для пользователя
	// данных может и не быть, так что 
	public List<ValuePageSummary> getUserInformation() {
		return getUser().getUserInformation();
	}

	public void disablePoint(ValuePath p) {
		PageWrapper page = getUser().getPagePure(p.getPageName().get());
		page.disablePoint(p);		
	}


	private void initDictsCache()
	{
		if (dictsCache != null){
			return;
		}
		dictsCache = CacheBuilder.newBuilder()
			.maximumSize(CACHE_SIZE)
			.build(
					new CacheLoader<String, Integer>() {
						@Override
						public Integer load(String key) {
							return 0;
						}
					});
	}

	public void createOrReplaceDict(String name, String text)
	{
		initDictsCache();

		// Проверяем нет ли такого словаря? - store is eventually consistent - how to check?
		//dictsCache.get(name);

	}
}
