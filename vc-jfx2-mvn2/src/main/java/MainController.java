// Text highlighting
// https://stackoverflow.com/questions/6530105/highlighting-text-in-java - Swing
// https://stackoverflow.com/questions/9128535/highlighting-strings-in-javafx-textarea - javafx
// ReachTextFx
// https://github.com/FXMisc/RichTextFX/issues/442
//
// https://ru.stackoverflow.com/questions/861957/%D0%98%D1%81%D0%BF%D0%BE%D0%BB%D1%8C%D0%B7%D0%BE%D0%B2%D0%B0%D0%BD%D0%B8%D0%B5-richtextfx

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.fxmisc.richtext.InlineCssTextArea;
import org.fxmisc.richtext.StyleClassedTextArea;
import org.javatuples.Pair;


// https://stackoverflow.com/questions/42497507/javafx-fxml-controller-event-handler-and-initialization-best-practice
public class MainController {
    // FIXME: Сохранять бы позицию полосы прокрутки
    // FIXME: считаь файлы раз
    // FIXME: Добавить заметки, чтобы можно было копировать регексы

    @FXML
    public Button hwBt;

    @FXML
    public Button applyBt;

    @FXML
    public TextField folderTf;

    @FXML
    public TextField regexTf;

    @FXML
    private InlineCssTextArea textReachTa;


    @FXML
    private TextArea notesTa;

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

    private SrtLoader srtLoader = null;

    private String readDefaultPath() {
        return read("defaultRoot", "/mnt");
    }

    private void saveDefaultRootPath(String startRoot) {
        save("defaultRoot", startRoot);
    }

    private String read(String key, String defaultVal) {
        Preferences pref;
        pref = Preferences.userNodeForPackage(MainController.class);
        return pref.get(key, defaultVal);
    }

    private void save(String key, String val) {
        Preferences pref;
        pref = Preferences.userNodeForPackage(MainController.class);
        pref.put(key, val);
    }

    @FXML
    private void initialize() {
        srtLoader = new SrtLoader();

        folderTf.setText(readDefaultPath());
        regexTf.setText(read("lastRegex", "have"));

        // http://qaru.site/questions/788947/javafx-textarea-hiding-scroll-bars
//        ScrollBar scrollBarv = (ScrollBar) textReachTa.lookup(".scroll-bar:vertical");
//        scrollBarv.setDisable(false);

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

                loadText();
            }
        });

        applyBt.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                applyRegex();
            }
        });

        loadText();
//        applyRegex();
    }

    private void applyRegex() {
        // С нуля
        textReachTa.clear();
        textReachTa.setStyle(0, textReachTa.getLength(), RD);

        // Load files
        String regex = regexTf.getText();
        for (List<String> rows : content) {
            SrtLoader.ReturnValue resp = srtLoader.select(rows, regex);

            for (int i = 0; i < resp.ptrs.size(); ++i) {
                // prev
                String prev = resp.prevs.get(i);
                if (!prev.isEmpty()) {
                    textReachTa.appendText(prev);
                }

                // current
                String message = resp.rows.get(i);
                int base = textReachTa.getLength(); // here
                textReachTa.appendText(message);
                List<Pair<Integer, Integer>> ptrs = resp.ptrs.get(i);

                for (int j = 0; j < ptrs.size(); ++j) {
                    int from = base + ptrs.get(j).getValue0();
                    int to = base + ptrs.get(j).getValue1();
                    // добавили сообещние

                    // указали для него стиль
                    textReachTa.setStyle(from, to, SEL);
                }

                // next
                String next = resp.nexts.get(i);
                if (!next.isEmpty()) {
                    textReachTa.appendText(next);
                }
                textReachTa.appendText("\n\n");
            }
        }


        save("lastRegex", regex);
    }

    private void loadText() {
        content.clear();
        File[] files = finder(readDefaultPath());
        for (File f : files) {
            String fn = f.getAbsolutePath();
            List<String> rows = srtLoader.getSentences(fn);
            content.add(rows);
        }
    }

    private List<List<String>> content = new ArrayList<>();

    private static final String SEL = "-rtfx-background-color: lightgreen;";
    private static final String RD = "-rtfx-background-color: white;";
}
