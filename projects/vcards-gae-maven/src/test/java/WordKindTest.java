import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.Closeable;
import gae_store_space.OfyService;
import kinds.GoogleTranslatorKind;
import org.apache.log4j.BasicConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import pipeline.ContentItem;
import pipeline.UniGram;

import java.util.ArrayList;

/**
 * Created by zaqwes on 5/12/14.
 */
public class WordKindTest {
    private static final LocalServiceTestHelper helper =
            new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig()
                    .setDefaultHighRepJobPolicyUnappliedJobPercentage(50));

    @Before
    public void setUp() {
        BasicConfigurator.configure();
        helper.setUp();
    }

    @After
    public void tearDown() {
        try (Closeable c = ObjectifyService.begin()) {
            OfyService.clearStore();
        }
        helper.tearDown();
    }

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

    @Test
    public void testGoogleCsv() {
        try (Closeable c = ObjectifyService.begin()) {
            GoogleTranslatorKind kind = new GoogleTranslatorKind();
            kind.persist(kind);

            assert kind.id != null;
        }
    }
}
