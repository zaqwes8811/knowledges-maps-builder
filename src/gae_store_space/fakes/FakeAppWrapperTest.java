package gae_store_space.fakes;

import gae_store_space.high_perf.OnePageProcessor;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.gson.Gson;

public class FakeAppWrapperTest {
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
		FakeAppWrapper a = FakeAppWrapper.getInstance();
		new Gson().toJson(a.getUserInformation(OnePageProcessor.defaultUserId));
	}

}
