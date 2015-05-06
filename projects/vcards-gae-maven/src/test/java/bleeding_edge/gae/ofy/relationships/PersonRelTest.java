package bleeding_edge.gae.ofy.relationships;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.Closeable;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static bleeding_edge.gae.ofy.relationships.OfyService.ofy;

/**
 * Created by zaqwes on 5/9/14.
 */
public class PersonRelTest {
  private static final LocalServiceTestHelper helper =
    new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

  @Before
  public void setUp() {
    helper.setUp();
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void testOneToOne() {
    try (Closeable c = ObjectifyService.begin()) {
      PersonRel wife = new PersonRel();
      wife.name = "July";
      ofy().save().entity(wife).now();
      assertNotNull(wife.id);

      PersonRel me = new PersonRel();
      me.name = "bob";
      ofy().save().entity(me).now();
      assertNotNull(me.id);

      me.significantOther = Key.create(wife);
      ofy().save().entity(me).now();

      // Read
      PersonRel bob = ofy().load().type(PersonRel.class).filter("name", "bob").first().now();
      PersonRel bobswife = ofy().load().key(bob.significantOther).now();
    }

  }
}
