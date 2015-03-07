package kinds;

import com.google.common.base.Optional;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Work;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Serialize;
import net.jcip.annotations.NotThreadSafe;

import java.util.HashSet;
import java.util.Set;

import static gae_store_space.OfyService.ofy;

// Must be full thread-safe
//
// Очень важен - попробую им гарантировать согласованность
// FIXME: хранить только данные, так проще будет мигрировать
//   http://habrahabr.ru/post/138461/
//   http://javatalks.ru/topics/41247
//   http://programmersnook.blogspot.ru/2011/03/anemic-domain-model-jpa.html
//   http://samsonych.com/lib/hibernate/persistent-classes.html

@NotThreadSafe
@Entity
public class UserKind {
  //@State
  //private  // FIXME:
  public UserKind() { }

  // FIXME: external lock
  // http://stackoverflow.com/questions/13197756/synchronized-method-calls-itself-recursively-is-this-broken

  // user is unique! can't do that with pages!
  @Id private String id;

  public Set<String> getPageNamesRegister() {
    return pageNamesRegister;
  }

  public void setPageNamesRegister(Set<String> pageNamesRegister) {
    this.pageNamesRegister = pageNamesRegister;
  }

  @Serialize private Set<String> pageNamesRegister;

  public Set<Key<PageKind>> getPageKeys() {
    return pageKeys;
  }

  public void setPageKeys(Set<Key<PageKind>> pageKeys) {
    this.pageKeys = pageKeys;
  }

  // FIXME: keys for pages!
  // FIXME: keys for filters!
  private Set<Key<PageKind>> pageKeys = new HashSet<>();
  public String getId() {
    return id;
  }

  //@Logic
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

  private void checkPersisted() {
    if (id == null)
      throw new IllegalStateException();
  }

  private void reset() {
    if (!Optional.fromNullable(pageNamesRegister).isPresent())
      pageNamesRegister = new HashSet<>();
  }
}
