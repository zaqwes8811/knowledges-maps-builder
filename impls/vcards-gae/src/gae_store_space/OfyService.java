package gae_store_space;

import static gae_store_space.OfyService.ofy;

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