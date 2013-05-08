package com.github.zaqwes8811.processor_word_frequency_index.spiders_processors;

import com.github.zaqwes8811.processor_word_frequency_index.crosscuttings.AppConfigurer;
import com.github.zaqwes8811.processor_word_frequency_index.crosscuttings.CrosscuttingsException;
import com.github.zaqwes8811.processor_word_frequency_index.crosscuttings.ProcessorTargets;
import com.google.common.io.Closer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: кей
 * Date: 08.05.13
 * Time: 10:41
 * To change this template use File | Settings | File Templates.
 */
public class MinimalSpiderProcessor {
  static void print(Object msg) {
    System.out.println(msg);
  }
  public String getPathToIndex() {
    String pathToIndex = "";
    try {
      // Получаем путь к папке приложения
      AppConfigurer configurer = new AppConfigurer();
      String pathToAppFolder = configurer.getPathToAppFolder();

      // Получаем имя индекса
      ProcessorTargets processorTargets = new ProcessorTargets();
      String idxName = processorTargets.getIndexName();
      pathToIndex = pathToAppFolder+'/'+idxName;
    } catch (CrosscuttingsException e) {
      System.out.println(e.getMessage());
    }
    return pathToIndex;
  }
  public List<String> getListNodes() {
    List<String> listNodes = new ArrayList<String>();
    // Получаем список узлов по папкам, а на по заданиям
    String pathToTmpFolder = getPathToIndex()+"/tmp";
    File rootTmp = new File(pathToTmpFolder);

    // Итоговый список
    listNodes.addAll(Arrays.asList(rootTmp.list()));
    return listNodes;
  }

  public List<String> getListNamesMetaFiles(String pathToNode) {
    File nodeContainer = new File(pathToNode);
    String regex = ".+\\.meta";
    List<String> result =
        Arrays.asList(nodeContainer.list(new DirFilter(regex)));
    return result;
  }

  /*
  *
  * @return: [[lang0, filename0], []]
  * */
  public List<List<String>> getFilenameAndLang(String pathToNode) {
    List<List<String>> targetsInfo = new ArrayList<List<String>>();
    List<String> listNamesMetaFiles = getListNamesMetaFiles(pathToNode);
    for (String filename: listNamesMetaFiles) {
      List<String> oneTarget = new ArrayList<String>();
      // Получаем язык файла, оцененный или заранее известрый, это отражено в мета-файле
      String metafilename = pathToNode+'/'+filename;
      oneTarget.add(getLang(metafilename));

      // Получаем имя файла с контентом
      //metafilename.g
      String fileWithContent = metafilename.substring(0, metafilename.lastIndexOf('.'))+".ptxt";
      oneTarget.add(fileWithContent);
      targetsInfo.add(oneTarget);
    }
    return targetsInfo;
  }

  private String getLang(String metafilename) {
    String lang = "unknown";
    try {
      Closer closer = Closer.create();
      try{
        // Reader
        BufferedReader reader = closer.register(new BufferedReader(new FileReader(metafilename)));
        StringBuilder buffer = new StringBuilder();
        String s;
        while ((s = reader.readLine())!= null) buffer.append(s);

        // Получаем настройки
        String jsonMeta = buffer.toString();
        Gson gson = new Gson();
        Type type = new TypeToken<HashMap<String, String>>() {}.getType();
        HashMap<String, String> meta = gson.fromJson(jsonMeta, type);
        lang = meta.get("lang");

      } catch (Throwable e) { // must catch Throwable
        throw closer.rethrow(e);
      } finally {
        closer.close();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return lang;
  }
}

class DirFilter implements FilenameFilter {
  private Pattern pattern;
  public DirFilter(String regex) {
    pattern = Pattern.compile(regex);
  }
  public boolean accept(File dir, String name) {
    return  pattern.matcher(name).matches();
  }
}
