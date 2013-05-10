package com.github.zaqwes8811.processor_word_frequency_index.jobs_processors;

import com.github.zaqwes8811.processor_word_frequency_index.common.ImmutableAppUtils;
import com.github.zaqwes8811.processor_word_frequency_index.index_coursors.ImmutableBaseCoursor;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: кей
 * Date: 10.05.13
 * Time: 12:18
 * To change this template use File | Settings | File Templates.
 *
 * Состояние для класса - это файлы индекса
 */
final public class JobsFormer {
  public static List<List<String>> getJobs() {
    List<String> nodes = ImmutableBaseCoursor.getListNodes();
    for (String node: nodes) {
      ImmutableAppUtils.print(node);
      //ImmutableAppUtils.getListFilenamesByExtention()
    }
    return null;
  }
}
