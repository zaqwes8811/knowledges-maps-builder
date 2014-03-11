package business.mapreduce;

import com.google.common.collect.Multiset;
import org.checkthread.annotations.NotThreadSafe;

/**
It's fake
 */
@NotThreadSafe
public class CountReducer {
  private final Multiset<String> storage_;
  public CountReducer(Multiset<String> storage) {
    storage_ = storage;
  }
  public void reduce(String key, Integer value) {
    storage_.add(key);
  }
}
