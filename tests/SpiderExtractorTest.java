import com.github.zaqwes8811.text_processor.AppConstants;
import com.github.zaqwes8811.text_processor.common.ImmutableAppUtils;
import com.github.zaqwes8811.text_processor.jobs_processors.ImmutableProcessorTargets;
import com.github.zaqwes8811.text_processor.spiders_extractors.ExtractorException;
import com.github.zaqwes8811.text_processor.spiders_extractors.ImmutableTikaWrapper;
import org.junit.Test;

import com.github.zaqwes8811.text_processor.crosscuttings.CrosscuttingsException;

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
