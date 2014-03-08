package write_chain.spiders_extractors;

import business.nlp.TikaWrapper;
import common.InnerReuse;
import write_chain.jobs_processors.ProcessorTargets;
import through_functional.AppConstants;
import through_functional.ThroughLevelBoundaryError;

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
