package business.mapreduce;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import dal.gae_kinds.ContentItem;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class CounterMapReduceTest {
  private List<ContentItem> getContentItems() {
    List<ContentItem> sentences = new ArrayList<ContentItem>();
    sentences.add(new ContentItem("hello"));
    sentences.add(new ContentItem("hello"));
    sentences.add(new ContentItem("world"));
    return sentences;
  }

  @Test
  public void testRun() throws Exception {
    // build
    Multimap<String, ContentItem> wordHistogram = HashMultimap.create();
    CountReducer reducer = new CountReducer(wordHistogram);
    CounterMapper mapper = new CounterMapper(reducer);

    // work
    List<ContentItem> contentItems = getContentItems();

    // Connect to page

    // Split
    mapper.map(contentItems);

    // Persist content items
    // Persist page
    // Persist words

    //Set<String> keys = wordHistogram.elementSet();
    //assert wordHistogram.count("hello") == 2;
    //assert keys.size() == 2;
  }
}
