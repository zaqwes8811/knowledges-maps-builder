package com.github.zaqwes8811.text_processor.mapreduce;

import com.github.zaqwes8811.text_processor.common.ImmutableAppUtils;
import com.github.zaqwes8811.text_processor.jobs_processors.ImmutableJobsFormer;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: кей
 * Date: 10.05.13
 * Time: 21:46
 * To change this template use File | Settings | File Templates.
 */
public class RunnerWordLevelProcessingTest {
  @Test
  public void testDevelop () {
    // Получаем работы
    List<List<String>> jobs = ImmutableJobsFormer.getJobs();

    //String text = "now we can use wordsMultiset.count(String) to find the count of a word";
    //Multiset<String> wordsMultiset = HashMultiset.create();
    //wordsMultiset.addAll(words);

    // Map Stage
    List<List> result_map_stage = new ArrayList<List>();
    for (List<String> job : jobs) {
      List one = ImmutableMappers.mapper_word_level(job);
      result_map_stage.add(one);

      ImmutableAppUtils.print(job);
      ImmutableAppUtils.print(one);
      break;  // DEVELOP
    }

    /*
    // Shuffle Stage - сейчас фактически нет - один узел - один файл
    List<List> result_shuffle_stage  = result_map_stage;

    // Reduce Stage  - так же нет, т.к. - один узел - один файл
    List<List> result_reduce_stage = new ArrayList<List>();
    for (List task: result_shuffle_stage) {
      List one = ImmutableReduces.reduce_sentences_level(task);
      ImmutableAppUtils.print(one);
      result_reduce_stage.add(one);
    } */

    // Filtration Stage

  }
}
