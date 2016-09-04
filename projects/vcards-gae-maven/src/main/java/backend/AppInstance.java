package backend;

import java.util.List;

import http_related.PageFrontend;
import pipeline.math.DistributionElement;
import http_related.PageSummaryValue;
import http_related.PathValue;

import com.google.common.collect.ImmutableList;
import http_related.WordDataValue;

public class AppInstance {
	public static final String defaultPageName = "Default page";
	public static final String defaultGeneratorName = "Default generator";
	public static final String defaultUserId = "Default user";

	public static class Holder {
		static final AppInstance w = new AppInstance();
	}

	public AppInstance() {	}

	public WordDataValue getWordData(String pageName) {
		PageFrontend p = getPage(pageName);
		return p.getWordData().get();
	}

	private synchronized UserFrontend getUser() {
		if (defaultUser == null) {
			// http://stackoverflow.com/questions/7325579/java-lang-noclassdeffounderror-could-not-initialize-class-xxx
			defaultUser = UserFrontend.createOrRestoreById(defaultUserId);
		}
		return defaultUser;
	}

	public static AppInstance getInstance() {
		return Holder.w;
	}

	UserFrontend defaultUser = null;
	
	static public String getTestFileName() {
	return "./fakes/lor.txt";
	  }

	public ImmutableList<DistributionElement> getDistribution(PathValue path) {
		if (!(path.getPageName().isPresent() && path.getGenName().isPresent())) 
			throw new IllegalArgumentException();
			
		// Срабатывает только один раз
		// TODO: Генератора реально может и не быть, или не найтись. Тогда лучше вернуть не ноль, а что-то другое 
		// FIXME: страница тоже может быть не найдена
		PageFrontend page = getUser().getPagePure(path.getPageName().get());
   	return ImmutableList.copyOf(page.getImportanceDistribution());
  }

	public void eraseStore() {
		OfyService.clearStore();
		getUser().clear();
		getUser().createDefaultPage();
	}

	public PageKind createOrReplacePage(String pageName, String text) {
		return getUser().replacePage(pageName, text);
	}

	public PageFrontend getPage(String pageName) {
		return getUser().getPagePure(pageName);
	}

	// пока не ясно, что за идентификация будет для пользователя
	// данных может и не быть, так что 
	public List<PageSummaryValue> getUserInformation() {
		return getUser().getUserInformation();
	}

	public void disablePoint(PathValue p) {
		PageFrontend page = getUser().getPagePure(p.getPageName().get());
		page.disablePoint(p);		
	} 
}
