import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
      Main configurer = new Main();
      System.out.println(configurer.getPathToAppFolder());
    }

    public String getPathToAppFolder() {
      String path_to_cfg = "apps/cfgs/app_cfg.yaml";
      Yaml yaml = new Yaml();
      //InputStream input;
      try {
        InputStream input = new FileInputStream(new File(path_to_cfg));
        Map<String, Object> object = (Map<String, Object>) yaml.load(input);
        Map topCfg = (Map)object;
        Map scriberCfg = (Map)((Map)topCfg.get("App")).get("Scriber");


        return (String)scriberCfg.get("app_folder");
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      } finally {
        //if (input != null)
        //  input.close();
      }
      return null;
    }
}
