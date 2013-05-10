package com.github.zaqwes8811.processor_word_frequency_index.spiders_processors;

import com.github.zaqwes8811.text_processor.index_coursors.ImmutableBaseCoursor;
import com.github.zaqwes8811.text_processor.spiders_processors.MinimalSpiderProcessor;
import org.junit.Test;

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
    List<String> nodes = ImmutableBaseCoursor.getListNodes();

    // Обрабатываем каждый узел в отдельности
    for (String node : nodes) {
      spiderProcessor.processOneNode(node);
    }
  }
}
