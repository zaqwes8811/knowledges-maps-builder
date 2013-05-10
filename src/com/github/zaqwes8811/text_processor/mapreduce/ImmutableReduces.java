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

  public static List reduce_sentences_level(List result_shuffle_stage) {
    // Средняя длина предложения
    List<Integer> s = (List<Integer>)result_shuffle_stage.get(ImmutableMappers.IDX_SENTENCES_LENS);
    double meanLengthSentence = ImmutableSummators.meanList(s);
    double countWords = ImmutableSummators.sumIntList(s)*1.0;

    List result_reduce_stage = new ArrayList();
    result_reduce_stage.add(meanLengthSentence);

    // Средняя длина слога
    s = (List<Integer>)result_shuffle_stage.get(ImmutableMappers.IDX_COUNT_SYLLABLES);
    double meanLengthSyllable = ImmutableSummators.sumIntList(s)/countWords;
    result_reduce_stage.add(meanLengthSyllable);

    String lang = (String)result_shuffle_stage.get(ImmutableMappers.IDX_LANG);
    if (lang.equals("ru")) {
      Double RE = (206.835 - 60.1*meanLengthSyllable - 1.3*meanLengthSentence);
      ImmutableAppUtils.print(
          Joiner.on(" ")
              .join(lang,
                    RE.toString(),
                    result_reduce_stage,
                    result_shuffle_stage.get(ImmutableMappers.IDX_NODE_NAME)));
    } else if (lang.equals("en")) {
      //Double RE = (206.835 - 84.6*meanLengthSyllable - 1.015*meanLengthSentence);
      //ImmutableAppUtils.print(lang+" "+RE.toString());
      //ImmutableAppUtils.print(result_reduce_stage);
    }
    return result_reduce_stage;
  }
}
