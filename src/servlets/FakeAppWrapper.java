package servlets;

import static store_gae_stuff.OfyService.ofy;
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
	}
}
