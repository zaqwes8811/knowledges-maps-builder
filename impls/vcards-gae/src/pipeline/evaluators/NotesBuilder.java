package pipeline.evaluators;


import com.google.common.base.Joiner;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotesBuilder {
  static public final String NOTE_N80_CAPACITY = "f80_p";  // Core
  static public final String NOTE_N20_CAPACITY = "f20";
  static public final String NOTE_N20_COUNT = "w20_p";  // Core
  static public final String NOTE_N80_COUNT = "w80";

  public Map<String, String> getDistributionNotes(String node) {
  	// TODO(): bad!
  	HashMap<String, HashMap<String, String>> metadataStaticNotes = ImmutableIdxGetters.getStaticNotes();  

    // Получаем статические данные по сложности
    // Статические оценки
    Map<String, String> nodeStaticNotesInfo = metadataStaticNotes.get(node);

    // Данные для каждого из индексов по 80/20
    List<String> sorted_full_idx = ImmutableIdxGetters.get_sorted_idx(node);

    // сам частотынй индекс индекс
    HashMap<String, Integer> sorted_freq_idx = ImmutableIdxGetters.get_freq_idx(node);

    Integer totalAmount = 0;
    for (String word: sorted_full_idx) 
    	totalAmount += sorted_freq_idx.get(word);
    
    Double threshold = totalAmount*0.8;

    // Оценка - 20% слов
    Integer count_unique_words = sorted_full_idx.size();
    Double N20 = count_unique_words*0.2;
    Double N80 = count_unique_words - N20;

    // Оценка - 80% интегральной частоты
    Double sum = new Double(0);
    Integer N80_Amount = 0;
    for (String word: sorted_full_idx) {
      if (sum > threshold) {
        break;
      }
      N80_Amount++;
      sum += sorted_freq_idx.get(word);
    }
    Double N20_Amount = new Double(count_unique_words)-N80_Amount;

    // Итого
    nodeStaticNotesInfo.put(NOTE_N80_CAPACITY, N80_Amount.toString());
    nodeStaticNotesInfo.put(NOTE_N20_CAPACITY, N20_Amount.toString());
    nodeStaticNotesInfo.put(NOTE_N20_COUNT, N20.toString());
    nodeStaticNotesInfo.put(NOTE_N80_COUNT, N80.toString());
    return nodeStaticNotesInfo;
  }
}
