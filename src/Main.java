import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;

import com.github.zaqwes8811.processor_word_frequency_index.crosscuttings.AppConfigurer;
import com.github.zaqwes8811.processor_word_frequency_index.crosscuttings.CrosscuttingsException;

public class Main {

    public static void main(String[] args) {
      AppConfigurer configurer = new AppConfigurer();
      try {
        String pathToAppFolder = configurer.getPathToAppFolder();
        //
        System.out.println(pathToAppFolder);

      } catch (CrosscuttingsException e) {
        System.out.println(e.getMessage());
      }



    }
}
