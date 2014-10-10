package store_gae_stuff;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;

public class OfyService {
  static {
    factory().register(ContentItem.class);
    factory().register(ContentPageKind.class);
    factory().register(WordItem.class);

    //factory().register(DistributionGen.class);  // интерфейс не регистрируется
    factory().register(ActiveDistributionGenKind.class);
    //...etc
  }

  public static Objectify ofy() {
    return ObjectifyService.ofy();//begin();
  }

  public static ObjectifyFactory factory() {
    return ObjectifyService.factory();
  }
}