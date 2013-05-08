package com.github.zaqwes8811.processor_word_frequency_index.spiders_processors;

import com.github.zaqwes8811.processor_word_frequency_index.crosscuttings.AppConfigurer;
import com.github.zaqwes8811.processor_word_frequency_index.crosscuttings.CrosscuttingsException;
import com.github.zaqwes8811.processor_word_frequency_index.crosscuttings.ProcessorTargets;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: кей
 * Date: 08.05.13
 * Time: 10:43
 * To change this template use File | Settings | File Templates.
 */
public class MinimalSpiderProcessorTest {
  static void print(Object msg) {
    System.out.println(msg);
  }

  @Test
  public void testDevelopSpider() {
    MinimalSpiderProcessor spiderProcessor = new MinimalSpiderProcessor();

    // Processing
    List<String> nodes = spiderProcessor.getListNodes();

    // Обрабатываем каждый узел в отдельности
    for (String node : nodes) {
      String pathToNode = spiderProcessor.getPathToIndex()+"/tmp/"+node;
      print(pathToNode);
      print(spiderProcessor.getFilenameAndLang(pathToNode));

      // Объединяем метаданные

      // Дробим и объединяем контент
      // TODO(zaqwes) TOTH: а если файлы написаны на разных языках?
      // перед предложенияеми в объед файле контента ставить язык
      break;
    }
  }
}
