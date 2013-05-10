package com.github.zaqwes8811.processor_word_frequency_index.mapreduce;

import com.github.zaqwes8811.text_processor.common.ImmutableAppUtils;
import com.github.zaqwes8811.text_processor.jobs_processors.ImmutableJobsFormer;
import com.github.zaqwes8811.text_processor.mapreduce.ImmutableMappers;
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
      List resultMapping = ImmutableMappers.mapper_sentences_level(job);
      List<Integer> s = (List<Integer>)resultMapping.get(ImmutableMappers.IDX_SENTENCES_LENS);
      ImmutableAppUtils.print(s);
      break;  // DEVELOP
    }

    // Shuffle Stage - сейчас фактически нет - один узел - один файл

    // Reduce Stage  - так же нет, т.к. - один узел - один файл
  }
}
