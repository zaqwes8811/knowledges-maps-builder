package com.github.zaqwes8811.processor_word_frequency_index.spiders_extractors;

import com.github.zaqwes8811.processor_word_frequency_index.AppConstants;
import com.github.zaqwes8811.processor_word_frequency_index.crosscuttings.AppConfigurer;
import com.github.zaqwes8811.processor_word_frequency_index.crosscuttings.CrosscuttingsException;
import com.github.zaqwes8811.processor_word_frequency_index.crosscuttings.ProcessorTargets;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: кей
 * Date: 08.05.13
 * Time: 16:45
 * To change this template use File | Settings | File Templates.
 */
public class MinimalSpiderExtractor {
  static void print(Object msg) {
    System.out.println(msg);
  }


  // Main()
  public static void main(String [ ] args) {
    try {
      // Получаем путь к папке приложения
      //AppConfigurer configurer = new AppConfigurer();
      String pathToAppFolder = AppConfigurer.getPathToAppFolder();
      print(pathToAppFolder);

      // Получаем имя индекса
      ProcessorTargets processorTargets = new ProcessorTargets();
      String idxName = processorTargets.getIndexName();

      // Получаем цели
      /*String spiderTargetsFilename = AppConstants.SPIDER_TARGETS_FILENAME;
      List<List<String>> targets = processorTargets.runParser(spiderTargetsFilename);
      for (List<String> target : targets) {
        String nodeName = target.get(ProcessorTargets.RESULT_NODE_NAME);
        String pathToFile = target.get(ProcessorTargets.RESULT_PATH);
        String fileName = target.get(ProcessorTargets.RESULT_FILENAME);

        // TODO(zaqwes): если файл существует, то будет перезаписан. Нужно хотя бы предупр.
        // Выделяем текст
        // Нужно передать имя исходного файла, и путь к итоговому(без расширения)
        ImmutableTikaWrapper tikaWrapper = new ImmutableTikaWrapper(pathToAppFolder, idxName);
        tikaWrapper.process(fileName, pathToFile, nodeName);

        // Формируем метаданные для каждой задачи
        //break;  // DEVELOP
      }
          */
    } catch (CrosscuttingsException e) {
      System.out.println(e.getMessage());
    }
  }
}
