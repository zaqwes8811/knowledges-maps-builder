package com.github.zaqwes8811.processor_word_frequency_index.spiders_extractors;

import com.github.zaqwes8811.processor_word_frequency_index.AppConstants;
import com.github.zaqwes8811.processor_word_frequency_index.crosscuttings.CrosscuttingsException;
import com.github.zaqwes8811.processor_word_frequency_index.crosscuttings.ImmutableAppConfigurator;
import com.github.zaqwes8811.processor_word_frequency_index.crosscuttings.ImmutableProcessorTargets;

import java.util.List;


public class MinimalSpiderExtractor {
  // Main()
  public static void main(String [ ] args) {
    try {
      // Получаем цели
      String spiderTargetsFilename = AppConstants.SPIDER_TARGETS_FILENAME;
      List<List<String>> targets = ImmutableProcessorTargets.runParser(spiderTargetsFilename);
      for (List<String> target : targets) {
        String nodeName = target.get(ImmutableProcessorTargets.RESULT_NODE_NAME);
        String pathToFile = target.get(ImmutableProcessorTargets.RESULT_PATH);
        String fileName = target.get(ImmutableProcessorTargets.RESULT_FILENAME);

        // TODO(zaqwes): если файл существует, то будет перезаписан. Нужно хотя бы предупр.
        ImmutableTikaWrapper.extractAndSaveText(fileName, pathToFile, nodeName);
        ImmutableTikaWrapper.extractAndSaveMetadata(fileName, pathToFile, nodeName);
        //break;  // DEVELOP
      }
    } catch (CrosscuttingsException e) {
      System.out.println(e.getMessage());
    }
  }
}
