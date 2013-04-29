package com.github.zaqwes8811.processor_word_frequency_index.crosscuttings;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: кей
 * Date: 29.04.13
 * Time: 21:10
 * To change this template use File | Settings | File Templates.
 */
public class ProcessorTargets {
  final static int NODE_NAME = 1;
  final static int INDEX_URL = 1;

  public void runParser(String targetPartPath) {
    try {
      BufferedReader java_in = new BufferedReader(new FileReader(targetPartPath+".txt"));
      //writer = ProfilingWriter();
      while (true) {
        String s = java_in.readLine();
        if (s == null)
          break;

        Iterable<String> result = Splitter.on('*')
                                          .trimResults()
                                          .omitEmptyStrings()
                                          .split(s);
        List<String> elements = Lists.newArrayList(result);
        String url = elements.get(INDEX_URL);
        print(url);

      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();

    }
    catch (IOException e) {
      e.printStackTrace();

    }
  }

  private void print(Object msg) {
      System.out.println(msg);
  }


}
