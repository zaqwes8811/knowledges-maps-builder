import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.Closeable;
import backend.OfyService;
import backend.AppInstance;
import backend.KindUser;
import org.apache.log4j.BasicConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class KindUserTest {
  private static final LocalServiceTestHelper helper =
    new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig()
      .setDefaultHighRepJobPolicyUnappliedJobPercentage(50));

  @Before
  public void setUp() {
    BasicConfigurator.configure();
    helper.setUp();
  }

  @After
  public void tearDown() {
    try (Closeable c = ObjectifyService.begin()) {
      OfyService.clearStore();
    }
    helper.tearDown();
  }

  @Test
  public void testCreateOrRestore() throws Exception {
    String userName = AppInstance.defaultUserId;
    assertNotNull(userName);

    try (Closeable c = ObjectifyService.begin()) {
      KindUser kind = KindUser.createOrRestoreById(userName);
      assertNotNull(kind);
      assertNotNull(kind.getId());
    }
  }
}