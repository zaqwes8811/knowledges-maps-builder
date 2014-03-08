package write_chain.spiders_extractors;//package com.github.zaqwes8811.text_processor.sandbox;


import business.nlp.TikaWrapper;
import common.InnerReuse;
import write_chain.jobs_processors.ProcessorTargets;
import through_functional.AppConstants;
import through_functional.ThroughLevelBoundaryError;
import org.junit.Test;

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
        List<List<String>> targets = ProcessorTargets.runParser(spiderTargetsFilename);
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
        System.out.println(e.getMessage());
      }
    }
}
