import com.github.zaqwes8811.processor_word_frequency_index.AppConstants;
import com.github.zaqwes8811.processor_word_frequency_index.common.ImmutableAppUtils;
import com.github.zaqwes8811.processor_word_frequency_index.jobs_processors.ImmutableProcessorTargets;
import com.github.zaqwes8811.processor_word_frequency_index.spiders_extractors.ExtractorException;
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
          try {
            // TODO(zaqwes): если файл существует, то будет перезаписан. Нужно хотя бы предупр.
            ImmutableTikaWrapper.extractAndSaveText(target);
            ImmutableTikaWrapper.extractAndSaveMetadata(target);
            //break;  // DEVELOP
          } catch (ExtractorException e) {
            // Ошибка может произойти на каждой итерации, но пусть обработка предолжается
            ImmutableAppUtils.print(e.getMessage());
          }
        }

      } catch (CrosscuttingsException e) {
        System.out.println(e.getMessage());
      }
    }
}
