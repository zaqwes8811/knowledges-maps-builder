package pipeline;

import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.Closeable;
import org.junit.Test;
import pipeline.ContentItem;
import pipeline.UniGram;

import java.util.ArrayList;

/**
 * Created by zaqwes on 5/12/14.
 */
public class WordKindTest {
  @Test
  public void testCompare() throws Exception {
    try (Closeable c = ObjectifyService.begin()) {
      ContentItem kind = new ContentItem("fake");
      ArrayList<ContentItem> s = new ArrayList<ContentItem>();
      s.add(kind);
      UniGram o1 = UniGram.create("hello", s, 1);
      UniGram o2 = UniGram.create("dfasdf", s, 1);

      assert 0 == UniGram.createImportanceComparator().compare(o1, o2);
    }
  }
}
