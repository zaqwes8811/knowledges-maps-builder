package com.github.zaqwes8811.processor_word_frequency_index.crosscuttings;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.io.Closer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: кей
 * Date: 29.04.13
 * Time: 21:10
 * To change this template use File | Settings | File Templates.
 */

// TODO(zaqwes): Как убрать боковые пробелы из строки без сплиттера и джоинера?
// TODO(zaqwes): Сделано очень плохо! Может для именвание узлов не испльзовать []
//   Guava and Python can remove spaces in begin and in end
// TODO(zaqwes): но вообще подумать над удалением заданных краевых символов строки
// TODO(zaqwes): разобраться с Guava Closer
  /*
  * Closer closer = Closer.create();
try {
   OutputStream stream = closer.register(openOutputStream());
   // что-то делаем со stream
} catch (Throwable e) { // ловим абсолютно все исключения (и даже Error'ы)
   throw closer.rethrow(e);
} finally {
   closer.close();
}
  * */
public class ProcessorTargets {
  final static int NODE_NAME = 0;
  final static int INDEX_URL = 1;

  public List<List<String>> runParser(String targetPartPath) throws CrosscuttingsException {
    // Строка задания = [Node name]*url*...
    String  targetPartPathUrlMapper = targetPartPath+".txt";
    List<List<String>> listUrls = new ArrayList<List<String>>();
    //Closer closer = Closer.create();

    try {
      BufferedReader in = new BufferedReader(new FileReader(targetPartPathUrlMapper));
      //closer.register(in);

      // Получаем строку задания
      while (true) {
        String s = in.readLine();
        if (s == null)
          break;

        // Дробим задания на блоки
        Iterable<String> result =
            Splitter.on('*')
                    .trimResults()
                    .omitEmptyStrings()
                    .split(s);

        // Строка задания по частям
        List<String> elements = Lists.newArrayList(result);
        String nodeName = elements.get(NODE_NAME);

        String purgedNodeName = extractNodeName(nodeName);
        List<String> resultItem = elements.subList(NODE_NAME, INDEX_URL+1);
        resultItem.set(NODE_NAME, purgedNodeName);

        print(resultItem);
      }
      return listUrls;
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      throw new CrosscuttingsException("File no found - "+targetPartPathUrlMapper);
    }
    catch (IOException e) {
      e.printStackTrace();
      throw new CrosscuttingsException("Error on read file - "+targetPartPathUrlMapper);
    }
    // TODO(zaqwes) : Как быть с закрытием ресурса?
    //finally {
    //  in.close();
    //}
  }

  public static void print(Object msg) {
      System.out.println(msg);
  }

  // TODO(zaqwes): impl. very bad!!
  private String extractNodeName(String line) {
    // Очищаем имя узла
    Joiner joiner = Joiner.on("").skipNulls();

    Iterable<String> purgeNode =
      Splitter.on('[')
      .trimResults()
      .omitEmptyStrings()
      .split(line);

    String tmp = joiner.join(purgeNode);
    purgeNode =
      Splitter.on(']')
        .trimResults()
        .omitEmptyStrings()
        .split(tmp);

    //print('*'+joiner.join(purgeNode)+'*');
    return joiner.join(purgeNode);
  }
}
