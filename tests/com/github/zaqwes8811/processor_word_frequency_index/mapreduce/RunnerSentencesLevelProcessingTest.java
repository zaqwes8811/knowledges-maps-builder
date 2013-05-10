package com.github.zaqwes8811.processor_word_frequency_index.mapreduce;

import com.github.zaqwes8811.text_processor.common.ImmutableAppUtils;
import com.github.zaqwes8811.text_processor.jobs_processors.ImmutableJobsFormer;
import com.github.zaqwes8811.text_processor.mapreduce.ImmutableMappers;
import com.github.zaqwes8811.text_processor.mapreduce.ImmutableReduces;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: кей
 * Date: 10.05.13
 * Time: 15:33
 * To change this template use File | Settings | File Templates.
 */
public class RunnerSentencesLevelProcessingTest {
  @Test
  public void testDevelop() throws Exception {
    // Получаем работы
    List<List<String>> jobs = ImmutableJobsFormer.getJobs();

    // Map Stage
    List<List> result_map_stage = new ArrayList<List>();
    for (List<String> job : jobs) {
      result_map_stage.add(ImmutableMappers.mapper_sentences_level(job));
      //break;  // DEVELOP
    }

    // Shuffle Stage - сейчас фактически нет - один узел - один файл
    List<List> result_shuffle_stage  = result_map_stage;

    // Reduce Stage  - так же нет, т.к. - один узел - один файл
    List<List> result_reduce_stage = new ArrayList<List>();
    for (List task: result_shuffle_stage) {
      List one = ImmutableReduces.reduce_sentences_level(task);
      ImmutableAppUtils.print(one);
      result_reduce_stage.add(one);
    }
  }
}
