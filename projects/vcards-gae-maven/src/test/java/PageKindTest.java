import org.apache.log4j.Logger;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

// Это таки юнитест, т.к. работает с фейковой базой данных
public class PageKindTest //extends AbstractBenchmark // don't work
{
	//@Rule
	//public TestRule benchmarkRun = new BenchmarkRule();
  //private AppInstance app = AppInstance.getInstance();

  private static Logger log = Logger.getLogger(PageKindTest.class.getName());

  private final String testFilePath = "src/test/resources/fakes/lor.txt";

//  // https://cloud.google.com/appengine/docs/java/tools/localunittesting
//  private static final LocalServiceTestHelper helper =
//      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig()
//              .setDefaultHighRepJobPolicyUnappliedJobPercentage(50));
//
//  @Before
//  public void setUp() {
//    BasicConfigurator.configure();
//    helper.setUp();
//  }
//
//  @After
//  public void tearDown() {
//    try (Closeable c = ObjectifyService.begin()) {
//      OfyService.clearStore();
//    }
//    helper.tearDown();
//  }
//
//  @Test
//  public void testCreateAndPersist() throws Exception {
//    try (Closeable c = ObjectifyService.begin()) {
//      String plainText = GlobalIO.getGetPlainTextFromFile(testFilePath);
//
//      {
//        AppInstance app = new AppInstance();
//        app.createOrReplacePage(AppInstance.defaultPageName, plainText);
//      }
//
//      {
//        AppInstance app = new AppInstance();
//        // Если идет доступ к странице и ее может не быть, то нужно ограничить число попыток.
//        int countTries = 100;  // random
//        PageWrapper page = null;
//        while (page == null) {
//          try {
//            page = app.getPage(AppInstance.defaultPageName);
//          } catch (IllegalStateException ex) {
//
//          }
//          if (countTries < 0)
//            assertTrue(false);
//          countTries--;
//        }
//
//        assertNotNull(page);
//
//        // Queries
//        ArrayList<DistributionElement> distribution = page.getImportanceDistribution();
//        assertFalse(distribution.isEmpty());
//      }
//    }
//  }
//
//  @Test
//  public void testGetDistribution() throws IOException {
//    try (Closeable c = ObjectifyService.begin()) {
//      String plainText = GlobalIO.getGetPlainTextFromFile(testFilePath);
//
//      Set<Key<KindPage>> filter = new HashSet<>();
//
//      {
//        AppInstance app = new AppInstance();
//        KindPage page = app.createOrReplacePage(AppInstance.defaultPageName, plainText);
//
//        assertNotNull(page);
//        filter.add(Key.create(page));
//      }
//
//      {
//        Optional<PageWrapper> page = Optional.absent();
//        int countTries = 100;  // random
//        while (!page.isPresent()) {
//          try {
//            page = PageBuilder.restore(AppInstance.defaultPageName, filter);
//          } catch (IllegalStateException ex) { }
//          if (countTries < 0)
//            assertTrue(false);
//          countTries--;
//        }
//
//        assertTrue(page.isPresent());
//
//        // Queries
//        ArrayList<DistributionElement> distribution = page.get().getImportanceDistribution();
//        assertFalse(distribution.isEmpty());
//
//        // TODO: how do that?
//        boolean sorted = Ordering.natural().reverse().isOrdered(distribution);
//        assertTrue(sorted);
//      }
//    }
//  }
//
//  @Test
//  public void testGetWordData() throws Exception {
//    try (Closeable c = ObjectifyService.begin()) {
//      String plainText = GlobalIO.getGetPlainTextFromFile(testFilePath);
//
//      {
//        AppInstance app = new AppInstance();
//        app.createOrReplacePage(AppInstance.defaultPageName, plainText);
//      }
//
//      {
//        AppInstance app = new AppInstance();
//        // Если идет доступ к странице и ее может не быть, то нужно ограничить число попыток.
//        int countTries = 100;  // random
//        PageWrapper page = null;
//        while (page == null) {
//          try {
//            page = app.getPage(AppInstance.defaultPageName);
//          } catch (IllegalStateException ex) {
//
//          }
//          if (countTries < 0)
//            assertTrue(false);
//          countTries--;
//        }
//
//        assertNotNull(page);
//
//        // Queries
//        app.getWordData(AppInstance.defaultPageName);
//
//        ArrayList<DistributionElement> distribution = page.getImportanceDistribution();
//        assertFalse(distribution.isEmpty());
//      }
//    }
//  }
}
