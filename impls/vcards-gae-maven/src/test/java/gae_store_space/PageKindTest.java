package gae_store_space;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.common.base.Optional;
import com.google.common.collect.Ordering;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.Closeable;
import cross_cuttings_layer.GlobalIO;
import instances.AppInstance;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import pipeline.math.DistributionElement;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

// Это таки юнитест, т.к. работает с фейковой базой данных
public class PageKindTest {
  //private AppInstance app = AppInstance.getInstance();

  private static Logger log = Logger.getLogger(PageKindTest.class.getName());

  private final String testFilePath = "src/test/resources/fakes/lor.txt";

  // https://cloud.google.com/appengine/docs/java/tools/localunittesting
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
  public void testCreateAndPersist() throws Exception {
    try (Closeable c = ObjectifyService.begin()) {
      String plainText = GlobalIO.getGetPlainTextFromFile(testFilePath);

      {
        AppInstance app = new AppInstance();
        app.createOrReplacePage(AppInstance.defaultPageName, plainText);
      }

      {
        AppInstance app = new AppInstance();
        // Если идет доступ к странице и ее может не быть, то нужно ограничить число попыток.
        int countTries = 100;  // random
        PageFrontend page = null;
        while (page == null) {
          try {
            page = app.getPage(AppInstance.defaultPageName);
          } catch (IllegalStateException ex) {

          }
          if (countTries < 0)
            assertTrue(false);
          countTries--;
        }

        assertNotNull(page);

        // Queries
        ArrayList<DistributionElement> distribution = page.getDistribution();
        assertFalse(distribution.isEmpty());
      }
    }
  }

  @Test
  public void testGetDistribution() throws IOException {
    try (Closeable c = ObjectifyService.begin()) {
      String plainText = GlobalIO.getGetPlainTextFromFile(testFilePath);

      Set<Key<PageKind>> filter = new HashSet<>();

      {
        AppInstance app = new AppInstance();
        PageKind page = app.createOrReplacePage(AppInstance.defaultPageName, plainText);

        assertNotNull(page);
        filter.add(Key.create(page));
      }

      {
        Optional<PageFrontend> page = Optional.absent();
        int countTries = 100;  // random
        while (!page.isPresent()) {
          try {
            page = PageFrontend.restore(AppInstance.defaultPageName, filter);
          } catch (IllegalStateException ex) { }
          if (countTries < 0)
            assertTrue(false);
          countTries--;
        }

        assertTrue(page.isPresent());

        // Queries
        ArrayList<DistributionElement> distribution = page.get().getDistribution();
        assertFalse(distribution.isEmpty());

        // TODO: how do that?
        boolean sorted = Ordering.natural().reverse().isOrdered(distribution);
        assertTrue(sorted);
      }
    }
  }

  @Test
  public void testGetWordData() throws Exception {
    try (Closeable c = ObjectifyService.begin()) {
      String plainText = GlobalIO.getGetPlainTextFromFile(testFilePath);

      {
        AppInstance app = new AppInstance();
        app.createOrReplacePage(AppInstance.defaultPageName, plainText);
      }

      {
        AppInstance app = new AppInstance();
        // Если идет доступ к странице и ее может не быть, то нужно ограничить число попыток.
        int countTries = 100;  // random
        PageFrontend page = null;
        while (page == null) {
          try {
            page = app.getPage(AppInstance.defaultPageName);
          } catch (IllegalStateException ex) {

          }
          if (countTries < 0)
            assertTrue(false);
          countTries--;
        }

        assertNotNull(page);

        // Queries
        app.getWordData(AppInstance.defaultPageName);

        ArrayList<DistributionElement> distribution = page.getDistribution();
        assertFalse(distribution.isEmpty());
      }
    }
  }
}
