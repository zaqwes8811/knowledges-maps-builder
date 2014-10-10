package business.mapreduce;

import com.google.common.collect.Multimap;
import store_gae_stuff.ContentItemKind;
import org.checkthread.annotations.NotThreadSafe;

@NotThreadSafe
public class CountReducer {
  private final Multimap<String, ContentItemKind> wordHistogram_;
  public CountReducer(Multimap<String, ContentItemKind> wordHistogram) {
    wordHistogram_ = wordHistogram;
  }

  // @param value inv. index key - index sentence - или лучше хеш.
  public void reduce(String key, ContentItemKind value) {
    wordHistogram_.put(key, value);
  }
}
