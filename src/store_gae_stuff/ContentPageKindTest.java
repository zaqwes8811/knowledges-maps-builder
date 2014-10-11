package store_gae_stuff;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.common.collect.Ordering;
import core.math.DistributionElement;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import store_gae_stuff.fakes.BuilderOneFakePage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static store_gae_stuff.OfyService.ofy;


// Это таки юнитест, т.к. работает с фейковой базой данных
public class ContentPageKindTest {
  private static final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

  public ContentPageKind buildContentPage(String pageName) {
    return new BuilderOneFakePage().buildContentPage(pageName);
  }

  @Before
  public void setUp() { helper.setUp(); }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void testCreateAndPersist() throws Exception {
    ContentPageKind page = buildContentPage("Korra");
    ActiveDistributionGenKind gen = ActiveDistributionGenKind.create(page.getRawDistribution());
    ofy().save().entity(gen).now();
    page.setGenerator(gen);
    ofy().save().entity(page).now();
  }

  @Test
  public void testGetDistribution() throws IOException {
    ofy().save().entity(buildContentPage("Korra")).now();
    
    // Очень медленно!!
    ContentPageKind page = ofy().load().type(ContentPageKind.class).filter("name =", "Korra").limit(1).first().now();

    /// Queries
    ArrayList<DistributionElement> distribution = page.getRawDistribution();
    assertFalse(distribution.isEmpty());

    // TODO: how do that?
    boolean sorted = Ordering.natural().reverse().isOrdered(distribution);
    assertTrue(sorted);
  }

  @Test
  public void testDeletePage() {
    // TODO: Delete full page
  }

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

    // Check store
    String activePageName = "Korra";
    ContentPageKind loadedPage =
      ofy().load().type(ContentPageKind.class).filter("name = ", activePageName).first().now();
    assertNull(loadedPage);  // с одним именем могуть быть, id будут разными

    // Create new page
    ContentPageKind page = buildContentPage(activePageName);
    ActiveDistributionGenKind gen = ActiveDistributionGenKind.create(page.getRawDistribution());
    ofy().save().entity(gen).now();
    page.setGenerator(gen);

    // создаем две страницы, важно для проверки разделения запросов.
    ofy().save().entity(page).now();
    ofy().save().entity(buildContentPage("Korra1")).now();  //

    // queries
    Integer pointPosition = gen.getPosition();

    // слово одно, но если страниц много, то получим для всех
    List<WordItemKind> words = ofy().load()
        .type(WordItemKind.class)
        //.ancestor(page)  // don't work
        //.parent(page)  // don't work
        .filterKey("in", page.words)
        .filter("pointPos =", pointPosition)
        .list();

    assertEquals(words.size(), 1);  // не прошли не свои страницы
    WordItemKind word = words.get(0);
    List<ContentItemKind> content = ofy().load().type(ContentItemKind.class)
        .filterKey("in", word.getItems()).list();

    for (ContentItemKind e: content) {
      String v = e.getSentence();

      // TODO: пока будет работать. Сейчас используется только стеммер
      // http://stackoverflow.com/questions/2275004/in-java-how-to-check-if-a-string-contains-a-substring-ignoring-the-case
      boolean in = v.toLowerCase().contains(word.word.toLowerCase());
      assertTrue(in);
    }

    // запрещаем точку
  }
}
