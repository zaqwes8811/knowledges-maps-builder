import com.github.zaqwes8811.processor_word_frequency_index.AppConstants;
import com.github.zaqwes8811.processor_word_frequency_index.crosscuttings.ImmutableAppConfigurator;
import com.github.zaqwes8811.processor_word_frequency_index.crosscuttings.ImmutableProcessorTargets;
import com.github.zaqwes8811.processor_word_frequency_index.spiders_extractors.ImmutableTikaWrapper;
import org.junit.Test;

import com.github.zaqwes8811.processor_word_frequency_index.crosscuttings.CrosscuttingsException;

import java.util.List;

public class SpiderExtractorTest {
  static void print(Object msg) {
     System.out.println(msg);

  }

  @Test
  public void testDevelopSpider() {
      try {
        // Получаем цели
        String spiderTargetsFilename = AppConstants.SPIDER_TARGETS_FILENAME;
        List<List<String>> targets = ImmutableProcessorTargets.runParser(spiderTargetsFilename);
        for (List<String> target : targets) {
          // TODO(zaqwes): если файл существует, то будет перезаписан. Нужно хотя бы предупр.

          // Выделяем текст
           ImmutableTikaWrapper.extractAndSaveText(target);
          //break;
        }

      } catch (CrosscuttingsException e) {
        System.out.println(e.getMessage());
      }
    }
}
