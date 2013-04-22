/**
 * Created with IntelliJ IDEA.
 * User: Igor
 * Date: 22.04.13
 * Time: 10:47
 * To change this template use File | Settings | File Templates.
 */

/*import org.apache.lucene.morphology.EnglishAnalyzer;
import org.apache.lucene.morphology.english.EnglishLuceneMorphology;
//import org.apache.lucene.morphology.english.
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import org.apache.lucene.morphology.russian.RussianMorphology;
import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.analysis.ru.RussianAnalyzer;
import org.apache.lucene.analysis.ru.RussianLightStemmer;     */


import org.tartarus.snowball.ext.russianStemmer;

import java.io.IOError;

public class TestLuceneStemmer {
    public static void main(String[] args) {
        try {
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
