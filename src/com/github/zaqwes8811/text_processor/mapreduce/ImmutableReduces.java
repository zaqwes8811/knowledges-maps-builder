package com.github.zaqwes8811.text_processor.mapreduce;

import com.github.zaqwes8811.text_processor.common.ImmutableAppUtils;
import com.github.zaqwes8811.text_processor.math.ImmutableSummators;
import com.google.common.base.Joiner;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: кей
 * Date: 10.05.13
 * Time: 18:57
 * To change this template use File | Settings | File Templates.
 */
final public class ImmutableReduces {
  public static final int IDX_NODE_NAME = 0;
  public static final int IDX_SENT_LENGTH_MEAN = 1;
  public static final int IDX_RE = 2;
  public static final int IDX_LANG = 3;


  public static List reduce_sentences_level(List result_shuffle_stage) {
    List result_reduce_stage = new ArrayList();
    result_reduce_stage.add(result_shuffle_stage.get(ImmutableMappers.IDX_NODE_NAME));  // 0

    // Средняя длина предложения
    List<Integer> s = (List<Integer>)result_shuffle_stage.get(ImmutableMappers.IDX_SENTENCES_LENS);
    double meanLengthSentence = ImmutableSummators.meanList(s);
    double countWords = ImmutableSummators.sumIntList(s)*1.0;
    result_reduce_stage.add(meanLengthSentence);  // 1

    // Средняя длина слога
    s = (List<Integer>)result_shuffle_stage.get(ImmutableMappers.IDX_COUNT_SYLLABLES);
    double meanLengthSyllable = ImmutableSummators.sumIntList(s)/countWords;

    Double RE = new Double(0);
    String lang = (String)result_shuffle_stage.get(ImmutableMappers.IDX_LANG);
    if (lang.equals("ru")) {
      RE = (206.835 - 60.1*meanLengthSyllable - 1.3*meanLengthSentence);
      /*ImmutableAppUtils.print(
          Joiner.on(" ")
              .join(lang,
                    RE.toString(),
                    result_reduce_stage,
                    result_shuffle_stage.get(ImmutableMappers.IDX_NODE_NAME)));*/
    } else if (lang.equals("en")) {
      RE = (206.835 - 84.6*meanLengthSyllable - 1.015*meanLengthSentence);
      /*ImmutableAppUtils.print(
        Joiner.on(" ")
          .join(lang,
            RE.toString(),
            result_reduce_stage,
            result_shuffle_stage.get(ImmutableMappers.IDX_NODE_NAME))); */
    }
    result_reduce_stage.add(RE.toString());  // 2

    // Язык
    result_reduce_stage.add(lang);
    return result_reduce_stage;
  }
}
