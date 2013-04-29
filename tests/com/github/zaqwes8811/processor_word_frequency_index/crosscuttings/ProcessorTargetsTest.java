package com.github.zaqwes8811.processor_word_frequency_index.crosscuttings;

import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: кей
 * Date: 29.04.13
 * Time: 21:51
 * To change this template use File | Settings | File Templates.
 */
public class ProcessorTargetsTest {
  @Test
  public void testRunParser() {
    ProcessorTargets testObj = new ProcessorTargets();

    String spiderTargetFname = "apps/targets/spider_extractor_target";
    try {
      testObj.runParser(spiderTargetFname);
    } catch (CrosscuttingsException e) {
      System.out.println(e.getMessage());
    }
  }
}
