package pipeline.mapreduce;

import net.jcip.annotations.NotThreadSafe;

import com.google.common.collect.Multimap;

@NotThreadSafe
public class CountReducerImpl<T> implements CountReducer<T> {
  private final Multimap<String, T> wordHistogram_;
  public CountReducerImpl(Multimap<String, T> wordHistogram) {
    wordHistogram_ = wordHistogram;
  }

  // @param value inv. index key - index sentence - или лучше хеш.
  @Override
  public void reduce(String key, T value) {
    wordHistogram_.put(key, value);
  }
}
