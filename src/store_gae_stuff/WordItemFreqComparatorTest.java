package store_gae_stuff;

import org.junit.Test;

/**
 * Created by zaqwes on 5/12/14.
 */
public class WordItemFreqComparatorTest {
  @Test
  public void testCompare() throws Exception {
    WordItem o1 = new WordItem("hello");
    o1.setRawFrequency(1);
    WordItem o2 = new WordItem("dfasdf");
    o2.setRawFrequency(1);

    assert 0 == WordItem.createFrequencyComparator().compare(o1, o2);

    o2.setRawFrequency(2);
  }
}
