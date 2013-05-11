package com.github.zaqwes8811.text_processor.mapreduce;

import com.github.zaqwes8811.text_processor.common.ImmutableAppUtils;
import com.github.zaqwes8811.text_processor.math.ImmutableSummators;
import com.google.common.base.Joiner;
import com.google.common.collect.Multiset;

import java.util.*;

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

  public static List reduce_word_level_base(List task) {
    List one = new ArrayList();
    // Сортируем индекс частот
    Multiset<String> unsorted_index = (Multiset<String>)task.get(ImmutableMappers.IDX_FREQ_INDEX);


    Set<String> elems = unsorted_index.elementSet();
    Map<String, Integer> unsorted_map = new HashMap<String, Integer>();
    for (String elem: elems) {
      unsorted_map.put(elem, unsorted_index.count(elem));
    }
    List<Map.Entry<String, Integer>> list = new LinkedList(unsorted_map.entrySet());

    // sort list based on comparator
    Collections.sort(list, new Comparator() {
      public int compare(Object o1, Object o2) {
        return ((Comparable) ((Map.Entry) (o2)).getValue())
          .compareTo(((Map.Entry) (o1)).getValue());
      }
    });

    // Сортированный список
    List<String> sorted_result_list = new ArrayList<String>();
    for (Map.Entry<String, Integer> entry : list) {
      sorted_result_list.add(entry.getKey());
    }

    // Добаляем к результатам
    one.addAll(task);
    one.add(sorted_result_list);
    return one;
  }
}
