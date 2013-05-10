package com.github.zaqwes8811.processor_word_frequency_index;

import com.github.zaqwes8811.processor_word_frequency_index.common.ImmutableAppUtils;
import com.github.zaqwes8811.processor_word_frequency_index.jobs_processors.JobsFormer;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: кей
 * Date: 10.05.13
 * Time: 12:25
 * To change this template use File | Settings | File Templates.
 */
public class JobsFormerTest {

  @Test
  public void testDevelop() {
    ImmutableAppUtils.print(JobsFormer.getJobs());

  }
}
