package business.nlp;

import com.google.common.collect.ImmutableList;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;

public class SentencesSplitter {

  // About:
  //   split with some probability
  //
  // Troubles:
  //   разбитие прямой речи из субтитров. Недоразбивает
  //   Yeah, I just wasn't looking where I was going. - But I'm great, actually. - Oh, thank goodness.
  public ImmutableList<String> getSentences(String text) {
    BreakIterator bi = BreakIterator.getSentenceInstance();
    bi.setText(text);
    List<String> result = new ArrayList<String>();
    int index = 0;
    while (bi.next() != BreakIterator.DONE) {
      String sentence = text.substring(index, bi.current());
      result.add(sentence);
      index = bi.current();
    }
    return ImmutableList.copyOf(result);
  }
}
