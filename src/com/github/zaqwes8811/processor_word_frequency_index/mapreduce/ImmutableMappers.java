package com.github.zaqwes8811.processor_word_frequency_index.mapreduce;

import com.github.zaqwes8811.processor_word_frequency_index.jobs_processors.ImmutableJobsFormer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: кей
 * Date: 10.05.13
 * Time: 15:44
 * To change this template use File | Settings | File Templates.
 */
final public class ImmutableMappers {

  /*
  *
  * [node_name, [len0, len1, ...], [syllable0, syllable1, ...]]
  *
  * No use generic!!
  * */
  public static List mapper_sentences_level(List<String> job) {
    List response = new ArrayList();
     String node = job.get(ImmutableJobsFormer.IDX_NODE_NAME);

    return response;
  }

  /*
  *
  *
  * */

 }
