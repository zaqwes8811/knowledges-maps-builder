package gae_store_space;

import com.google.common.base.Optional;
import com.googlecode.objectify.Work;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Serialize;
import org.apache.log4j.Logger;

import java.util.HashSet;
import java.util.Set;
import static gae_store_space.OfyService.ofy;

// Must be full thread-safe
//
// Очень важен - попробую им гарантировать согласованность
@Entity
public class UserKind {
  private static Logger log = Logger.getLogger(UserKind.class.getName());

  // FIXME: external lock
  // http://stackoverflow.com/questions/13197756/synchronized-method-calls-itself-recursively-is-this-broken

  // user is unique! can't do that with pages!
  @Id private String id;
  @Serialize private Set<String> pagesNames;

  public String getId() {
    return id;
  }

  private void checkPersisted() {
    if (id == null)
      throw new IllegalStateException();
  }

  private void reset() {
    if (!Optional.fromNullable(pagesNames).isPresent())
      pagesNames = new HashSet<>();
  }

  private UserKind() { }

  public static UserKind createOrRestoreById(final String id) {
    UserKind r = ofy().transact(new Work<UserKind>() {
      @Override
      public UserKind run() {
        UserKind th = ofy().load().type(UserKind.class).id(id).now();
        if (th == null) {
          th = new UserKind();
          th.id = id;
          ofy().save().entity(th);
        }
        return th;
      }
    });

    r.checkPersisted();  // Это должно быть здесь
    r.reset();
    return r;
  }

  private void checkPageName(String pageName) {
    if (pageName == null)
      throw new IllegalArgumentException();
  }

  public synchronized boolean tryPushPage(String pageName) {
    checkPageName(pageName);
    boolean wasInserted = false;
    if (!pagesNames.contains(pageName)) {
      pagesNames.add(pageName);
      wasInserted = true;
    }
    return wasInserted;
  }

  public synchronized boolean removePage(String pageName) {
    checkPageName(pageName);
    return pagesNames.remove(pageName);
  }

  public synchronized boolean isContain(String pageName) {
    checkPageName(pageName);
    return pagesNames.contains(pageName);
  }
}
