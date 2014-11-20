package gae_store_space;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.common.base.Optional;
import com.google.common.collect.Ordering;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.Closeable;
import cross_cuttings_layer.GlobalIO;
import instances.AppInstance;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import pipeline.math.DistributionElement;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

// Это таки юнитест, т.к. работает с фейковой базой данных
public class PageKindTest {
  private AppInstance app = AppInstance.getInstance();

  private final String testFilePath = "src/test/resources/fakes/lor.txt";

  // https://cloud.google.com/appengine/docs/java/tools/localunittesting
  private static final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig()
              .setDefaultHighRepJobPolicyUnappliedJobPercentage(50));

  public void buildAndStoreContentPage(String pageName) {
  	String plainText = GlobalIO.getGetPlainTextFromFile(testFilePath);
    app.createOrRecreatePage(pageName, plainText);
  }

  @Before
  public void setUp() { helper.setUp(); }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void testCreateAndPersist() throws Exception {
    try (Closeable c = ObjectifyService.begin()) {
      buildAndStoreContentPage(AppInstance.defaultPageName);

      Optional<PageKind> page = Optional.absent();
      // FIXME: просто греем процессор - bad!
      // ! Мы знаем точно что страница там есть! Это важно!
      // Если идет доступ к странице и ее может не быть, то нужно ограничить число попыток.
      int countTries = 100;  // random
      while (!page.isPresent()) {
        page = PageKind.restore(AppInstance.defaultPageName);
        if (countTries < 0)
          assertTrue(false);
        countTries--;
      }
    }
  }

  @Test
  public void testGetDistribution() throws IOException {
    try (Closeable c = ObjectifyService.begin()) {
      buildAndStoreContentPage(AppInstance.defaultPageName);

      Optional<PageKind> page = Optional.absent();
      // FIXME: просто греем процессор - bad!
      while (!page.isPresent()) {
        page = PageKind.restore(AppInstance.defaultPageName);
      }

      // Queries
      ArrayList<DistributionElement> distribution = page.get().getDistribution();
      assertFalse(distribution.isEmpty());

      // TODO: how do that?
      boolean sorted = Ordering.natural().reverse().isOrdered(distribution);
      assertTrue(sorted);
    }
  }

  @Deprecated
  @Test
  public void testGetWordData() {
    // Получаем все сразу, но это никчему. Можно передать подсписок, но это не то что хотелось бы.
    // Хотелось бы выбирать по некоторому критерию.
    // https://groups.google.com/forum/#!topic/objectify-appengine/scb3xNPFszE
    // http://stackoverflow.com/questions/9867401/objectify-query-filter-by-list-in-entity-contains-search-parameter
    // http://bighow.net/3869301-Objectify___how_to__Load_a_List_lt_Ref_lt___gt__gt__.html
    //
    // http://stackoverflow.com/questions/11924572/using-in-query-in-objectify
    //
    // https://www.mail-archive.com/google-appengine-java@googlegroups.com/msg09389.html
    //
    // TODO: troubles. Может добавить метод выкалывания точек?
    // TODO: Может лучше сделать ссылкой-ключом?
    // TODO: может лучше внешний, а данные получать из страницы. Но будут доп. обращ. к базе.
    //   можно использовать кэши, но как быть с обновлением данных?
  	//PageKind page = putPagesInStore();

    // store_specific
    //Integer pointPosition = page.getGenerator(TextPipeline.defaultGeneratorName).getPosition();

    /*
    // слово одно, но если страниц много, то получим для всех
    List<WordKind> words = ofy().load()
        .type(WordKind.class)
        //.ancestor(page)  // don't work
        //.parent(page)  // don't work
        .filterKey("in", page.getWordKeys()).filter("pointPos =", pointPosition)
        .list();
    assertEquals(words.size(), 1);  // не прошли не свои страницы
    
    WordKind word = words.get(0);
    
    List<SentenceKind> content = word.getContendKinds();

    for (SentenceKind e: content) {
      String v = e.getSentence();

      // TODO: пока будет работать. Сейчас используется только стеммер
      // http://stackoverflow.com/questions/2275004/in-java-how-to-check-if-a-string-contains-a-substring-ignoring-the-case
      boolean in = v.toLowerCase().contains(word.getWord().toLowerCase());
      assertTrue(in);
    }
    // запрещаем точку
    */

  	//PageKind page = putDefaultPagesInStore();
  	//Optional<WordDataValue> v = page.getWordData(TextPipeline.defaultGeneratorName);
  	//assertTrue(v.isPresent());
  } 
}
