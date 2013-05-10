package com.github.zaqwes8811.text_processor.mapreduce;

import com.github.zaqwes8811.text_processor.common.ImmutableAppUtils;
import com.github.zaqwes8811.text_processor.jobs_processors.ImmutableJobsFormer;
import com.github.zaqwes8811.text_processor.nlp.BaseSyllableCounter;
import com.github.zaqwes8811.text_processor.nlp.BaseTokenizer;
import com.google.common.io.Closer;

import java.io.BufferedReader;
import java.io.FileReader;
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
    String filename = job.get(ImmutableJobsFormer.IDX_FILENAME);
    List<Integer> sentencesLengths = new ArrayList<Integer>();
    List<Integer> syllablesLengths = new ArrayList<Integer>();

    try {
      Closer closer = Closer.create();
      try {
        BufferedReader reader = closer.register(new BufferedReader(new FileReader(filename)));
        String s;
        while ((s = reader.readLine()) != null) {
          //ImmutableAppUtils.print(s);
          int langPtr = s.indexOf(' ');
          List<String> words = BaseTokenizer.extractWords(s.substring(langPtr, s.length()));
          sentencesLengths.add(words.size());

          // Получаем язык, нужно для деления на слоги
          String lang = s.substring(0, langPtr);
          for (String word : words) {
            int count = BaseSyllableCounter.calc(word, lang);
            ImmutableAppUtils.print(word+" "+count);
          }
          //
        }

      } catch (Throwable e) {
        closer.rethrow(e);
      } finally {
        closer.close();
      }
    } catch (IOException e) {
       e.printStackTrace();
    }

    //
    response.addAll(Arrays.asList(node, sentencesLengths, syllablesLengths));
    return response;
  }

  /*
  *
  * Mapper for word level processing
  * */

 }
