package com.github.zaqwes8811.processor_word_frequency_index.index_coursors;

import com.github.zaqwes8811.processor_word_frequency_index.AppConstants;
import com.github.zaqwes8811.processor_word_frequency_index.crosscuttings.ImmutableProcessorTargets;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: кей
 * Date: 09.05.13
 * Time: 13:06
 * To change this template use File | Settings | File Templates.
 */

// Список получаем по размеченным папкам в временной директории индекса
final public class ImmutableBaseCoursor {
  public static List<String> getListNodes() {
    List<String> listNodes = new ArrayList<String>();
    // Получаем список узлов по папкам, а на по заданиям
    String pathToTmpFolder = ImmutableProcessorTargets.getPathToIndex()+"/"+ AppConstants.TMP_FOLDER;
    File rootTmp = new File(pathToTmpFolder);

    // Итоговый список
    listNodes.addAll(Arrays.asList(rootTmp.list()));
    return listNodes;
  }
}
