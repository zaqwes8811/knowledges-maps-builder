// https://groups.google.com/forum/#!topic/objectify-appengine/5I-B2wxog5g
// https://developers.google.com/appengine/docs/java/tools/localunittesting?csw=1#Introducing_the_Java_Testing_Utilities
//
// https://developers.google.com/appengine/docs/java/datastore/index
package chew.chew_third_party.gae;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.googlecode.objectify.*;
import org.junit.*;

//import static com.googlecode.objectify.ObjectifyService;
import java.util.List;
import java.util.Map;

import static chew.chew_third_party.gae.OfyService.ofy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
//import static com.googlecode.objectify.ObjectifyService.ofy;

// TODO: now version: 4.0b1 - в прочих местами работает не так и не так как в Wiki!
public class CarTest {
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
	public void testCreate() {
    // Troubles:
    //  Result -> Ref - что будет с синхронностью/асинхронностью
    //
		Car porsche = new Car();
    porsche.color = 8;
    porsche.license = "6JHD";
		ofy().save().entity(porsche).now();    // async without the now()

    assertNotNull(porsche.id);    // id was autogenerated

    Car porsche2 = new Car();
    porsche2.color = 5;
    porsche2.license = "6JHD";
    ofy().save().entity(porsche2).now();    // async without the now()

    Key<Car> thingKey = Key.create(Car.class, porsche.id);
		// Get it back
    Car fetched1 = ofy().cache(false).load().key(thingKey).getValue();
    assert fetched1.equals(porsche);  // TODO: возможно и не должно

    List<Car> result = ofy().load().type(Car.class).list();

    assertEquals(2, result.size());

    //Result<Car> th = ofy().load().key(thingKey);
		//Ref<Car> result = ofy.load().key();  // Result is async
		//Car fetched1 = result.now();    // Materialize the async value

    //Car fetched2 = ofy().load().type(Car.class).id(porsche.id);//.now();

		// More likely this is what you will type
		Car fetched2 = ofy().load().type(Car.class).id(porsche.id).get();//.now();
    assertNotNull(fetched2);

		// Or you can issue a query
		Car fetched3 = ofy().load().type(Car.class).filter("license", "2FAST").first().get();//.now();
    assertNull(fetched3);

		// Change some data and write it
		//porsche.color = BLUE;
		//ofy().save().entity(porsche).now();    // async without the now()

		// Delete it
		//ofy().delete().entity(porsche).now();    // async without the now()
	}

  @Test
  public void testTree() {
    Car porsche = new Car();
    porsche.color = 8;
    porsche.license = "6JHD";
    ofy().save().entity(porsche).now();    // async without the now()
    assertNotNull(porsche.id);    // id was autogenerated

    Engine engine1 = new Engine();
    ofy().save().entities(engine1).now();
    assertNotNull(engine1.id);    // id was autogenerated
    // Key.create(porsche, Engine.class, engine1)

    porsche.owners.add(Ref.create(engine1));
    ofy().save().entity(porsche).now();

    assertNotNull(porsche.id);    // id was autogenerated

    // Извлекаем
  }
}
