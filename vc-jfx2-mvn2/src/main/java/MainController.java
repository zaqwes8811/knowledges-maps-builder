import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import backend.nlp.PlainTextTokenizer;
import backend.text_extractors.SpecialSymbols;
import backend.text_extractors.SubtitlesContentHandler;
import backend.text_extractors.SubtitlesParser;
import com.google.common.base.Joiner;
import com.google.common.io.Closer;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.apache.tika.parser.Parser;
import org.xml.sax.ContentHandler;

class StrLoader {
    void cout(String str) {
        System.out.println(str);
    }

    public List<String> select(String str_filename, String regex) {
        Pattern p = Pattern.compile(regex);

        cout("Orig:" + str_filename);

        File tmpDir = new File(str_filename);

        List<String> result = new ArrayList<>();
        boolean exists = tmpDir.exists();
        if (!exists) {
            cout("No file:" + str_filename);
            return result;
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
                        result.add(v);
                    }
                }
//                result = rows;

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
}


// https://stackoverflow.com/questions/42497507/javafx-fxml-controller-event-handler-and-initialization-best-practice
public class MainController {

    @FXML
    public Button hwBt;

    @FXML
    public Button applyBt;

    @FXML
    public TextField folderTf;

    @FXML
    public TextField regexTf;

    @FXML
    private TextArea respTa;

    public Stage stage = null;

    // Add a public no-args constructor
    public MainController() {
    }

    public File[] finder(String dirName) {
        File dir = new File(dirName);

        return dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String filename) {
                return filename.endsWith(".srt");
            }
        });

    }

    private StrLoader strLoader = null;

    private String readDefaultPath() {
        Preferences pref;
        pref = Preferences.userNodeForPackage(MainController.class);
        return pref.get("defaultRoot", "/mnt");
    }

    private void saveDefaultRootPath(String startRoot) {
        Preferences pref;
        pref = Preferences.userNodeForPackage(MainController.class);
        pref.put("defaultRoot", startRoot);
    }

    @FXML
    private void initialize() {
        strLoader = new StrLoader();

        folderTf.setText(readDefaultPath());
        regexTf.setText("have");

        hwBt.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {

                DirectoryChooser directoryChooser = new DirectoryChooser();
                directoryChooser.setInitialDirectory(new File(readDefaultPath()));

                File selectedDirectory = directoryChooser.showDialog(stage);

                if (selectedDirectory == null) {
                    //No Directory selected
                    return;
                }

                String selectedPath = selectedDirectory.getAbsolutePath();
                saveDefaultRootPath(selectedPath);
                folderTf.setText(selectedPath);
            }
        });

        applyBt.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                // Load files
                File[] files = finder(readDefaultPath());
                String regex = regexTf.getText();
                List<String> l = new ArrayList<>();
                for (File f : files) {
                    String fn = f.getAbsolutePath();
                    List<String> resp = strLoader.select(fn, regex);
                    l.addAll(resp);
                }

                // draw
                if (!l.isEmpty()) {
                    respTa.setText(Joiner.on("\n").join(l));
                }
            }
        });
    }

}
