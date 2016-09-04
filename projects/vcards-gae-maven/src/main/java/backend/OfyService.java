package backend;

// Run outside 'normal'
// https://groups.google.com/forum/#!topic/objectify-appengine/fZltoWFwbrs

import com.google.common.base.Optional;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;

import java.util.List;
import java.util.Set;

public class OfyService {
  static {
//    factory().register(GoogleTranslatorRecord.class);
    factory().register(PageKind.class);
    factory().register(GeneratorKind.class);
    factory().register(UserKind.class);
    factory().register(DictionaryKind.class);
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
//    ofy().delete().keys(ofy().load().type(GoogleTranslatorRecord.class).keys()).now();
    ofy().delete().keys(ofy().load().type(DictionaryKind.class).keys()).now();
  }

  // generic not work
  //public static <T> Optional<T> getPageKind(
    public static Optional<DictionaryKind> getPageKind(
            String pageName) {
        List<DictionaryKind> kinds =
                OfyService.ofy().transactionless().load().type(DictionaryKind.class)
                        .filter("name = ", pageName)
                        .list();

        if (kinds.size() > 1) {
            throw new StoreIsCorruptedException();
        }

        if (kinds.size() == 0)
            return Optional.absent();

        return Optional.of(kinds.get(0));
    }
}