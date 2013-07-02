import com.google.common.base.Optional;
import common.utils;
import coursors.ImmutableBaseCoursor;

import java.util.List;

public class Main {

    public static void main(String[] args) {
      // Получаем список узлов индекса
      Optional<List<String>> nodes = ImmutableBaseCoursor.getListNodes();
      if (nodes.isPresent()) {
        utils.print(nodes.isPresent());
      } else {
        utils.print("Bad is happened.");
      }
    }
}
