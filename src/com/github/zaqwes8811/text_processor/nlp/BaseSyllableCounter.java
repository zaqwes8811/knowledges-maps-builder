package com.github.zaqwes8811.text_processor.nlp;


//import com.sun.xml.internal.ws.util.StringUtils;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Array;

/**
 * Created with IntelliJ IDEA.
 * User: кей
 * Date: 10.05.13
 * Time: 17:55
 * To change this template use File | Settings | File Templates.
 */
final public class BaseSyllableCounter {
  private static final String russianVowel[] = {"а", "е", "ё", "и", "о", "у", "ы", "э", "ю", "я"};
  public static int calc (String word, String lang) {
    String workCopy = word.toLowerCase();
    // а е ё и о у ы э ю я
    if (lang.equals("ru")) {
      int count = 0;
      for (int i = 0; i < russianVowel.length; ++i) {
        count += StringUtils.countMatches(workCopy, russianVowel[i]);
      }
      return count;
    } else {
      return 0;
    }
  }
}
