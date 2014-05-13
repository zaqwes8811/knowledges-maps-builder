package hided.spiders_extractors;

import business.adapters_3rdparty.TikaWrapper;
import common.InnerReuse;
import hided.jobs_processors.ProcessorTargets;
import hided.crosscuttings.AppConstants;
import hided.crosscuttings.ThroughLevelBoundaryError;

import java.util.List;


public class SpiderExtractor {
  //private static Logger log = Logger.getLogger(SpiderExtractor.class);
  // Main()
  public static void main(String [ ] args) {
    //BasicConfigurator.configure();
    try {
      // Получаем цели
      List<List<String>> targets = ProcessorTargets.runParser(AppConstants.SPIDER_TARGETS_FILENAME);
      for (List<String> target : targets) {
        try {
          // TODO(zaqwes): если файл существует, то будет перезаписан. Нужно хотя бы предупр.
          TikaWrapper.extractAndSaveText(target);
          TikaWrapper.extractAndSaveMetadata(target);
          //break;  // DEVELOP
        } catch (ExtractorException e) {
          // Ошибка может произойти на каждой итерации, но пусть обработка предолжается
          InnerReuse.print(e.getMessage());
        }
      }
    } catch (ThroughLevelBoundaryError e) {
      InnerReuse.print(e.getMessage());
    }
    InnerReuse.print("Done. Spider extractor.\n");
  }
}
