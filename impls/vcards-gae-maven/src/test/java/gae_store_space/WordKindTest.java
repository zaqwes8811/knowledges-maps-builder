package gae_store_space;

import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.Closeable;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by zaqwes on 5/12/14.
 */
public class WordKindTest {
  @Test
  public void testCompare() throws Exception {
    try (Closeable c = ObjectifyService.begin()) {
      SentenceKind kind = new SentenceKind("fake");
      ArrayList<SentenceKind> s = new ArrayList<SentenceKind>();
      s.add(kind);
      NGramKind o1 = NGramKind.create("hello", s, 1, null);
      NGramKind o2 = NGramKind.create("dfasdf", s, 1, null);

      assert 0 == NGramKind.createImportanceComparator().compare(o1, o2);
    }
  }
}
