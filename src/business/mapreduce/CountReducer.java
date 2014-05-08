package business.mapreduce;

import com.google.common.collect.Multiset;
import org.checkthread.annotations.NotThreadSafe;

/**
It's fake
 */
@NotThreadSafe
public class CountReducer {
  private final Multiset<String> index_;
  public CountReducer(Multiset<String> storage) {
    index_ = storage;
  }

  // @param value inv. index key - index sentence - или лучше хеш.
  public void reduce(String key, Integer value) {
    index_.add(key, value);
  }
}
