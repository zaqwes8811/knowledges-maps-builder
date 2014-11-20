package gae_store_space;

// Run outside 'normal'
// https://groups.google.com/forum/#!topic/objectify-appengine/fZltoWFwbrs

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;

public class OfyService {
  static {
    //factory().register(SentenceKind.class);
    factory().register(PageKind.class);
    factory().register(GeneratorKind.class);
    factory().register(UserKind.class);
    //factory().register(NGramKind.class);
    //factory().register(DistributionGen.class);  // интерфейс не регистрируетс
  }

  public static Objectify ofy() {
    //ObjectifyService.run()
    return ObjectifyService.ofy();//begin();
  }

  public static ObjectifyFactory factory() {
    return ObjectifyService.factory();
  }
  
  public static void clearStore() {
  	ofy().delete().keys(ofy().load().type(PageKind.class).keys()).now();
  	ofy().delete().keys(ofy().load().type(GeneratorKind.class).keys()).now();
  	ofy().delete().keys(ofy().load().type(UserKind.class).keys()).now();
  }
}