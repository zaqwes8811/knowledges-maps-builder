package medium_tests;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;
import dal.gae_kinds.ContentItem;
import dal.gae_kinds.ContentPage;

public class OfyService {
  static {
    factory().register(ContentItem.class);
    factory().register(ContentPage.class);
    //...etc
  }

  public static Objectify ofy() {
    return ObjectifyService.ofy();//begin();
  }

  public static ObjectifyFactory factory() {
    return ObjectifyService.factory();
  }
}