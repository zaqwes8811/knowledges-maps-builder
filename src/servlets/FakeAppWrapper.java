package servlets;

import static store_gae_stuff.OfyService.ofy;

import java.util.List;

import store_gae_stuff.ActiveDistributionGenKind;
import store_gae_stuff.ContentPageKind;
import store_gae_stuff.fakes.BuilderOneFakePage;

public class FakeAppWrapper {
	public FakeAppWrapper() {
		{
			
		}
 
    // read    
    {
  

    }
	}
	
	static class Holder {
		static final FakeAppWrapper w = new FakeAppWrapper();
	}
	
	public static FakeAppWrapper getInstance() {
		return Holder.w;
	}
}
