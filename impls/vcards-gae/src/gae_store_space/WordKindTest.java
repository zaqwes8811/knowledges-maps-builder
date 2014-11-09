package gae_store_space;

import org.junit.Test;

/**
 * Created by zaqwes on 5/12/14.
 */
public class WordKindTest {
  @Test
  public void testCompare() throws Exception {
    NGramKind o1 = NGramKind.create("hello", null, 1, null);
    NGramKind o2 = NGramKind.create("dfasdf", null, 1, null);

    assert 0 == NGramKind.createImportanceComparator().compare(o1, o2);
  }
}
