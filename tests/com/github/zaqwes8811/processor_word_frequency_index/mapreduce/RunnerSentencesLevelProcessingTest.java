package com.github.zaqwes8811.processor_word_frequency_index.mapreduce;

import com.github.zaqwes8811.processor_word_frequency_index.jobs_processors.ImmutableJobsFormer;
import org.junit.Test;

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
    for (List<String> job : jobs) {

    }

    // Shuffle Stage - сейчас фактически нет - один узел - один файл

    // Reduce Stage  - так же нет, т.к. - один узел - один файл
  }
}
