package store_gae_stuff;

import business.math.DistributionElement;
import business.nlp.PlainTextTokenizer;
import business.text_extractors.SpecialSymbols;
import business.text_extractors.SubtitlesContentHandler;
import business.text_extractors.SubtitlesParser;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Ordering;
import com.google.common.io.Closer;
import common.Tools;
import org.apache.tika.parser.Parser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.ContentHandler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static store_gae_stuff.OfyService.ofy;


// Это таки юнитест, т.к. работает с фейковой базой данных
public class PersistContentPageKindTest {
  private String getTestFileName() {
    return "test_data/korra/The Legend of Korra - 02x10 - A New Spiritual Age.WEB-DL.BS.English.HI.C.orig.Addic7ed.com.srt";
  }

  private static final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

  private String getSubtitlesToPlainText(String filename) throws IOException {
    String rawText = Joiner.on('\n').join(Tools.fileToList(filename));
    //InputStream in = closer.register(new FileInputStream(new File(filename)));  // No in GAE

    // Пока файл строго юникод - UTF-8
    Closer closer = Closer.create();
    try {
      // http://stackoverflow.com/questions/247161/how-do-i-turn-a-string-into-a-stream-in-java
      InputStream in = closer.register(new ByteArrayInputStream(rawText.getBytes(Charsets.UTF_8)));
      Parser parser = new SubtitlesParser();
      List<String> sink = new ArrayList<String>();
      ContentHandler handler = new SubtitlesContentHandler(sink);
      parser.parse(in, handler, null, null);

      // Получили список строк.
      SpecialSymbols symbols = new SpecialSymbols();
      return Joiner.on(symbols.WHITESPACE_STRING).join(sink);
    } catch (Throwable e) {
      throw closer.rethrow(e);
    } finally {
      closer.close();
    }
  }

  private ArrayList<ContentItemKind> getContentElements(String text) {
    ImmutableList<String> sentences = new PlainTextTokenizer().getSentences(text).subList(1, 50);
    assertFalse(sentences.isEmpty());

    // Пакуем
    ArrayList<ContentItemKind> contentElements = new ArrayList<ContentItemKind>();
    Long pos = (long) 1;
    for (String sentence: sentences) {
      contentElements.add(new ContentItemKind(sentence, pos));
      pos++;
    }

    return contentElements;
  }

  private ContentPageKind buildContentPage() {
    String filename = getTestFileName();
    try {
      // Phase I
      String plainText = getSubtitlesToPlainText(filename);
      assertFalse(plainText.isEmpty());

      // Phase II не всегда они разделены, но с случае с субтитрами точно разделены.
      ArrayList<ContentItemKind> contentElements = getContentElements(plainText);

      // Last - Persist page
      return new ContentPageBuilder().build("Korra", contentElements);     
    } catch (IOException e)  {
      throw new RuntimeException(e);
    }
  }

  @Before
  public void setUp() { helper.setUp(); }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void testCreateAndPersist() throws Exception {
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
    //
    ContentPageKind page = buildContentPage();
    ActiveDistributionGenKind gen = ActiveDistributionGenKind.create(page.getRawDistribution());

    ofy().save().entity(page).now();

    Integer idxPosition = gen.getPosition();
    int countFirst = 4;
    WordItemKind elem = ofy().load().type(WordItemKind.class).filter("sortedIdx =", idxPosition).first().now();
    List<ContentItemKind> coupled = ofy().load().type(ContentItemKind.class)
      .filterKey("in", elem.getItems())
      //.filter("pos <=", 8)
      .limit(countFirst)
      .list();

    assertFalse(coupled.isEmpty());

    ContentPageKind loadedPage = ofy().load().type(ContentPageKind.class).filter("name = ", "Korra").first().now();
    assertNotNull(loadedPage);
  }

  @Test
  public void testGetDistribution() throws IOException {
    ofy().save().entity(buildContentPage()).now();
    
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
    ContentPageKind page = ofy().load().type(ContentPageKind.class).filter("name =", "Korra").limit(1).first().now();

    Integer position = 9;
    //Pair<Optional<WordItemKind> page.get(position);
  }
}
