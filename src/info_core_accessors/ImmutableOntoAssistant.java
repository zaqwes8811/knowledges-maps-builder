package info_core_accessors;

@Deprecated
public class ImmutableOntoAssistant {
  /*

  static public void main(String [] args) {
    StringBuilder result = new StringBuilder();
      // Получаем сортированных индекс
    List<String> nodes = ImmutableBaseCoursor.getListNodes();
    for (String node: nodes) {
      utils.print(node);
      // сортированных индекс
      List<String> sorted_idx = ImmutableIdxGetters.get_sorted_idx(node);
      Map<String, String> rest_idx = ImmutableIdxGetters.get_rest_idx(node);
      Map<String, Integer> freq_idx = ImmutableIdxGetters.get_freq_idx(node);

      int i = 0;
      for (String stem: sorted_idx) {
        if (i%7  != 0) {
        utils.print(
          Joiner.on(" * ")
            .join(freq_idx.get(stem), stem, rest_idx.get(stem)));
        } else {
          utils.print(
            Joiner.on(" * ")
              .join("\n"+freq_idx.get(stem), stem, rest_idx.get(stem)));
        }
          i++;
      }
    }


      // Частота, стем, остатки

  }
  */
}
