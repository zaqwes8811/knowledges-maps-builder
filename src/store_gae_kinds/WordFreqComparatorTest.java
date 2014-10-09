package store_gae_kinds;

import org.junit.Test;

/**
 * Created by zaqwes on 5/12/14.
 */
public class WordFreqComparatorTest {
  @Test
  public void testCompare() throws Exception {
    Word o1 = new Word("hello");
    o1.setRawFrequency(1);
    Word o2 = new Word("dfasdf");
    o2.setRawFrequency(1);

    assert 0 == Word.createFreqComparator().compare(o1, o2);

    o2.setRawFrequency(2);
  }
}
