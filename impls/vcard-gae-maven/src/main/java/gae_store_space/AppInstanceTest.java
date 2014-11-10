package gae_store_space;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pipeline.TextPipeline;

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
		new Gson().toJson(a.getUserInformation(TextPipeline.defaultUserId));
	}

}
