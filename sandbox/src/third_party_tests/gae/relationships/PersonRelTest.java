package third_party_tests.gae.relationships;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.googlecode.objectify.Key;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static third_party_tests.gae.relationships.OfyService.ofy;

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
    PersonRel wife = new PersonRel();
    wife.name = "July";
    ofy().save().entity(wife);
    assert wife.id != null;

    PersonRel me = new PersonRel();
    me.name = "bob";
    ofy().save().entity(me);
    assert me.id != null;

    me.significantOther = Key.create(wife);
    ofy().save().entity(me);

  }
}
