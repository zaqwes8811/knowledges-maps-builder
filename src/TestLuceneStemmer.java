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

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

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
            BufferedReader in = new BufferedReader(
                    new FileReader("apps/indexes/first_index.json"));
            String s;
            PrintWriter out = new PrintWriter("apps/out.txt");
            while ((s = in.readLine())!= null) {
                String word = s;
                System.out.println(s);
                russianStemmer rs_new = new russianStemmer();
                rs_new.setCurrent(word);
                rs_new.stem();
                String result = rs_new.getCurrent();

                Gson gson = new Gson();


                //System.out.println(gson.fromJson(result, List.class));
                HashMap<String, ArrayList<HashMap<Integer, String>>> collectionType =
                    new HashMap<String, ArrayList<HashMap<Integer,String>>>();
                //new Map<String, List<Map<Integer, String>>>();
                Map<String, List<Map<Integer, String>>> type =
                        gson.fromJson(result, collectionType.getClass());

                // Можно обрабатывать
                System.out.println(type);

                // Пишем результат
                //out.println(word);
            }
            //out.close();


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
