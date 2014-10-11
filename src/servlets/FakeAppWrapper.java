package servlets;

import static store_gae_stuff.OfyService.ofy;

import java.util.List;

import store_gae_stuff.ActiveDistributionGenKind;
import store_gae_stuff.ContentPageKind;
import store_gae_stuff.fakes.BuilderOneFakePage;

public class FakeAppWrapper {
	public FakeAppWrapper() {
	// store page
    String activePageName = "Korra";
    
    // store
    ContentPageKind page = new BuilderOneFakePage().buildContentPage("Korra");
    ActiveDistributionGenKind gen = ActiveDistributionGenKind.create(page.getRawDistribution());
    ofy().save().entity(gen).now();
    page.setGenerator(gen);
    ofy().save().entity(page).now();
    
    // read    
    {
	    // FIXME: генератор не загружен из хранилища!
	    List<ContentPageKind> loadedPage = 
	    		ofy().load().type(ContentPageKind.class)activePageName.list();
	    
	    if (loadedPage.size() > 1 || loadedPage.size() == 0) {
	 	    throw new RuntimeException();
	 	  }
	    
	    loadedPage.get(0);
	    
	    //if (loadedPage.size() > 1 || loadedPage.size() == 0) {
	   // 	throw new RuntimeException();
	    //}
	    
	    /*
	    if (!(loadedPage.get(0).g.getId() == page.g.getId()))
	    	throw new RuntimeException();
	    */
	    
	    //ActiveDistributionGenKind gens = 
	    //		ofy().load().type(ActiveDistributionGenKind.class).id(page.g.getId()).now();
	    
	   // if (!gens.id.equals(gen.id))
	    	//throw new RuntimeException();
    }
	}
}
