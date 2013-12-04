package spiders_extractors;


//import org.apache.log4j.BasicConfigurator;
//import org.apache.log4j.Logger;

import common.InnerReuse;
import jobs_processors.ProcessorTargets;
import through_functional.AppConstants;
import through_functional.CrosscuttingsException;

import java.util.List;


public class MinimalSpiderExtractor {
  //private static Logger log = Logger.getLogger(MinimalSpiderExtractor.class);
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
    } catch (CrosscuttingsException e) {
      InnerReuse.print(e.getMessage());
    }
    InnerReuse.print("Done. Spider extractor.\n");
  }
}
