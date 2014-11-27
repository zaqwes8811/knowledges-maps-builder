package gae_store_space;

import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.Closeable;
import org.junit.Test;
import pipeline.Unigram;

import java.util.ArrayList;

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
      Unigram o1 = Unigram.create("hello", s, 1);
      Unigram o2 = Unigram.create("dfasdf", s, 1);

      assert 0 == Unigram.createImportanceComparator().compare(o1, o2);
    }
  }
}
