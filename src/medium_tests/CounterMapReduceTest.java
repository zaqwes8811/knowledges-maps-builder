package medium_tests;

import business.mapreduce.CountReducer;
import business.mapreduce.CounterMapper;
import business.nlp.ContentItemsTokenizer;
import business.text_extractors.SpecialSymbols;
import business.text_extractors.SubtitlesContentHandler;
import business.text_extractors.SubtitlesParser;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import com.google.common.io.Closer;
import common.Util;
import dal.gae_kinds.ContentItem;
import dal.gae_kinds.ContentPage;
import org.apache.tika.parser.Parser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.ContentHandler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static medium_tests.OfyService.ofy;
import static org.junit.Assert.assertFalse;

public class CounterMapReduceTest {
  private static final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

  @Before
  public void setUp() {
    helper.setUp();
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  private List<ContentItem> getContentItems() {
    List<ContentItem> sentences = new ArrayList<ContentItem>();
    sentences.add(new ContentItem("hello"));
    sentences.add(new ContentItem("hello"));
    sentences.add(new ContentItem("world"));
    return sentences;
  }

  private ImmutableList<ContentItem> getItems() throws IOException {
    String filename = "/home/zaqwes/work/statistic/the.legend.of.korra.a.new.spiritual.age.(2013).eng.1cd.(5474296)/" +
      "The Legend of Korra - 02x10 - A New Spiritual Age.WEB-DL.BS.English.HI.C.orig.Addic7ed.com.srt";
    String rawText = Joiner.on('\n').join(Util.fileToList(filename));
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
      String text = Joiner.on(symbols.WHITESPACE_STRING).join(sink);
      assertFalse(text.isEmpty());

      ImmutableList<String> sentences = new ContentItemsTokenizer().getSentences(text);
      assertFalse(sentences.isEmpty());

      // Пакуем
      List<ContentItem> contentItems = new ArrayList<ContentItem>();
      Long idx = new Long(1);
      for (String sentence: sentences) {
        ContentItem item = new ContentItem(sentence);
        item.setIdx(idx);
        contentItems.add(item);
        idx++;
      }

      return ImmutableList.copyOf(contentItems);
    } catch (Throwable e) {
      throw closer.rethrow(e);
    } finally {
      closer.close();
    }
  }

  @Test
  public void testRun() throws Exception {
    // build
    Multimap<String, ContentItem> wordHistogram = HashMultimap.create();
    CountReducer reducer = new CountReducer(wordHistogram);
    CounterMapper mapper = new CounterMapper(reducer);

    // work
    ImmutableList<ContentItem> contentItems = getItems();

    // Connect to page

    // Split
    mapper.map(contentItems);  // TODO: implicit, but be so

    // WARNING: Порядок важен!
    // Persist content items
    ofy().save().entities(contentItems).now();

    // Persist page
    ContentPage page = new ContentPage("Korra");
    page.setItems(contentItems);  // TODO: Подключить в именованном конструкторе
    ofy().save().entity(page).now();

    // Queries - failed
    //List<ContentItem> list = ofy().load().type(ContentItem.class).filterKey(page.getItems());
    // TODO: Work but bad!! Как делать запросы не ясно. Похоже нужна ссылка и на страницу - бред.
    // Получаем все сразу, но это никчему. Можно передать подсписок, но это не то что хотелось бы.
    // Хотелось бы выбирать по некоторому критерию.
    // https://groups.google.com/forum/#!topic/objectify-appengine/scb3xNPFszE
    // http://stackoverflow.com/questions/9867401/objectify-query-filter-by-list-in-entity-contains-search-parameter
    // http://bighow.net/3869301-Objectify___how_to__Load_a_List_lt_Ref_lt___gt__gt__.html
    Collection<ContentItem> i = ofy().load().keys(page.getItems()).values();

    // Persist words

    // Delete full page
  }
}
