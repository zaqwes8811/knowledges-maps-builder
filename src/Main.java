import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
	// write your code here

      String path_to_cfg = "";
      Yaml yaml = new Yaml();
      try {
        InputStream input = new FileInputStream(new File("apps/cfgs/app_cfg.yaml"));
        Map<String, Object> object = (Map<String, Object>) yaml.load(input);
        System.out.println(object);
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      }
    }
}
