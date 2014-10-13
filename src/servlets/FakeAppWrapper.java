package servlets;

import static store_gae_stuff.OfyService.ofy;

import java.util.List;

import com.googlecode.objectify.Key;

import store_gae_stuff.ActiveDistributionGenKind;
import store_gae_stuff.ContentPageKind;
import store_gae_stuff.fakes.BuilderOneFakePage;

public class FakeAppWrapper {
	public static final String defaultPageName = "Korra";
	
	public static final String defaultGenName = "No";
	
	public FakeAppWrapper() {
		// пока создаем один раз и удаляем. классы могут менятся, лучше так, чтобы не было 
		//   конфликтов.
		//
		List<Key<ContentPageKind>> keys = ofy().load().type(ContentPageKind.class).keys().list();
  	ofy().delete().keys(keys).now();
  	List<Key<ActiveDistributionGenKind>> keys_gen = ofy().load().type(ActiveDistributionGenKind.class).keys().list();
  	ofy().delete().keys(keys_gen).now();
  	
  	// Own tables
  	ContentPageKind p0 = new BuilderOneFakePage().buildContentPage(defaultPageName);
  	ofy().save().entity(p0).now();
  	ofy().save().entity(p0).now();
  	
  	List<ContentPageKind> pages = 
  			ofy().load().type(ContentPageKind.class).list();
  	
  	if (pages.size() != 1) {
  		throw new IllegalStateException();
  	}
  	
  	// add generator
  	ActiveDistributionGenKind g = ActiveDistributionGenKind.create(p0.getRawDistribution());
  	ofy().save().entity(g).now();
  	p0.setGenerator(g);
  	ofy().save().entity(p0).now();  
  	
  	// may work
	}
	
	public ContentPageKind getPage(String name) {
		List<ContentPageKind> pages = 
    		ofy().load().type(ContentPageKind.class).filter("name = ", name).list();
    
    if (pages.size() != 1) {
  		throw new IllegalStateException();
  	}
    
    return pages.get(0);  // 1 item
	}
	
	static class Holder {
		static final FakeAppWrapper w = new FakeAppWrapper();
	}
	
	public static FakeAppWrapper getInstance() {
		return Holder.w;
	}
}
