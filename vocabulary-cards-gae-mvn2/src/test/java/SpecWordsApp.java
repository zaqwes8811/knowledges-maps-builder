import backend.nlp.PlainTextTokenizer;
import backend.nlp.SentenceTokenizer;
import backend.text_extractors.SpecialSymbols;
import backend.text_extractors.SubtitlesContentHandler;
import backend.text_extractors.SubtitlesParser;
import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.io.Closer;
import org.apache.tika.parser.Parser;
import org.junit.Test;
import org.xml.sax.ContentHandler;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;


public class SpecWordsApp {
    void cout(String str) {
        System.out.println(str);
    }

    @Test
    public void select() {
        String str_filename = System.getenv("STR_FILENAME");
        String regex = System.getenv("PATTERN");
//        Pattern pattern = Pattern.compile(regex);
        Pattern p = Pattern.compile(regex);

        cout("Orig:" + str_filename);

        File tmpDir = new File(str_filename);
        boolean exists = tmpDir.exists();
        if (!exists) {
            cout("No file:" + str_filename);
            return;
        }
        // FIXME: побольше контекста вокруг
        // FIXME: файлы пачкой по расширению
        // FIXME: проверить что это srt-файлы

        try {
            // Пока файл строго юникод - UTF-8
            Closer closer = Closer.create();
            try {
                File file = new File(str_filename);
                byte[] content = Files.readAllBytes(file.toPath());
                InputStream in = closer.register(new ByteArrayInputStream(content));
                Parser parser = new SubtitlesParser();
                List<String> sink = new ArrayList<String>();
                ContentHandler handler = new SubtitlesContentHandler(sink);
                parser.parse(in, handler, null, null);

                // Получили список строк.
                SpecialSymbols symbols = new SpecialSymbols();
                String resp = Joiner.on(symbols.WHITESPACE_STRING).join(sink);

                // could be split to sentences
//                SentenceTokenizer tokenizer = new SentenceTokenizer();
                PlainTextTokenizer tokenizer = new PlainTextTokenizer();
                List<String> rows = tokenizer.getSentences(resp);

                // https://stackoverflow.com/questions/9464261/how-to-find-the-exact-word-using-a-regex-in-java
                for (String v : rows) {
                    Matcher m = p.matcher(v);
                    List<String> matches = new ArrayList<>();
                    while (m.find()) {
                        matches.add(m.group(0));
                    }
                    if (!matches.isEmpty()) {
                        cout(v);
                    }
                }

            } catch (Throwable e) {
                throw closer.rethrow(e);
            } finally {
                closer.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}


