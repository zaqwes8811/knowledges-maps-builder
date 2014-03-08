package third_party_test;

import com.google.common.base.Splitter;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import org.junit.Test;

public class SetGuavaTest {

  @Test
    public void testSplit() {
	// write your code here

        Iterable<String> splitted = Splitter.on(',')
                .trimResults()
                .omitEmptyStrings()
                .split("foo,bar,,   qux");

        //for(String s: splitted) {
        //    System.out.println(s);
        //}

        splitted = Splitter.on(',').trimResults().split("a,  l b,   c asfddf, d, d, d");
        //for(String s: splitted) {
        //    System.out.println(s);
        //}

        Multiset<String> wordsMultiset = HashMultiset.create(splitted);
        // wordsMultiset.create(splitted);
        for(String s: wordsMultiset.elementSet()) {
            System.out.println((s));
            System.out.println(wordsMultiset.count(s));
        }
        System.out.print(wordsMultiset);
    }

  @Test
  void testTrimSpacesString() {


  }
}
