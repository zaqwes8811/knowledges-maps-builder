import backend.DictionaryKind;
import backend.GoogleTranslatorRecord;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.common.base.Optional;
import com.google.common.io.Closer;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.Closeable;
import backend.OfyService;
import org.apache.log4j.BasicConfigurator;
import org.jsefa.Deserializer;
import org.jsefa.csv.CsvIOFactory;
import org.jsefa.csv.config.CsvConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import pipeline.ContentItem;
import pipeline.UniGram;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zaqwes on 5/12/14.
 */
public class WordKindTest {
    private static final LocalServiceTestHelper helper =
            new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig()
                    .setDefaultHighRepJobPolicyUnappliedJobPercentage(10));

    private static final String csvFn = "../../materials/Phrasebook - Sheet 1.csv";

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
    public void testGoogleCsv()throws IOException {
        Closer closer = Closer.create();
        List<GoogleTranslatorRecord> records = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(csvFn), "UTF8"));
            closer.register(reader);

            CsvConfiguration csvConfig = new CsvConfiguration();
            csvConfig.setFieldDelimiter(',');
            csvConfig.setLineBreak("\n");
            csvConfig.setQuoteCharacter('\"');

            Deserializer deserializer = CsvIOFactory.createFactory(csvConfig,
                    GoogleTranslatorRecord.class).createDeserializer();
            deserializer.open(reader);

            while (deserializer.hasNext()) {
                GoogleTranslatorRecord p = deserializer.next();
                if(p.from.equals("English")){
                    records.add(p);
                }
            }
            deserializer.close(true);

        } catch (Throwable e) {
            closer.rethrow(e);
        } finally {
            closer.close();
        }

        // Сохраняем
        try (Closeable c = ObjectifyService.begin()) {
            // Нельзя сохранять поштучно
            // Строки тоже короткие

            DictionaryKind dict = new DictionaryKind();
            dict.records = records;
            dict.name = "1";

            dict.persist();
            assert dict.id != null;

            // load
            Optional<DictionaryKind> loaded = OfyService.getPageKind("1");
//            assert loaded.isPresent();
        }
    }
}
