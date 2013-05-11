package com.github.zaqwes8811.text_processor.mapreduce;

import com.github.zaqwes8811.text_processor.common.ImmutableAppUtils;
import com.github.zaqwes8811.text_processor.jobs_processors.ImmutableJobsFormer;
import com.github.zaqwes8811.text_processor.nlp.BaseSyllableCounter;
import com.github.zaqwes8811.text_processor.nlp.BaseTokenizer;
import com.google.common.base.CharMatcher;
import com.google.common.collect.*;
import com.google.common.io.Closer;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.WrongCharaterException;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import org.tartarus.snowball.ext.russianStemmer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: кей
 * Date: 10.05.13
 * Time: 15:44
 * To change this template use File | Settings | File Templates.
 */
final public class ImmutableMappers {
  public final static int IDX_NODE_NAME = 0;
  public final static int IDX_SENTENCES_LENS = 1;
  public final static int IDX_COUNT_SYLLABLES = 2;
  public final static int IDX_LANG = 3;
  /*
  *
  * [node_name, [len0, len1, ...], [syllable0, syllable1, ...]]
  *
  * No use generic!!
  *
  * Ограничения:
  *   - Расчет число слогов призводится упрощенно - по числу гласных букв и усредненному языку
  *      а значит, встрачается не соотв. языку слов, то число слогов в нем будет 0.
  *      Возможно оценка из-за этого будет искажаться. Определять язык по одному слову? Будут промахи!
  * */
  public static List mapper_sentences_level(List<String> job) {
    List response = new ArrayList();
    String node = job.get(ImmutableJobsFormer.IDX_NODE_NAME);
    String filename = job.get(ImmutableJobsFormer.IDX_FILENAME);
    List<Integer> sentencesLengths = new ArrayList<Integer>();
    List<Integer> syllablesLengths = new ArrayList<Integer>();
    String meanLang = "unknown";
    try {
      Closer closer = Closer.create();
      try {
        BufferedReader reader = closer.register(new BufferedReader(new FileReader(filename)));
        String s;
        while ((s = reader.readLine()) != null) {
          int langPtr = s.indexOf(' ');
          List<String> words = BaseTokenizer.extractWords(s.substring(langPtr, s.length()));
          sentencesLengths.add(words.size());

          // Получаем язык, нужно для деления на слоги
          meanLang = s.substring(0, langPtr);  // язык средний по доукменту
          int countSyllable = 0;
          for (String word : words) {
            countSyllable += BaseSyllableCounter.calc(word, meanLang);
          }
          syllablesLengths.add(countSyllable);
        }
      } catch (Throwable e) {
        closer.rethrow(e);
      } finally {
        closer.close();
      }
    } catch (IOException e) {
       e.printStackTrace();
    }

    //
    response.addAll(Arrays.asList(node, sentencesLengths, syllablesLengths, meanLang));
    return response;
  }

  /*
  *
  * Mapper for word level processing
  * */
  public final static int IDX_FREQ_INDEX = 1;
  public final static int IDX_LANG_MAP = 2;
  public final static int IDX_SENT_MAP = 3;
  public static List mapper_word_level(List<String> job) {
    List response = new ArrayList();
    String node = job.get(ImmutableJobsFormer.IDX_NODE_NAME);
    String filename = job.get(ImmutableJobsFormer.IDX_FILENAME);

    // Processing
    try {
      Closer closer = Closer.create();
      try {
        int sentenceNumber = 1;
        Multiset<String> wordsFrequenceMultyset = HashMultiset.create();
        HashMultimap<String, String> langMap = HashMultimap.create();
        HashMultimap<String, Integer> sentencesPtrsMap = HashMultimap.create();

        // Reading
        BufferedReader reader = closer.register(new BufferedReader(new FileReader(filename)));
        String s;
        while ((s = reader.readLine()) != null) {
          String workCopy = s.toLowerCase();
          int langPtr = workCopy.indexOf(' ');
          List<String> words = BaseTokenizer.extractWords(workCopy.substring(langPtr, workCopy.length()));
          String lang = workCopy.substring(0, langPtr);

          // Добавляем в частотный индекс
          wordsFrequenceMultyset.addAll(words);

          // Получаем язык, нужно для деления на слоги
          for (String word : words) {
            //ImmutableAppUtils.print(word);
            langMap.put(word, lang);
            sentencesPtrsMap.put(word, sentenceNumber);
          }

          // Указываем не следующее
          sentenceNumber++;
        }

        //for (String key: sentencesPtrsMap.keySet()) {
         // ImmutableAppUtils.print(sentencesPtrsMap.get(key));
        //}

        // Make result
        response.add(node);
        response.add(wordsFrequenceMultyset);
        response.add(langMap);
        response.add(sentencesPtrsMap);
      } catch (Throwable e) {
        closer.rethrow(e);
      } finally {
        closer.close();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    //
    return response;
  }

  public static List mapper_word_level_with_compression(Multiset<String> frequencies) {
    Multiset<String> frequenciesCompressed = HashMultiset.create();
    Multimap<String, String> frequenciesWordRest = HashMultimap.create();
    Multimap<String, Integer> frequenciesCompressedTest = ArrayListMultimap.create();

    // Компрессоры
    russianStemmer ruStemmer = new russianStemmer();

    // Перебираем слова
    for (String key: frequencies.elementSet()) {
      if (isEnabled(key)) {
        // Сжимаем ключ
        ruStemmer.setCurrent(key);
        ruStemmer.stem();
        String compressedKey = ruStemmer.getCurrent();

        frequenciesCompressedTest.put(compressedKey, frequencies.count(key));
        frequenciesWordRest.put(compressedKey, key);
      }
    }

    // Смотрим результат
    for (String key: frequenciesCompressedTest.keySet()) {
      ImmutableAppUtils.print(frequenciesCompressedTest.get(key)+" "+frequenciesWordRest.get(key));
    }

    return null;
  }

  // Base filters
  public static final int MIN_COUNT_LETTERS_IN_WORD = 2;
  private static boolean isEnabled(String key) {
    String noDigits = CharMatcher.JAVA_DIGIT.replaceFrom(key, "*"); // star out all digits
    int countDigits = StringUtils.countMatches(noDigits, "*");
    if (countDigits == 0) {
      // в ключе чисел нет
      if (key.length() > MIN_COUNT_LETTERS_IN_WORD) {
        // однобуквенные удаляем
        if (StringUtils.countMatches(key, ".") == 0) {
          // Нет точек в слове
          return true;
        }
      }
    }
    return false;
  }
 }
