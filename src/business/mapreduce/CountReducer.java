package business.mapreduce;

import com.google.common.collect.Multimap;
import store_gae_stuff.ContentItem;
import org.checkthread.annotations.NotThreadSafe;

@NotThreadSafe
public class CountReducer {
  private final Multimap<String, ContentItem> wordHistogram_;
  public CountReducer(Multimap<String, ContentItem> wordHistogram) {
    wordHistogram_ = wordHistogram;
  }

  // @param value inv. index key - index sentence - или лучше хеш.
  public void reduce(String key, ContentItem value) {
    wordHistogram_.put(key, value);
  }
}
