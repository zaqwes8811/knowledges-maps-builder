package idx_coursors;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import java.util.*;

public class BaseContentHolderTest {
  @Test
  public void testGetContentItem() throws Exception {
    ImmutableMap<String, List<Integer>> keys = ImmutableMap.of(
      "hello", Arrays.asList(1, 2), "hay", Arrays.asList(1));

    // Порядок предложений важен! Поэтому список с сохранением порядка
    // Коллекции Guava хранят порядок, но для сипска рекомендуюется использовать
    //   JDK версию данной коллекции.
    ImmutableList<String> sentences = ImmutableList.of("hello hay", "hay");

    ContentHolder contentHolder = new BaseContentHolder(keys, sentences);
  }
}
