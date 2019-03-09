

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class SrtLoaderTest {
    void cout(String str) {
        System.out.println(str);
    }

    @Test
    public void select() {
        SrtLoader srtParser = new SrtLoader();
        List<String> row = new ArrayList<>();
        row.add("have have hy have");
        srtParser.select(row, "have");
    }

}
