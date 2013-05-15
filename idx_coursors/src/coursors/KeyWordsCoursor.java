package coursors;

import com.google.common.base.Joiner;
import common.utils;
import crosscuttings.AppConstants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

      // Получаем сортировынный список слов
      List<String> sortedWords = ImmutableIdxGetters.get_sorted_idx(node);
      List<String> recordPerWord =
      for (String word: sortedWords) {

        // Если слова в глобально списке ингорирования - одни цвеь

        // Если в локальном - другой

      }
      page.add(Joiner.on(WEB_NEW_LINE).join(sortedWords));

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
