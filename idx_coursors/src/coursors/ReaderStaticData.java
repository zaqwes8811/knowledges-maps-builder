package coursors;

import com.github.zaqwes8811.text_processor.AppConstants;
import com.github.zaqwes8811.text_processor.common.ImmutableAppUtils;
import com.github.zaqwes8811.text_processor.index_coursors.ImmutableBaseCoursor;
import com.github.zaqwes8811.text_processor.jobs_processors.ImmutableProcessorTargets;
import com.google.common.base.Joiner;
import com.google.common.io.Closer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: кей
 * Date: 13.05.13
 * Time: 16:18
 * To change this template use File | Settings | File Templates.
 */
public class ReaderStaticData {
  static public String file2string(String filename) {
    try {
      Closer closer = Closer.create();
      try {
        BufferedReader in = closer.register(new BufferedReader(new FileReader(filename)));
        String s;
        StringBuffer buffer = new StringBuffer();
        while ((s = in.readLine()) != null) buffer.append(s);
        return buffer.toString();
      } catch (Throwable e) {
        closer.rethrow(e);
      } finally {
        closer.close();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  static public List<String> get_sorted_idx(String node) {
    String sorted_idx_json = file2string(
      Joiner.on(AppConstants.PATH_SPLITTER)
        .join(
          ImmutableProcessorTargets.getPathToIndex(),
          AppConstants.COMPRESSED_IDX_FOLDER,
          node,
          AppConstants.SORTED_IDX_FILENAME));
    return (new Gson().fromJson(sorted_idx_json,
      new TypeToken<ArrayList<String>>() {}.getType()));
  }

  static public HashMap<String, Integer> get_freq_idx(String node) {
    String sorted_freq_idx_json = file2string(
      Joiner.on(AppConstants.PATH_SPLITTER)
        .join(
          ImmutableProcessorTargets.getPathToIndex(),
          AppConstants.COMPRESSED_IDX_FOLDER,
          node,
          AppConstants.FREQ_IDX_FILENAME));
    return (new Gson().fromJson(sorted_freq_idx_json,
      new TypeToken<HashMap<String, Integer>>() {}.getType()));
  }

  //static private Map<>
  static public void main(String [] args) {
    // Получаем адреса соответствующие узлам и оцененный язык
    List<String> nodes = ImmutableBaseCoursor.getListNodes();
    for (String node : nodes) {
      // Получаем путь к индексу
      String pathToIdx = ImmutableProcessorTargets.getPathToIndex();

      // Путь к мета-файлу
      String pathToMetaFile = Joiner.on(AppConstants.PATH_SPLITTER)
        .join(pathToIdx,
          AppConstants.CONTENT_FOLDER,
          node,
          AppConstants.CONTENT_META_FILENAME);

      // Преобразуем в json
      String settingsInJson = file2string(pathToMetaFile);
      Gson gson = new Gson();
      Type type = new TypeToken<List<List<String>>>() {}.getType();
      List<List<String>> metadata = gson.fromJson(settingsInJson, type);

      // Можно вытряхивать
      for (List<String> item: metadata) {
        ImmutableAppUtils.print(node);
        ImmutableAppUtils.print("\t"+item.get(0)+", "+item.get(1));
      }

      // Получаем статические данные по сложности
      // Заголовки столбцов таблицы
      //ImmutableAppUtils.print("Сложность");
      ImmutableAppUtils.print("Имя, Флеш, Средняя длина преложения, Время на прочтение, 80, 20");

      //
      String pathToStaticContentMeta = Joiner.on(AppConstants.PATH_SPLITTER)
        .join(
          pathToIdx,
          AppConstants.STATIC_NOTES_FILENAME);

      String staticNotesJson = file2string(pathToStaticContentMeta);

      Type typeStaticNotes = new TypeToken<HashMap<String, HashMap<String, String>>>() {}.getType();
      HashMap<String, HashMap<String, String>> metadataStaticNotes =
          gson.fromJson(staticNotesJson, typeStaticNotes);

      // Данные для каждого из индексов по 80/20
      List<String> sorted_fall_idx = get_sorted_idx(node);

      // сам частотынй индекс индекс
      HashMap<String, Integer> sorted_freq_idx = get_freq_idx(node);

      for (String word: sorted_fall_idx) {
        ImmutableAppUtils.print(word+" "+sorted_freq_idx.get(word));
      }

      // Оценка - 20% слов
      Integer count_unique_words = sorted_fall_idx.size();
      Integer

      // Оценка - 80% интегральной частоты

      break;
    }
  }
}
