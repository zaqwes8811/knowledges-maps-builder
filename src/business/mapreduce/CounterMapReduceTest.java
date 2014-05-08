package business.mapreduce;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CounterMapReduceTest {
  private List<String> getContentItems() {
    List<String> sentences = new ArrayList<String>();
    sentences.add("hello");
    sentences.add("hello");
    sentences.add("world");
    return sentences;
  }

  @Test
  public void testRun() throws Exception {
    // build
    Multiset<String> wordHistogram = HashMultiset.create();
    CountReducer reducer = new CountReducer(wordHistogram);
    CounterMapper mapper = new CounterMapper(reducer);

    // work
    List<String> contentItems = getContentItems();
    // Persist items

    // Connect to page

    mapper.map(contentItems);

    Set<String> keys = wordHistogram.elementSet();
    assert wordHistogram.count("hello") == 2;
    assert keys.size() == 2;
  }
}
