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
import com.google.gson.internal.LinkedHashTreeMap;

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

                Gson gson = new Gson();


                //System.out.println(gson.fromJson(result, List.class));
                HashMap<String, ArrayList<LinkedHashTreeMap<Integer, String>>> collectionType =
                    new HashMap<String, ArrayList<LinkedHashTreeMap<Integer,String>>>();

                HashMap<String, LinkedHashTreeMap<String, Integer>> result_collection =
                        new HashMap<String, LinkedHashTreeMap<String, Integer>>();
                //new Map<String, List<Map<Integer, String>>>();
                HashMap<String, ArrayList<LinkedHashTreeMap<Integer, String>>>  type =
                        gson.fromJson(s, collectionType.getClass());

                // Можно обрабатывать

                for (Map.Entry<String, ArrayList<LinkedHashTreeMap<Integer, String>>> entry : type.entrySet()) {
                    String node_name = entry.getKey();
                    ArrayList<LinkedHashTreeMap<Integer, String>> value = entry.getValue();
                    System.out.println("src: "+value.size());
                    LinkedHashTreeMap<String, Integer> result_list = new
                            LinkedHashTreeMap<String, Integer>();
                    //System.out.println(value);
                    result_collection.put(node_name, result_list);
                    int len = value.size();
                    for(int i = 0; i < len; ++i) {
                        LinkedHashTreeMap<Integer, String> wordTuple = value.get(i);
                        // Слова
                        for (Map.Entry<Integer, String> entry_word : wordTuple.entrySet()) {
                            Object count = entry_word.getKey();
                            String wordOne = entry_word.getValue();
                            String word = wordOne;
                            russianStemmer rs_new = new russianStemmer();
                            rs_new.setCurrent(word);
                            rs_new.stem();
                            String result = rs_new.getCurrent();

                            //System.out.println(result);
                            if (result_collection.get(node_name).containsKey(result)) {
                                Integer addValue = Integer.parseInt((String)count);
                                result_collection.get(node_name).put(result, result_collection.get(node_name).get(result)+addValue);
                            } else {
                                result_collection.get(node_name).put(result, Integer.parseInt((String)count));
                            }
                        }
                    }
                    System.out.println("result: "+result_collection.get(node_name).size());
                    //break;
                    // Do things with the list
                }


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
