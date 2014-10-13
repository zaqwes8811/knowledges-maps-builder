package gae_store_space;

import org.junit.Test;

/**
 * Created by zaqwes on 5/12/14.
 */
public class WordItemKindTest {
  @Test
  public void testCompare() throws Exception {
    WordKind o1 = new WordKind("hello");
    o1.setRawFrequency(1);
    WordKind o2 = new WordKind("dfasdf");
    o2.setRawFrequency(1);

    assert 0 == WordKind.createFrequencyComparator().compare(o1, o2);

    o2.setRawFrequency(2);
  }
}
