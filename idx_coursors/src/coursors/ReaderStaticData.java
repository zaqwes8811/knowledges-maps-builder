package coursors;

import com.github.zaqwes8811.text_processor.common.ImmutableAppUtils;
import com.github.zaqwes8811.text_processor.index_coursors.ImmutableBaseCoursor;
import com.github.zaqwes8811.text_processor.jobs_processors.ImmutableProcessorTargets;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: кей
 * Date: 13.05.13
 * Time: 16:18
 * To change this template use File | Settings | File Templates.
 */
public class ReaderStaticData {
  static public void main(String [] args) {
    // Получаем адреса соответствующие узлам и оцененный язык
    List<String> nodes = ImmutableBaseCoursor.getListNodes();
    for (String node : nodes) {
      ImmutableAppUtils.print(node);

      // Получаем путь к индексу
      String pathToIdx = ImmutableProcessorTargets.getPathToIndex();

      // Путь к мета-файлу
    }
  }
}
