package spiders_extractors;

import com.github.zaqwes8811.text_processor.AppConstants;
import com.github.zaqwes8811.text_processor.common.ImmutableAppUtils;
import com.github.zaqwes8811.text_processor.crosscuttings.CrosscuttingsException;
import com.github.zaqwes8811.text_processor.jobs_processors.ImmutableProcessorTargets;
//import org.apache.log4j.BasicConfigurator;
//import org.apache.log4j.Logger;

import java.util.List;


public class MinimalSpiderExtractor {
  //private static Logger log = Logger.getLogger(MinimalSpiderExtractor.class);
  // Main()
  public static void main(String [ ] args) {
    //BasicConfigurator.configure();
    try {
      // Получаем цели
      List<List<String>> targets = ImmutableProcessorTargets.runParser(AppConstants.SPIDER_TARGETS_FILENAME);
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
      ImmutableAppUtils.print(e.getMessage());
    }
    ImmutableAppUtils.print("Done. Spider extractor.\n");
  }
}
