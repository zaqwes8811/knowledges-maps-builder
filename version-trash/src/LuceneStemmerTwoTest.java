import org.junit.Test;
import org.tartarus.snowball.ext.russianStemmer;

import java.io.IOError;
import java.io.IOException;
import java.util.List;

public class LuceneStemmerTwoTest {
  @Test
    public void testMain() {
        try {
            //EnglishAnalyzer english = new EnglishAnalyzer();
            //english.
            /*LuceneMorphology luceneMorph = new RussianLuceneMorphology();
            String word = "раскраска";
            List<String> wordBaseForms = luceneMorph.getMorphInfo(word);
            System.out.print(wordBaseForms);
            word = "вина";
            wordBaseForms = luceneMorph.getMorphInfo(word);
            System.out.print(wordBaseForms);

            RussianLightStemmer rs = new RussianLightStemmer();
            word = "смотрел";
            int result = rs.stem(word.toCharArray(), word.length());
            System.out.println("\n"+word);
            word = "смотрела";
            result = rs.stem(word.toCharArray(), word.length());
            System.out.println(word);   */

            String word = "смотрела";
            russianStemmer rs_new = new russianStemmer();
            rs_new.setCurrent(word);
            rs_new.stem();
            System.out.println(rs_new.getCurrent());

            word = "смотрим";
            rs_new.setCurrent(word);
            rs_new.stem();
            System.out.println(rs_new.getCurrent());
        } catch (IOError e) {
            e.getStackTrace();
        }
    }
}
