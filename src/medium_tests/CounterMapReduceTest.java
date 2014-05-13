package medium_tests;

import business.mapreduce.CountReducer;
import business.mapreduce.CounterMapper;
import business.math.GeneratorAnyDistribution;
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
import dal.gae_kinds.ContentPageKind;
import dal.gae_kinds.Word;
import org.apache.tika.parser.Parser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.ContentHandler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static dal.gae_kinds.OfyService.ofy;
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

  private String getTestText(String filename) throws IOException {
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
      return Joiner.on(symbols.WHITESPACE_STRING).join(sink);
    } catch (Throwable e) {
      throw closer.rethrow(e);
    } finally {
      closer.close();
    }
  }

  private ArrayList<ContentItem> getItems(String text) {
    ImmutableList<String> sentences = new ContentItemsTokenizer().getSentences(text);
    assertFalse(sentences.isEmpty());

    // Пакуем
    ArrayList<ContentItem> contentItems1 = new ArrayList<ContentItem>();
    Long idx = new Long(1);
    for (String sentence: sentences) {
      ContentItem item = new ContentItem(sentence);
      item.setIdx(idx);
      contentItems1.add(item);
      idx++;
    }

    return contentItems1;
  }

  @Test
  public void testRun() throws Exception {
    // TODO: Extract class
    String filename = "/home/zaqwes/work/statistic/the.legend.of.korra.a.new.spiritual.age.(2013).eng.1cd.(5474296)/" +
        "The Legend of Korra - 02x10 - A New Spiritual Age.WEB-DL.BS.English.HI.C.orig.Addic7ed.com.srt";

    // Phase I
    String text = getTestText(filename);
    assertFalse(text.isEmpty());

    // Phase II не всегда они разделены, но с случае с субтитрами точно разделены.
    ArrayList<ContentItem> contentItems = getItems(text);

    /// ClassEdge
    Multimap<String, ContentItem> wordHistogram = HashMultimap.create();
    CountReducer reducer = new CountReducer(wordHistogram);
    CounterMapper mapper = new CounterMapper(reducer);

    // Split
    mapper.map(contentItems);  // TODO: implicit, but be so

    // WARNING: Порядок важен!
    // Persist content items
    ofy().save().entities(contentItems).now();

    // Persist words
    List<Word> words = new ArrayList<Word>();
    for (String word: wordHistogram.keySet()) {
      Collection<ContentItem> value = wordHistogram.get(word);
      Word wordObj = Word.create(word, value);
      words.add(wordObj);
    }

    // Sort words by frequency and assign idx
    Collections.sort(words, Word.createFreqComparator());
    Collections.reverse(words);
    ArrayList<Integer> distribution = new ArrayList<Integer>();
    for (int i = 0; i < words.size(); i++) {
      words.get(i).setSortedIdx(i);

      // нужны частоты для распределения
      distribution.add(words.get(i).getFrequency());
    }

    ofy().save().entities(words).now();

    // Заряжаем генератор
    GeneratorAnyDistribution gen = GeneratorAnyDistribution.create(distribution);

    // Last - Persist page
    ContentPageKind page = ContentPageKind.createFromComponents("Korra", contentItems, words);

    ofy().save().entity(page).now();

    /// Queries
    // Получаем все сразу, но это никчему. Можно передать подсписок, но это не то что хотелось бы.
    // Хотелось бы выбирать по некоторому критерию.
    // https://groups.google.com/forum/#!topic/objectify-appengine/scb3xNPFszE
    // http://stackoverflow.com/questions/9867401/objectify-query-filter-by-list-in-entity-contains-search-parameter
    // http://bighow.net/3869301-Objectify___how_to__Load_a_List_lt_Ref_lt___gt__gt__.html
    //
    // http://stackoverflow.com/questions/11924572/using-in-query-in-objectify
    //
    // https://www.mail-archive.com/google-appengine-java@googlegroups.com/msg09389.html
    Integer idxPosition = gen.getPosition();
    int countFirst = 4;
    Word elem = ofy().load().type(Word.class).filter("sortedIdx =", idxPosition).first().get();
    List<ContentItem> coupled = ofy().load().type(ContentItem.class)
      .filterKey("in", elem.getItems())
      //.filter("idx <=", 8)
      .limit(countFirst)
      .list();

    // TODO: Delete full page
  }
}
