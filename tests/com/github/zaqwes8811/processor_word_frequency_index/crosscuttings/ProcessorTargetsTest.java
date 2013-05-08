package com.github.zaqwes8811.processor_word_frequency_index.crosscuttings;

import org.junit.Test;

import java.util.List;

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
    print("\nNEW TEST");
    ProcessorTargets testObj = new ProcessorTargets();

    String spiderTargetFname = "apps/targets/spider_extractor_target";
    try {
      List<List<String>> listTargets = testObj.runParser(spiderTargetFname);
      for (List<String> target : listTargets)
        ProcessorTargets.print(target);
    } catch (CrosscuttingsException e) {
      System.out.println(e.getMessage());
    }
  }

  @Test
  public void testPathSplitter() {
    print("\nNEW TEST");
    ProcessorTargets testObj = new ProcessorTargets();

    String spiderTargetFname = "apps/targets/spider_extractor_target.json";
    try {
      List<String> splittedPath =  testObj.splitUrlToFilenameAndPath(spiderTargetFname);
      print(splittedPath);
    } catch (CrosscuttingsException e) {
      System.out.println(e.getMessage());
    }
  }

  static void print(Object msg) {
    System.out.println(msg);
  }
}
