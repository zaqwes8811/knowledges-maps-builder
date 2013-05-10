package com.github.zaqwes8811.processor_word_frequency_index.mapreduce;

import com.github.zaqwes8811.processor_word_frequency_index.jobs_processors.ImmutableJobsFormer;
import com.google.common.io.Closer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: кей
 * Date: 10.05.13
 * Time: 15:44
 * To change this template use File | Settings | File Templates.
 */
final public class ImmutableMappers {
  public final static int IDX_SENTENCES_LENS = 1;
  /*
  *
  * [node_name, [len0, len1, ...], [syllable0, syllable1, ...]]
  *
  * No use generic!!
  * */
  public static List mapper_sentences_level(List<String> job) {
    List response = new ArrayList();
    String node = job.get(ImmutableJobsFormer.IDX_NODE_NAME);
    List<Integer> sentencesLengths = new ArrayList<Integer>();
    List<Integer> syllablesLengths = new ArrayList<Integer>();

    try {
      Closer closer = Closer.create();
      try {

      } finally {
        closer.close();
      }
    } catch (IOException e) {

    }

    //
    response.addAll(Arrays.asList(node, sentencesLengths, syllablesLengths));
    return response;
  }

  /*
  *
  *
  * */

 }
