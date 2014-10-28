package gae_store_space.values;

import java.util.Comparator;

public class WordValueFrequencyComparator implements Comparator<WordValue> {
  @Override
  public int compare(WordValue o1, WordValue o2) {
    return o1.frequency.compareTo(o2.frequency);
  }
}
