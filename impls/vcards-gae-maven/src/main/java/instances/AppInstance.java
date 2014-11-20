package instances;

import gae_store_space.PageKind;
import gae_store_space.OfyService;

import java.util.List;

import gae_store_space.UserKind;
import pipeline.math.DistributionElement;
import web_relays.protocols.PageSummaryValue;
import web_relays.protocols.PathValue;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

public class AppInstance {
	public static final String defaultPageName = "DefaultPage";
	public static final String defaultGeneratorName = "Default";
	public static final String defaultUserId = "DefaultUser";

	public AppInstance() {	}

	public static class Holder {
		static final AppInstance w = new AppInstance();
	}

	public static AppInstance getInstance() {
		return Holder.w;
	}

	UserKind defaultUser = new UserKind();
	
	static public String getTestFileName() {
    return "./fakes/lor.txt";
  }

	public ImmutableList<DistributionElement> getDistribution(PathValue path) {
		if (!(path.getPageName().isPresent() && path.getGenName().isPresent())) 
			throw new IllegalArgumentException();
			
		// Срабатывает только один раз
		// TODO: Генератора реально может и не быть, или не найтись. Тогда лучше вернуть не ноль, а что-то другое 
		// FIXME: страница тоже может быть не найдена
  	PageKind page = defaultUser.getPagePure(path.getPageName().get());
   	return ImmutableList.copyOf(page.getDistribution());
  }

	public void eraseStore() {
		OfyService.clearStore();
		defaultUser.createDefaultPage();
	}

	public void createOrReplacePage(String name, String text) {
		defaultUser.createOrReplacePage(name, text);
	}

	public PageKind getPage(String pageName) {
		return defaultUser.getPagePure(pageName);
	}

	// пока не ясно, что за идентификация будет для пользователя
	// данных может и не быть, так что 
	public List<PageSummaryValue> getUserInformation() {
		return defaultUser.getUserInformation();
	}
	
	public void disablePoint(PathValue p) {
		PageKind page = defaultUser.getPagePure(p.getPageName().get());
		page.disablePoint(p);		
	} 
}
