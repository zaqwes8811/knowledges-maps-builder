import com.github.zaqwes8811.processor_word_frequency_index.spiders_extractors.tika_wrapper.ImmutableTikaWrapper;
import org.junit.Test;

import com.github.zaqwes8811.processor_word_frequency_index.crosscuttings.AppConfigurer;
import com.github.zaqwes8811.processor_word_frequency_index.crosscuttings.CrosscuttingsException;
import com.github.zaqwes8811.processor_word_frequency_index.crosscuttings.ProcessorTargets;

import java.util.List;

public class SpiderExtractorTest {
  static void print(Object msg) {
     System.out.println(msg);

  }

  @Test
  public void testDevelopSpider() {
      try {
        // Получаем путь к папке приложения
        AppConfigurer configurer = new AppConfigurer();
        String pathToAppFolder = configurer.getPathToAppFolder();
        print(pathToAppFolder);

        // Получаем имя индекса
        ProcessorTargets processorTargets = new ProcessorTargets();
        String idxName = processorTargets.getIndexName();

        // Получаем цели
        String spiderTargetsFilename = "apps/targets/spider_extractor_target.txt";
        List<List<String>> targets = processorTargets.runParser(spiderTargetsFilename);
        for (List<String> target : targets) {
          ProcessorTargets.print(target);
          String nodeName = target.get(ProcessorTargets.RESULT_NODE_NAME);
          String pathToFile = target.get(ProcessorTargets.RESULT_PATH);
          String fileName = target.get(ProcessorTargets.RESULT_FILENAME);

          // Выделяем текст
          // Нужно передать имя исходного файла, и путь к итоговому(без расширения)
          ImmutableTikaWrapper tikaWrapper = new ImmutableTikaWrapper(pathToAppFolder, idxName);
          tikaWrapper.process(fileName, pathToFile, nodeName);

          // Формируем метаданные для каждой задачи
          break;
        }

      } catch (CrosscuttingsException e) {
        System.out.println(e.getMessage());
      }
    }
}
