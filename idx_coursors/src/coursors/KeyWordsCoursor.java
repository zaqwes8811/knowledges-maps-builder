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
  public static void main(String [] args) {
    // Need read nodes
    List<String> nodes = ImmutableBaseCoursor.getListNodes();

    // Real processing
    for (String node: nodes) {
      List<String> page = new ArrayList<String>();
      page.add("<!DOCTYPE html>\n" +
        "<html>\n" +
        "<body>");
      List<String> rpt = new ArrayList<String>();
      // Получаем оценки

      // сохраняем отчет
      page.add("</body>\n" +
        "</html>");
      String rptFileName = Joiner.on(AppConstants.PATH_SPLITTER)
        .join(
          "rpts",
          "key_words",
          node+".html");
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
