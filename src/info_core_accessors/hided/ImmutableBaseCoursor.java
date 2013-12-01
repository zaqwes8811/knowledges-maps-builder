/**
 * Created with IntelliJ IDEA.
 * User: кей
 * Date: 09.05.13
 * Time: 13:06
 * To change this template use File | Settings | File Templates.
 */

package info_core_accessors.hided;



import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import through_functional.hided.AppConstants;
import through_functional.CrosscuttingsException;
import through_functional.hided.ProcessorTargets;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// Список получаем по размеченным папкам в временной директории индекса
//@Stateless
final public class ImmutableBaseCoursor {
  public static Optional<List<String>> getListNodes() {
    try {
      String pathToTmpFolder =
        Joiner.on(AppConstants.PATH_SPLITTER)
            .join(
              ProcessorTargets.getPathToIndex(),
                AppConstants.TMP_FOLDER);

      // Получаем список узлов по папкам, а на по заданиям
      File root = new File(pathToTmpFolder);
      List<String> listNodes = new ArrayList<String>(Arrays.asList(root.list()));
      return Optional.of(listNodes);
    } catch (CrosscuttingsException e)  {
       // Обработка ошибки сжимается до проверки результата на ноль.
       // Потом в логах можно посмотреть что конкретно случилось.
       return Optional.absent();
    }
  }
}
