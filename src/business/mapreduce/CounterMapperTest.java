package business.mapreduce;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: кей
 * Date: 11.03.14
 * Time: 19:45
 * To change this template use File | Settings | File Templates.
 */
public class CounterMapperTest {
  @Test
  public void testRun() throws Exception {
    Multiset<String> summary = HashMultiset.create();
    CountReducer reducer = new CountReducer(summary);
  }
}
