package pipeline.evaluators;

import com.google.common.base.Joiner;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NotesBuilderTest {
  @Test
  public void testCreating() {
    List<String> rpt = new ArrayList<String>(
      Arrays.asList(Joiner.on(";")
        .join(
          "Имя документа",
          "Флеш",
          "Время прочтения",
          "Ср. дл. предл.",
          "20% частых",
          "80% редких",
          "частые сост. 80% слов. состава",
          "редкие - 20% состава")));
  }
}
