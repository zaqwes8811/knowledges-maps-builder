package dal.gae_kinds;

// http://stackoverflow.com/questions/2784514/sort-arraylist-of-custom-objects-by-property
//
// Eff. java.:

import java.util.Comparator;

/**
 * Created by zaqwes on 5/12/14.
 */
public class WordComparator implements Comparator<Word> {
  @Override
  public int compare(Word o1, Word o2) {
    return -1;//o1.getStartDate().compareTo(o2.getStartDate());
  }
}
