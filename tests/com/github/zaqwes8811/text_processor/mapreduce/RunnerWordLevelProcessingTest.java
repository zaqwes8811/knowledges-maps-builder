package com.github.zaqwes8811.text_processor.mapreduce;

import com.github.zaqwes8811.text_processor.AppConstants;
import com.github.zaqwes8811.text_processor.common.ImmutableAppUtils;
import com.github.zaqwes8811.text_processor.jobs_processors.ImmutableJobsFormer;
import com.github.zaqwes8811.text_processor.jobs_processors.ImmutableProcessorTargets;
import com.google.common.base.Joiner;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.io.Closer;
import org.junit.Test;

import java.io.IOException;
import java.util.*;

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

    // Map Stage
    List<List> result_map_stage = new ArrayList<List>();
    for (List<String> job : jobs) {
      List one = ImmutableMappers.mapper_word_level_with_compression(job);
      result_map_stage.add(one);
      break;  // DEVELOP
    }

    // Shuffle Stage - сейчас фактически нет - один узел - один файл
    List<List> result_shuffle_stage  = result_map_stage;

    // Reduce Stage  - так же нет, т.к. - один узел - один файл
    List<List> result_reduce_stage = new ArrayList<List>();
    for (List task: result_shuffle_stage) {
      List one = ImmutableReduces.reduce_word_level_base(task);
      result_reduce_stage.add(one);
    }

    // Save results
    for (List result: result_reduce_stage) {
      // Куда сохраняем результаты
      String path_for_save = Joiner.on(AppConstants.PATH_SPLITTER)
          .join(
            ImmutableProcessorTargets.getPathToIndex(),
            AppConstants.COMPRESSED_IDX_FOLDER,
            result.get(ImmutableMappers.IDX_NODE_NAME));

      ImmutableAppUtils.print(path_for_save);

      try {
        Closer closer = Closer.create();
        try {
          // Сохраняем сортированные индекс
          List<String> sorted_index = (ArrayList<String>)result.get(ImmutableReduces.IDX_SORTED_IDX);

        } catch (Throwable e) {
          closer.rethrow(e);
        } finally {
          closer.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
