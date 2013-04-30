package com.github.zaqwes8811.processor_word_frequency_index.crosscuttings;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;

//import com.github.zaqwes8811.processor_word_frequency_index.crosscuttings.CrosscuttingsException;

/**
 * Created with IntelliJ IDEA.
 * User: кей
 * Date: 29.04.13
 * Time: 20:51
 * To change this template use File | Settings | File Templates.
 */
public class AppConfigurer {

  public String getPathToAppFolder() throws CrosscuttingsException {
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
      //e.printStackTrace();
      throw new CrosscuttingsException("File with cfg, no found. File name - "+path_to_cfg);
    } finally {
      //if (input != null)
      //  input.close();
    }
    //return null;
  }
}
