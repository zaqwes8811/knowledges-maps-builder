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

public class SpiderExtractorTest {

  @Test
  public void testDevelopSpider() {
      try {
        AppConfigurer configurer = new AppConfigurer();
        String pathToAppFolder = configurer.getPathToAppFolder();
        //
        System.out.println(pathToAppFolder);

        // Получаем работы
        ProcessorTargets processorTargets = new ProcessorTargets();
        String spiderTargetFname = "apps/targets/spider_extractor_target";
        List<List<String>> listTargets = processorTargets.runParser(spiderTargetFname);
        for (List<String> target : listTargets)
          ProcessorTargets.print(target);

        // Выделяем текст
        // Нужно передать имя исходного файла, и путь к итоговому(без расширения)


      } catch (CrosscuttingsException e) {
        System.out.println(e.getMessage());
      }
    }
}
