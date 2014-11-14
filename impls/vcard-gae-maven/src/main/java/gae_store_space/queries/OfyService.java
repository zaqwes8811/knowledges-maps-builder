package gae_store_space.queries;

// Run outside 'normal'
// https://groups.google.com/forum/#!topic/objectify-appengine/fZltoWFwbrs

import static gae_store_space.queries.OfyService.ofy;
import gae_store_space.GeneratorKind;
import gae_store_space.PageKind;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;

public class OfyService {
  static {
    //factory().register(SentenceKind.class);
    factory().register(PageKind.class);
    //factory().register(NGramKind.class);

    //factory().register(DistributionGen.class);  // интерфейс не регистрируется
    factory().register(GeneratorKind.class);
    //...etc
    
    //factory().register(EasyKind.class);
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
  	//ofy().delete().keys(ofy().load().type(NGramKind.class).keys()).now();
  }
}