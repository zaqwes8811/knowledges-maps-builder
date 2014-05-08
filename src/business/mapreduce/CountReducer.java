package business.mapreduce;

import com.google.common.collect.Multiset;
import org.checkthread.annotations.NotThreadSafe;

/**
It's fake
 */
@NotThreadSafe
public class CountReducer {
  private final Multiset<String> wordHistogram_;
  public CountReducer(Multiset<String> wordHistogram) {
    wordHistogram_ = wordHistogram;
  }

  // @param value inv. index key - index sentence - или лучше хеш.
  public void reduce(String key, Integer contentItem) {
    wordHistogram_.add(key);
  }
}
