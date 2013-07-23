package crosscuttings.hided;

import com.google.common.base.Joiner;
import crosscuttings.CrosscuttingsException;
import idx_coursors.hided.ImmutableBaseCoursor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: кей
 * Date: 10.05.13
 * Time: 12:18
 * To change this template use File | Settings | File Templates.
 *
 * Состояние для класса - это файлы индекса
 *
 * Возможно похожий класс может дробить файлы узла для балансировки нагрузки
 */
final public class ImmutableJobsFormer {
  public static final int IDX_NODE_NAME = 0;
  public static final int IDX_FILENAME = 1;

  public static List<List<String>> getJobs() throws CrosscuttingsException {

    List<List<String>> jobs = new ArrayList<List<String>>();
    //
    List<String> nodes = ImmutableBaseCoursor.getListNodes().get();
    for (String node: nodes) {
      String contentFilename = Joiner.on(AppConstants.PATH_SPLITTER)
        .join(
          ProcessorTargets.getPathToIndex(),
          AppConstants.CONTENT_FOLDER,
          node,
          AppConstants.CONTENT_FILENAME);
      jobs.add(Arrays.asList(node, contentFilename));
    }
    return jobs;
  }
}
