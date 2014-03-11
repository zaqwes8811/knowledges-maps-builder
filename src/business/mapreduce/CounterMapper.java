package business.mapreduce;

import com.google.common.collect.Multiset;
import org.checkthread.annotations.NotThreadSafe;

/**
 https://hadoop.apache.org/docs/r1.2.1/mapred_tutorial.html

 Fake MapReduce - try make similar
 */
@NotThreadSafe
public class CounterMapper {
  private final CountReducer reducer_;
  public CounterMapper(CountReducer reducer) {
    reducer_ = reducer;
  }

  public void map(String text, Multiset<String> output) {

  }
}
