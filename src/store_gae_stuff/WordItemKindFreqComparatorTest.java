package store_gae_stuff;

import org.junit.Test;

/**
 * Created by zaqwes on 5/12/14.
 */
public class WordItemKindFreqComparatorTest {
  @Test
  public void testCompare() throws Exception {
    WordItemKind o1 = new WordItemKind("hello");
    o1.setRawFrequency(1);
    WordItemKind o2 = new WordItemKind("dfasdf");
    o2.setRawFrequency(1);

    assert 0 == WordItemKind.createFrequencyComparator().compare(o1, o2);

    o2.setRawFrequency(2);
  }
}
