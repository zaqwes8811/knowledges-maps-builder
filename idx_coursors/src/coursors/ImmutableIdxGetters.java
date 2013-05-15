package coursors;

import com.google.common.base.Joiner;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import common.ImmutableAppUtils;
import common.utils;
import crosscuttings.AppConstants;
import jobs_processors.ImmutableProcessorTargets;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: кей
 * Date: 15.05.13
 * Time: 19:31
 * To change this template use File | Settings | File Templates.
 */
final public class ImmutableIdxGetters {
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
}
