/**
 * Created with IntelliJ IDEA.
 * User: кей
 * Date: 09.05.13
 * Time: 13:06
 * To change this template use File | Settings | File Templates.
 */

package coursors;



import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import crosscuttings.AppConstants;
import crosscuttings.CrosscuttingsException;
import crosscuttings.jobs_processors.ImmutableProcessorTargets;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// Список получаем по размеченным папкам в временной директории индекса
final public class ImmutableBaseCoursor {
  public static Optional<List<String>> getListNodes() {
    try {
      String pathToTmpFolder =
        Joiner.on(AppConstants.PATH_SPLITTER).join(
          ImmutableProcessorTargets.getPathToIndex().get(),
          AppConstants.TMP_FOLDER);

      // Получаем список узлов по папкам, а на по заданиям
      File rootTmp = new File(pathToTmpFolder);
      List<String> listNodes = new ArrayList<String>(Arrays.asList(rootTmp.list()));
      return Optional.of(listNodes);
    } catch (CrosscuttingsException e)  {
       return Optional.absent();
    }
  }
}
