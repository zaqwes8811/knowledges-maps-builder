import com.google.common.base.Optional;
import common.utils;
import coursors.ImmutableBaseCoursor;
import coursors.ImmutableIdxGetters;

import java.util.List;

public class Main {
    public static void main(String[] args) {
      // Получаем список узлов индекса
      Optional<List<String>> nodes = ImmutableBaseCoursor.getListNodes();

      // Тут уже по сути нужен ответ да или нет.
      if (nodes.isPresent()) {
        for (String node: nodes.get()) {
          utils.print(node);
          Optional<List<String>> sortedIdx = ImmutableIdxGetters.getSortedIdx(node);
          if (sortedIdx.isPresent()) {
            utils.print(sortedIdx);
          }
          break;  // DEVELOP
        }

        //
      } else {
        utils.print("Bad is happened.");
      }
    }
}
