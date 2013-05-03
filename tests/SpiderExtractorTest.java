import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.github.zaqwes8811.processor_word_frequency_index.crosscuttings.AppConfigurer;
import com.github.zaqwes8811.processor_word_frequency_index.crosscuttings.CrosscuttingsException;
import com.github.zaqwes8811.processor_word_frequency_index.crosscuttings.ProcessorTargets;
import com.github.zaqwes8811.processor_word_frequency_index.spiders_extractors.tika_wrapper.TikaWrapper;

public class SpiderExtractorTest {

  @Test
  public void testDevelopSpider() {
      try {
        // Получаем путь к папке приложения
        AppConfigurer configurer = new AppConfigurer();
        String pathToAppFolder = configurer.getPathToAppFolder();
        ProcessorTargets.print(pathToAppFolder);

        // Получаем цели
        ProcessorTargets processorTargets = new ProcessorTargets();
        String spiderTargetsFilename = "apps/targets/spider_extractor_target.txt";
        List<List<String>> targets = processorTargets.runParser(spiderTargetsFilename);
        for (List<String> target : targets) {
          ProcessorTargets.print(target);
          String nodeName = target.get(ProcessorTargets.RESULT_NODE_NAME);
          String pathToFile = target.get(ProcessorTargets.RESULT_PATH);
          String fileName = target.get(ProcessorTargets.RESULT_FILENAME);

          // Выделяем текст
          // Нужно передать имя исходного файла, и путь к итоговому(без расширения)
          TikaWrapper tikaWrapper = new TikaWrapper();
          tikaWrapper.process(fileName, pathToFile, nodeName, pathToAppFolder);

          // Формируем метаданные для каждой задачи
          break;
        }

      } catch (CrosscuttingsException e) {
        System.out.println(e.getMessage());
      }
    }
}
