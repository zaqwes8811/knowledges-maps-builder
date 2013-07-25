package web_wrapper;


import java.util.*;

class Getters {
  private Getters() {}

  public static Getter createFake() {
    return new FakeGetter();
  }

  private static class FakeGetter implements Getter {
    public List<Map<String, List<String>>> getPackage() {
      final Integer TOTAL_CARDS = 12;
      List<Map<String, List<String>>> cardsInfo = new ArrayList<Map<String, List<String>>>();

      // Заполняем одну карточку
      for (int i = 0; i < TOTAL_CARDS; ++i) {
        List<String> word = new ArrayList<String>(Arrays.asList("jetty" + i));
        List<String> content = new ArrayList<String>(Arrays.asList("jetty here"+i, "a jetty"+i));
        List<String> translate = new ArrayList<String>(Arrays.asList("Сервер"+i, "Много используется"+i));

        Map<String, List<String>> oneCardContent = new HashMap<String, List<String>>();
        oneCardContent.put("word", word);
        oneCardContent.put("content", content);
        oneCardContent.put("translate", translate);

        cardsInfo.add(oneCardContent);
      }
      return cardsInfo;
    }
  }
}
