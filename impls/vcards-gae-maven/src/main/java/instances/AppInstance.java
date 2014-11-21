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

	public static class Holder {
		static final AppInstance w = new AppInstance();
	}

	public AppInstance() {

	}

	private synchronized UserKind getUser() {
		if (defaultUser == null) {
			// http://stackoverflow.com/questions/7325579/java-lang-noclassdeffounderror-could-not-initialize-class-xxx
			defaultUser = UserKind.createOrRestoreById(defaultUserId);
		}
		return defaultUser;
	}

	public static AppInstance getInstance() {
		return Holder.w;
	}

	UserKind defaultUser = null;
	
	static public String getTestFileName() {
    return "./fakes/lor.txt";
  }

	public ImmutableList<DistributionElement> getDistribution(PathValue path) {
		if (!(path.getPageName().isPresent() && path.getGenName().isPresent())) 
			throw new IllegalArgumentException();
			
		// Срабатывает только один раз
		// TODO: Генератора реально может и не быть, или не найтись. Тогда лучше вернуть не ноль, а что-то другое 
		// FIXME: страница тоже может быть не найдена
  	PageKind page = getUser().getPagePure(path.getPageName().get());
   	return ImmutableList.copyOf(page.getDistribution());
  }

	public void eraseStore() {
		OfyService.clearStore();
		getUser().clear();
		getUser().createDefaultPage();
	}

	public void createOrReplacePage(String pageName, String text) {
		getUser().createOrReplacePage(pageName, text);
	}

	public PageKind getPage(String pageName) {
		return getUser().getPagePure(pageName);
	}

	// пока не ясно, что за идентификация будет для пользователя
	// данных может и не быть, так что 
	public List<PageSummaryValue> getUserInformation() {
		return getUser().getUserInformation();
	}

	public void disablePoint(PathValue p) {
		PageKind page = getUser().getPagePure(p.getPageName().get());
		page.disablePoint(p);		
	} 
}
