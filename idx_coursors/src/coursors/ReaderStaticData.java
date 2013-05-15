package coursors;


import common.ImmutableAppUtils;
import common.utils;
import crosscuttings.AppConstants;
import jobs_processors.ImmutableProcessorTargets;
import com.google.common.base.Joiner;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import mapreduce.ImmutableReduceSentencesLevel;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: кей
 * Date: 13.05.13
 * Time: 16:18
 * To change this template use File | Settings | File Templates.
 */
public class ReaderStaticData {
  static public final String NOTE_N80_CAPACITY = "f80_p";  // Core
  static public final String NOTE_N20_CAPACITY = "f20";
  static public final String NOTE_N20_COUNT = "w20_p";  // Core
  static public final String NOTE_N80_COUNT = "w80";


  static public List<String> get_sorted_idx(String node) {
    String sorted_idx_json = utils.file2string(
      Joiner.on(AppConstants.PATH_SPLITTER)
        .join(
          ImmutableProcessorTargets.getPathToIndex(),
          AppConstants.COMPRESSED_IDX_FOLDER,
          node,
          AppConstants.SORTED_IDX_FILENAME));
    return (new Gson().fromJson(sorted_idx_json,
      new TypeToken<ArrayList<String>>() {}.getType()));
  }

  static public HashMap<String, HashMap<String, String>>  get_static_notes() {
    String metadata_static_notes_json = utils.file2string(
      Joiner.on(AppConstants.PATH_SPLITTER)
        .join(
          ImmutableProcessorTargets.getPathToIndex(),
          AppConstants.STATIC_NOTES_FILENAME));
    return (new Gson().fromJson(metadata_static_notes_json,
        new TypeToken<HashMap<String, HashMap<String, String>>>() {}.getType()));
  }

  static public HashMap<String, Integer> get_freq_idx(String node) {
    String sorted_freq_idx_json = utils.file2string(
      Joiner.on(AppConstants.PATH_SPLITTER)
        .join(
          ImmutableProcessorTargets.getPathToIndex(),
          AppConstants.COMPRESSED_IDX_FOLDER,
          node,
          AppConstants.FREQ_IDX_FILENAME));
    return (new Gson().fromJson(sorted_freq_idx_json,
      new TypeToken<HashMap<String, Integer>>() {}.getType()));
  }

  public void get_urls_and_langs_node(String node){
    // Путь к мета-файлу
    String pathToMetaFile = Joiner.on(AppConstants.PATH_SPLITTER)
      .join(
        ImmutableProcessorTargets.getPathToIndex(),
        AppConstants.CONTENT_FOLDER,
        node,
        AppConstants.CONTENT_META_FILENAME);

    // Преобразуем в json
    String settingsInJson = utils.file2string(pathToMetaFile);
    Type type = new TypeToken<List<List<String>>>() {}.getType();
    List<List<String>> metadata = new Gson().fromJson(settingsInJson, type);

    // Можно вытряхивать
    for (List<String> item: metadata) {
      ImmutableAppUtils.print(node);
      ImmutableAppUtils.print("\t"+item.get(0)+", "+item.get(1));
    }
  }

  static public String json_get_notes_for_node(String node) {
    return new Gson().toJson(get_notes_for_node(node));
  }

  static public Map<String, String> get_notes_for_node(String node) {
    HashMap<String, HashMap<String, String>> metadata_static_notes = get_static_notes();  // TODO(): bad!

    // Получаем статические данные по сложности
    // Статические оценки
    Map<String, String> node_static_notes_info = metadata_static_notes.get(node);
    //ImmutableAppUtils.print(node_static_notes_info.get(ImmutableReduceSentencesLevel.NOTE_RE));

    // Данные для каждого из индексов по 80/20
    List<String> sorted_fall_idx = get_sorted_idx(node);

    // сам частотынй индекс индекс
    HashMap<String, Integer> sorted_freq_idx = get_freq_idx(node);

    Integer total_amount = 0;
    for (String word: sorted_fall_idx) total_amount += sorted_freq_idx.get(word);
    Double threshold = total_amount*0.8;

    // Оценка - 20% слов
    Integer count_unique_words = sorted_fall_idx.size();
    Double N20 = count_unique_words*0.2;
    Double N80 = count_unique_words - N20;

    // Оценка - 80% интегральной частоты
    Double sum = new Double(0);
    Integer N80_Amount = 0;
    for (String word: sorted_fall_idx) {
      if (sum > threshold) {
        break;
      }
      N80_Amount++;
      sum += sorted_freq_idx.get(word);
    }
    Double N20_Amount = new Double(count_unique_words)-N80_Amount;

    // Итого
    node_static_notes_info.put(NOTE_N80_CAPACITY, N80_Amount.toString());
    node_static_notes_info.put(NOTE_N20_CAPACITY, N20_Amount.toString());
    node_static_notes_info.put(NOTE_N20_COUNT, N20.toString());
    node_static_notes_info.put(NOTE_N80_COUNT, N80.toString());
    return node_static_notes_info;
  }

  //static private Map<>
  static public void main(String [] args) {

    // Получаем адреса соответствующие узлам и оцененный язык
    List<String> nodes = ImmutableBaseCoursor.getListNodes();
    List<String> rpt = new ArrayList<String>(
      Arrays.asList(Joiner.on(";")
        .join(
          "Имя документа",
          "Флеш",
          "Время прочтения",
          "Ср. дл. предл.",
          "20% частых",
          "80% редких",
          "частые сост. 80% слов. состава",
          "редкие - 20% состава")));
    for (String node : nodes) {
      Map<String, String> node_static_notes_info = get_notes_for_node(node);
      rpt.add(get_one_record(node, node_static_notes_info));
      //break;  // DEVELOP
    }

    // пишем результат
    try {
      utils.list2file(rpt, Joiner.on(AppConstants.PATH_SPLITTER)
          .join(
            "rpts",
            "real_notes.csv"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  static public String get_one_record(String node, Map<String, String> info) {
    utils.print(node+" "+info);
    String record = Joiner.on(";")
        .join(
          node,
          info.get(ImmutableReduceSentencesLevel.NOTE_RE).replace('.', ','),
          info.get(ImmutableReduceSentencesLevel.NOTE_MEAN_TIME_FOR_READ).replace('.', ','),
          info.get(ImmutableReduceSentencesLevel.NOTE_MEAN_LEN_SENT).replace('.', ','),
          info.get(ReaderStaticData.NOTE_N20_COUNT).replace('.', ','),
          info.get(ReaderStaticData.NOTE_N80_COUNT).replace('.', ','),
          info.get(ReaderStaticData.NOTE_N80_CAPACITY).replace('.', ','),
          info.get(ReaderStaticData.NOTE_N20_CAPACITY).replace('.', ','));
    return record;
  }
}
