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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOError;
import java.io.IOException;

public class TestLuceneStemmer {
    public String stem(String fname) {
        try {
            BufferedReader in = new BufferedReader(new FileReader(fname));
            String s;
            while ((s = in.readLine())!= null) {
                String word = s;
                russianStemmer rs_new = new russianStemmer();
                rs_new.setCurrent(word);
                rs_new.stem();
                System.out.println(rs_new.getCurrent());
            }
        } catch (IOError e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Test return";

    }
    public static void main(String[] args) {
        try {
            BufferedReader in = new BufferedReader(new FileReader("tmp2.txt"));
            String s;
            while ((s = in.readLine())!= null) {
                //System.out.println(s);
                String word = s;
                russianStemmer rs_new = new russianStemmer();
                rs_new.setCurrent(word);
                rs_new.stem();
                System.out.println(rs_new.getCurrent());
            }


            /*word = "смотрим";
            rs_new.setCurrent(word);
            rs_new.stem();
            System.out.println(rs_new.getCurrent());  */
        } catch (IOError e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
