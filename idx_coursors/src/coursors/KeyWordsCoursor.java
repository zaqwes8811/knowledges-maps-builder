package coursors;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import common.utils;
import crosscuttings.AppConstants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: кей
 * Date: 15.05.13
 * Time: 19:27
 * To change this template use File | Settings | File Templates.
 */
public class KeyWordsCoursor {
  private static final String WEB_NEW_LINE = "<br>";
  public static void main(String [] args) {
    // Need read nodes
    List<String> nodes = ImmutableBaseCoursor.getListNodes();

    // Real processing
    for (String node: nodes) {
      List<String> page = new ArrayList<String>();
      page.add("<!DOCTYPE html><html><head>" +
        "<meta http-equiv=\"Content-Type\" content=\"text/html;charset=utf-8\">" +
        "</head><body>");
      List<String> rpt = new ArrayList<String>();

      // Получаем исходные адреса
      rpt.addAll(ImmutableIdxGetters.get_urls_and_langs_node(node));
      page.add(Joiner.on(WEB_NEW_LINE).join(rpt));
      page.add(WEB_NEW_LINE+WEB_NEW_LINE);

      // Получаем сортировынный список слов
      List<String> sortedWords = ImmutableIdxGetters.get_sorted_idx(node);
      Map<String, Integer> frequencyIdx = ImmutableIdxGetters.get_freq_idx(node);
      List<String> recordPerWord = new ArrayList<String>();
      HashMap<String, String> restIdx = ImmutableIdxGetters.get_rest_idx(node);
      HashMap<String, List<Integer>> sentences_idx = ImmutableIdxGetters.get_sentences_idx(node);

      List<String> list_sentences = ImmutableIdxGetters.get_list_sentences(node);
      for (String word: sortedWords) {
        List<Integer> idxs_sentences = sentences_idx.get(word);
        Iterable<String> rest_words =
            Splitter.on(" ")
                .trimResults()
                .omitEmptyStrings()
                .split(restIdx.get(word));
        String sentence = list_sentences.get(idxs_sentences.get(0)-1);
        sentence = sentence.substring(sentence.indexOf(" ")+1, sentence.length());
        for (String rest_word: rest_words) {
          sentence = sentence.replace(rest_word,
              "<span style=\"background-color:Crimson;color:azure;\">"+rest_word+"</span>");
          utils.print(rest_word);
          //break;  // DEVELOP
        }


        // Добавляем в запись
        recordPerWord.add(Joiner.on("<br>&nbsp&nbsp&nbsp")
          .join(
            "Key word: <span style=\"background-color:Crimson;color:azure;\">"+word+"</span>",
            "Actuality: "+"<span style=\"background-color:red\">"+"new"+"</span>",
            "Rest words: "+"<span style=\"background-color:red\">"+rest_words.toString()+"</span>",
            "Frequency: "+"<span style=\"background-color:red\">"+frequencyIdx.get(word).toString()+"</span>",
            "Count sentences: "+"<span style=\"background-color:red\">"+sentences_idx.get(word).size()+"</span>",
            "<span style=\"background-color:Darkorange;color:azure;\">"+"Content item:"+"</span> "+sentence));
        recordPerWord.add("<hr>");
        // Если слова в глобально списке ингорирования - одни цвеь

        // Если в локальном - другой
        //break;  // DEVELOP
      }
      page.add("<hr>");
      page.add(Joiner.on(WEB_NEW_LINE).join(recordPerWord));


      // Получаем оценки

      // сохраняем отчет

      page.add("</body></html>");
      String rptFileName = Joiner.on(AppConstants.PATH_SPLITTER)
        .join("rpts", "key_words",node+".html");
      try {
        utils.list2file(page, rptFileName);
      } catch (IOException e) {
        e.printStackTrace();
      }
      break;  // DEVELOP
    }

    // rpt

  }
}
