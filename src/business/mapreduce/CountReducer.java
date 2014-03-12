package business.mapreduce;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import org.checkthread.annotations.NotThreadSafe;

/**
It's fake
 */
@NotThreadSafe
public class CountReducer {
  private final Multiset<String> index_;
  private final Multimap<String, Integer> inverseIndex_;
  public CountReducer(Multiset<String> storage, Multimap<String, Integer> inverseIdx) {
    index_ = storage;
    inverseIndex_ = inverseIdx;
  }

  // @param value inv. index key - index sentence - или лучше хеш.
  public void reduce(String key, Integer value) {
    index_.add(key);
    inverseIndex_.put(key, value);
  }
}
