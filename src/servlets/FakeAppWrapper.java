package servlets;

import static store_gae_stuff.OfyService.ofy;

import java.util.List;

import store_gae_stuff.ActiveDistributionGenKind;
import store_gae_stuff.ContentPageKind;
import store_gae_stuff.fakes.BuilderOneFakePage;

public class FakeAppWrapper {
	public FakeAppWrapper() {
	// store page
    ContentPageKind page = new BuilderOneFakePage().buildContentPage("Korra");
    ActiveDistributionGenKind gen = ActiveDistributionGenKind.create(page.getRawDistribution());
    ofy().save().entity(gen).now();
    page.setGenerator(gen);
    ofy().save().entity(page).now();
    
    // FIXME: генератор не загружен из хранилища!
    String activePageName = "Korra";
    ContentPageKind loadedPage =
      ofy().load().type(ContentPageKind.class).filter("name = ", activePageName).first().now();
    
    assert loadedPage != null;
    
    ActiveDistributionGenKind gen_ = loadedPage.getGenerators();
    
    if (gen_ == null)
    	throw new RuntimeException();
    
    //if (gen_.size() != 1)
    //	throw new RuntimeException();
    
    ActiveDistributionGenKind f = gen_;
    int i = 0;
    f.getDistribution();
	}
}
