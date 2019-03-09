import backend.nlp.PlainTextTokenizer;
import backend.text_extractors.SpecialSymbols;
import backend.text_extractors.SubtitlesContentHandler;
import backend.text_extractors.SubtitlesParser;
import com.google.common.base.Joiner;
import com.google.common.io.Closer;
import org.apache.tika.parser.Parser;
import org.javatuples.Tuple;
import org.javatuples.Pair;
import org.xml.sax.ContentHandler;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class SrtLoader {
    void cout(String str) {
        System.out.println(str);
    }

    public class ReturnValue {
        List<String> rows = new ArrayList<>();

        // [beg, end)
        List<List<Pair<Integer, Integer>>> ptrs = new ArrayList<>();
    }

    public List<String> getSentences(String srtFilename) {

        File tmpDir = new File(srtFilename);

        List<String> result = new ArrayList<>();
        boolean exists = tmpDir.exists();
        if (!exists) {
            cout("No file:" + srtFilename);
            return result;
        }
        // FIXME: побольше контекста вокруг
        // FIXME: файлы пачкой по расширению
        // FIXME: проверить что это srt-файлы

        try {
            // Пока файл строго юникод - UTF-8
            Closer closer = Closer.create();
            try {
                File file = new File(srtFilename);
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
                result = rows;
            } catch (Throwable e) {
                throw closer.rethrow(e);
            } finally {
                closer.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public ReturnValue select(List<String> rows, String regex) {
        ReturnValue rt = new ReturnValue();
        Pattern p = Pattern.compile(regex);
        // https://stackoverflow.com/questions/9464261/how-to-find-the-exact-word-using-a-regex-in-java
        for (final String v : rows) {
            Matcher m = p.matcher(v);
            List<Pair<Integer, Integer>> matches = new ArrayList<>();

            // Индексы
            while (m.find()) {
                Pair val = Pair.with(m.start(), m.end());
                matches.add(val);
            }
            if (!matches.isEmpty()) {
                rt.ptrs.add(matches);
                rt.rows.add(v);
            }
        }
        return rt;
    }
}
