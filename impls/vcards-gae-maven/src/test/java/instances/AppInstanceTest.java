package instances;


import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.Closeable;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.gson.Gson;

public class AppInstanceTest {
	private static final LocalServiceTestHelper helper =
	    new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

  @Before
  public void setUp() { helper.setUp(); }

  @After
  public void tearDown() {
    helper.tearDown();
  }
	
	@Test
	public void testGetUserInformation() {
		AppInstance a = AppInstance.getInstance();
		try (Closeable c = ObjectifyService.begin()) {
			// do your work.
			new Gson().toJson(a.getUserInformation());
		}
	}

}
