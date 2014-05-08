package business.mapreduce;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import common.Utils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: кей
 * Date: 11.03.14
 * Time: 19:45
 * To change this template use File | Settings | File Templates.
 */
public class CounterMapReduceTest {
  @Test
  public void testRun() throws Exception {
    // build
    Multiset<String> summary = HashMultiset.create();
    Multimap<String, Integer> inverseIndex = HashMultimap.create();
    HashFunction hf = Hashing.md5();
    CountReducer reducer = new CountReducer(summary, inverseIndex);
    CounterMapper mapper = new CounterMapper(reducer, hf);

    // work
    List<String> sentences = new ArrayList<String>();
    sentences.add("hello");
    sentences.add("hello");
    sentences.add("world");

    mapper.map(sentences);
    inverseIndex.put("hello", 9);
    inverseIndex.put("hello", 9);
    Utils.print(inverseIndex);

    Set<String> keys = summary.elementSet();
    assert inverseIndex.keySet().size() == 2;
    assert summary.count("hello") == 2;
    assert keys.size() == 2;
  }
}
