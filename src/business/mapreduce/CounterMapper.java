package business.mapreduce;

import com.google.common.hash.HashFunction;
import org.checkthread.annotations.NotThreadSafe;

import java.util.List;

/**
 https://hadoop.apache.org/docs/r1.2.1/mapred_tutorial.html

 Fake MapReduce - try make similar
 */
@NotThreadSafe
public class CounterMapper {
  private final CountReducer reducer_;
  private final HashFunction hashFunction_;
  public CounterMapper(CountReducer reducer, HashFunction hashFunction) {
    reducer_ = reducer;
    hashFunction_ = hashFunction;
  }

  private void emit(String key, Integer value) {
    reducer_.reduce(key, value);
  }

  public void map(List<String> text) {
    for (String sentence : text) {
      Integer value = hashFunction_.newHasher().putString(sentence).hash().asInt();
      String key = sentence;
      emit(key, value);
    }
  }
}
