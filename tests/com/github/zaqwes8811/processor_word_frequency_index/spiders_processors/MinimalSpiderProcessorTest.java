package com.github.zaqwes8811.processor_word_frequency_index.spiders_processors;

import com.google.common.io.Closer;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: кей
 * Date: 08.05.13
 * Time: 10:43
 * To change this template use File | Settings | File Templates.
 */
public class MinimalSpiderProcessorTest {
  static void print(Object msg) {
    System.out.println(msg);
  }

  @Test
  public void testDevelopSpider() {
    MinimalSpiderProcessor spiderProcessor = new MinimalSpiderProcessor();

    // Processing
    List<String> nodes = spiderProcessor.getListNodes();

    // Обрабатываем каждый узел в отдельности
    StringBuilder summaryContent = new StringBuilder();
    // В нем может быть несколько файлов
    for (String node : nodes) {
      String pathToNode = spiderProcessor.getPathToIndex()+"/tmp/"+node;
      List<List<String>> summaryMeta = new ArrayList<List<String>>();
      List<List<String>> targets = spiderProcessor.getTarget(pathToNode);
      //StringBuilder
      for (List<String> target: targets) {
        try {  // внутри, чтобы не прервалась обработка из-за одного файла
          Closer readCloser = Closer.create();
          try {
            String lang = target.get(MinimalSpiderProcessor.IDX_LANG);
            String srcUrl = target.get(MinimalSpiderProcessor.IDX_SRC_URL);
            String tmpFilename = target.get(MinimalSpiderProcessor.IDX_TMP_FILE);
            summaryMeta.add(new ArrayList<String>(Arrays.asList(srcUrl, lang)));

            // Reader
            BufferedReader reader = readCloser.register(new BufferedReader(new FileReader(tmpFilename)));
            StringBuilder buffer = new StringBuilder();
            String s;
            while ((s = reader.readLine())!= null) buffer.append(s);

            // разбиваем не единицы и пишем
            // TODO(zaqwes) TOTH: maybi slow!
            String dataForSplitting = buffer.toString().replace('\n',' ');
            BreakIterator bi = BreakIterator.getSentenceInstance();
            bi.setText(dataForSplitting);
            int index = 0;
            while (bi.next() != BreakIterator.DONE) {
              String sentence = dataForSplitting.substring(index, bi.current());
              String oneRecrod = lang+' '+sentence+'\n';
              summaryContent.append(oneRecrod);
              index = bi.current();
            }

          } catch (Throwable e) { // must catch Throwable
            throw readCloser.rethrow(e);
          } finally {
            readCloser.close();
          }
        } catch (IOException e) {
          e.printStackTrace();
        }
        //break;  // DEVELOP
      }

      // Можно писать результат
      print(summaryMeta);
      print(summaryContent.toString());

      // Объединяем метаданные

      // Дробим и объединяем контент
      // TODO(zaqwes) TOTH: а если файлы написаны на разных языках?
      // перед предложенияеми в объед файле контента ставить язык
      break;
    }
  }
}
