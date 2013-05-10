package com.github.zaqwes8811.processor_word_frequency_index.nlp;

import java.text.BreakIterator;

/**
 * Created with IntelliJ IDEA.
 * User: кей
 * Date: 10.05.13
 * Time: 15:04
 * To change this template use File | Settings | File Templates.
 */
final public class BaseTokenizer {

  // Встроенный в SDK дробитель на предложения - похоже безсловарный, поэтому грубоватый
  public static StringBuilder splitToSentences(StringBuilder buffer, String lang) {
    // разбиваем не единицы и пишем
    // TODO(zaqwes) TOTH: maybi slow!
    // Убираем переносы строк - могут быть ошибки! Некоторые слова с дефисом могут быть перенес. по дуфису
    String dataForSplitting = buffer.toString().replace("-\n", "\n").replace('\n', ' ');

    BreakIterator bi = BreakIterator.getSentenceInstance();
    bi.setText(dataForSplitting);
    int index = 0;
    StringBuilder summaryContent = new StringBuilder();
    while (bi.next() != BreakIterator.DONE) {
      String sentence = dataForSplitting.substring(index, bi.current());
      String oneRecord = lang+' '+sentence+'\n';
      summaryContent.append(oneRecord);
      index = bi.current();
    }
    return summaryContent;
  }
}
