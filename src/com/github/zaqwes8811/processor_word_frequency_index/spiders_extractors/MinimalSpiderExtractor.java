package com.github.zaqwes8811.processor_word_frequency_index.spiders_extractors;

import com.github.zaqwes8811.processor_word_frequency_index.AppConstants;
import com.github.zaqwes8811.processor_word_frequency_index.common.ImmutableAppUtils;
import com.github.zaqwes8811.processor_word_frequency_index.crosscuttings.CrosscuttingsException;
import com.github.zaqwes8811.processor_word_frequency_index.jobs_processors.ImmutableProcessorTargets;

import java.util.List;


public class MinimalSpiderExtractor {
  // Main()
  public static void main(String [ ] args) {
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
      System.out.println(e.getMessage());
    }
  }
}
