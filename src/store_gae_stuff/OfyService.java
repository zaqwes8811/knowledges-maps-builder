package store_gae_stuff;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;

public class OfyService {
  static {
    factory().register(ContentItemKind.class);
    factory().register(ContentPageKind.class);
    factory().register(WordKind.class);

    //factory().register(DistributionGen.class);  // интерфейс не регистрируется
    factory().register(ActiveDistributionGenKind.class);
    //...etc
    
    factory().register(EasyKind.class);
  }

  public static Objectify ofy() {
    return ObjectifyService.ofy();//begin();
  }

  public static ObjectifyFactory factory() {
    return ObjectifyService.factory();
  }
}