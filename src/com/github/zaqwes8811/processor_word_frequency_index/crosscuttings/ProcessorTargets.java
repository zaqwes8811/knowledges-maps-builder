package com.github.zaqwes8811.processor_word_frequency_index.crosscuttings;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: кей
 * Date: 29.04.13
 * Time: 21:10
 * To change this template use File | Settings | File Templates.
 */
public class ProcessorTargets {
  public void runParser(String targetPartPath) {
    try {
      BufferedReader java_in = new BufferedReader(new FileReader(targetPartPath+".txt"));
      //writer = ProfilingWriter();
      while (true) {
        String s = java_in.readLine();
        print(s);
        if (s == null)
          break;
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
