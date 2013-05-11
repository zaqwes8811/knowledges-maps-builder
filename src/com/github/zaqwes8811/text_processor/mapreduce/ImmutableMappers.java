package com.github.zaqwes8811.text_processor.mapreduce;

import com.github.zaqwes8811.text_processor.common.ImmutableAppUtils;
import com.github.zaqwes8811.text_processor.jobs_processors.ImmutableJobsFormer;
import com.github.zaqwes8811.text_processor.nlp.BaseSyllableCounter;
import com.github.zaqwes8811.text_processor.nlp.BaseTokenizer;
import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.collect.*;
import com.google.common.io.Closer;
import org.apache.commons.lang3.StringUtils;
import org.tartarus.snowball.ext.russianStemmer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

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
            langMap.put(word, lang);
            sentencesPtrsMap.put(word, sentenceNumber);
          }

          // Указываем не следующее
          sentenceNumber++;
        }

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

  public static List mapper_word_level_with_compression(List<String> job) {
    List one = ImmutableMappers.mapper_word_level(job);
    Multiset<String> frequencies = (Multiset<String>)one.get(ImmutableMappers.IDX_FREQ_INDEX);

    // Результаты стадии
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
        if (RUSSIAN_STOP_WORDS.contains(compressedKey)) {
          // Нет в списке стоп-слов
          continue;
        }

        frequenciesCompressedTest.put(compressedKey, frequencies.count(key));
        frequenciesCompressed.add(compressedKey, frequencies.count(key));
        frequenciesWordRest.put(compressedKey, key);
      }
    }

    // Смотрим результат
    for (String key: frequenciesCompressedTest.keySet()) {
      ImmutableAppUtils.print(
        Joiner.on(" ").join(
          key,
          frequenciesCompressed.count(key),
          frequenciesCompressedTest.get(key),
          frequenciesWordRest.get(key)));
    }//*/
    ImmutableAppUtils.print("Size = "+frequenciesCompressed.size());

    return null;
  }

  // Base filters
  private static final Set<String> RUSSIAN_STOP_WORDS = new HashSet<String>(Arrays.asList("ль", "рад", "преимуществ", "нача", "примен", "прит", "формул", "раз", "сам", "вопрек", "вкуп", "ран", "будт", "поскольк", "вмест", "близ", "с", "присущ", "у", "позвол", "при", "случа", "стол", "эт", "прич", "дан", "достига", "опя", "немног", "моч", "снабд", "пример", "про", "дат", "подтверд", "все", "дела", "пот", "хот", "применя", "никак", "перед", "поч", "как", "провод", "состоя", "обуславлива", "некотор", "ли", "этот", "наш", "несмотр", "мал", "различн", "нескольк", "ко", "пользова", "создава", "служ", "над", "вблиз", "совс", "сво", "цел", "рассмотрет", "яв", "чег", "ввид", "никт", "отт", "чем", "нов", "тем", "выполня", "внов", "под", "описа", "ближайш", "насчет", "заявля", "пок", "сдела", "вкратц", "нужн", "поз", "дал", "посл", "даж", "существова", "даб", "разн", "знат", "кром", "этак", "нельз", "разв", "тут", "использован", "к", "определя", "им", "ин", "вдол", "начина", "и", "откуд", "обозначен", "ил", "о", "поясня", "кто", "в", "новизн", "их", "а", "назва", "ит", "ж", "е", "осуществля", "быт", "немал", "позволя", "нискольк", "был", "изображен", "очен", "небольш", "необходим", "поначал", "нема", "сущност", "нек", "фиг", "всюд", "из-под", "нем", "известн", "дава", "вот", "вроз", "сперв", "пр", "нет", "врод", "тогд", "никуд", "предложен", "себ", "называ", "немн", "неч", "подл", "заявк", "зач", "показа", "однак", "идт", "по", "зат", "рассматрива", "нег", "почт", "взам", "отнюд", "смоч", "реализова", "пут", "прежд", "прежн", "от", "позж", "предполож", "вдруг", "он", "охарактеризовыва", "част", "впред", "получ", "скольк", "постольк", "проведен", "об", "зде", "отсюд", "станов", "частн", "наибольш", "но", "изготов", "поздн", "нибуд", "вряд", "межд", "ни", "не", "пред", "достаточн", "следова", "на", "приведен", "согласн", "обуслов", "когд", "где", "работоспособн", "можн", "инач", "стольк", "ним", "хотет", "простот", "обеспечива", "нич", "созда", "относ", "всяк", "них", "впоследств", "такж", "существ", "сверх", "описан", "налич", "снабжа", "предлага", "друг", "выполнен", "установлен", "осуществ", "последова", "сил", "упрощен", "да", "поперек", "ве", "сперед", "наряд", "без", "предназнач", "во", "нигд", "наканун", "патент", "понемног", "итак", "либ", "отлич", "характеризова", "помим", "напр", "невозможн", "преимуществен", "представля", "осуществлен", "предполага", "чертеж", "изобретен", "мим", "расположен", "работа", "обеспеч", "тож", "благодар", "сообразн", "через", "получа", "сопоставительн", "указа", "че", "бы", "чтоб", "тог", "конкретн", "окол", "сраз", "посредств", "создан", "прост", "использова", "предлож", "ниоткуд", "выполн", "далек", "изготовлен", "требова", "нект", "вслед", "существен", "охарактеризова", "наподоб", "применим", "особен", "см", "со", "относительн", "снов", "горазд", "показыва", "некогд", "есл", "котор", "близк", "возл", "решен", "из", "ест", "представ", "что", "буд", "сравнен", "описыва", "счет", "аналог", "вокруг", "тот", "реализац", "соб", "том", "никогд", "результат", "наибол", "то", "полн", "тольк", "изобретательск", "устанавлива", "имет", "отч", "вед", "имен", "за", "куд", "поставлен", "подчас", "та", "обычн", "заключа", "совокупн", "скол", "чут", "ничт", "ничут", "предназнача", "лиш", "для", "так", "частност", "ем", "из-з", "там", "подтвержда", "отличн", "вероятн", "выраз", "снача", "едв", "вследств", "определен", "же", "прот", "указыва", "явля", "намн", "отлича", "ещ", "кажд", "др", "почест", "до", "содержа", "определ", "должн", "заявлен", "прототип", "поэт", "недостаток", "ег", "сред", "след", "например" ));
  public static final int MIN_COUNT_LETTERS_IN_WORD = 2;
  private static boolean isEnabled(String key) {
    // предлоги
    //
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
  /*
  * */
 }
