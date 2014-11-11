package gae_store_space;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.common.collect.Ordering;
import cross_cuttings_layer.CrossIO;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import pipeline.TextPipeline;
import pipeline.math.DistributionElement;

import java.io.IOException;
import java.util.ArrayList;

import static gae_store_space.queries.OfyService.ofy;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/*
class ObjectifyFilter_ extends AsyncCacheFilter
{
  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    try {
      super.doFilter(request, response, chain);
    } finally {
      ObjectifyService.reset();
    }
  }

  /**
   * Perform the actions that are performed upon normal completion of a request.
   * /
  public static void complete() {
    AsyncCacheFilter.complete();
    ObjectifyService.reset();
  }
}
*/

// Это таки юнитест, т.к. работает с фейковой базой данных
public class PageKindTest {
  private static final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

  public PageKind buildContentPage(String pageName) {
  	TextPipeline processor = new TextPipeline();
  	String plainText = CrossIO.getGetPlainTextFromFile(AppInstance.getTestFileName());
    return processor.pass(pageName, plainText);
  }

  @Before
  public void setUp() { helper.setUp(); }

  @After
  public void tearDown() {
    //ObjectifyFilter;
    helper.tearDown();
  }

  @Deprecated
  @Test
  public void testCreateAndPersist() throws Exception {
    PageKind page = buildContentPage(TextPipeline.defaultPageName);
    //GeneratorKind gen = GeneratorKind.create(page.buildSourceImportanceDistribution());
    //ofy().save().entity(gen).now();
    //page.setGenerator(gen);
    //ofy().save().entity(page).now();
  }

  @Test
  public void testGetDistribution() throws IOException {
    ofy().save().entity(buildContentPage(TextPipeline.defaultPageName)).now();
    
    // Очень медленно!!
    PageKind page =
    		ofy().load().type(PageKind.class)
    			.filter("name =", TextPipeline.defaultPageName)
    			.limit(1).first().now();

    /// Queries
    ArrayList<DistributionElement> distribution = page.getDistribution();
    assertFalse(distribution.isEmpty());

    // TODO: how do that?
    boolean sorted = Ordering.natural().reverse().isOrdered(distribution);
    assertTrue(sorted);
  }

  @Test
  public void testDeletePage() {
    // TODO: Delete full page
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

    // queries
    //Integer pointPosition = page.getGenerator(TextPipeline.defaultGenName).getPosition();

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
  }
  
  @Deprecated
  @Test 
  public void testGetPackedWordData() {
  	//PageKind page = putDefaultPagesInStore();
  	//Optional<WordDataValue> v = page.getWordData(TextPipeline.defaultGenName);
  	//assertTrue(v.isPresent());
  } 
}
