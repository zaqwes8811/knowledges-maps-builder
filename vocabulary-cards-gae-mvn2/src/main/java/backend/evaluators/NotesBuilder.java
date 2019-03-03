package backend.evaluators;


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
  	HashMap<String, HashMap<String, String>> metadataStaticNotes = null;//ImmutableIdxGetters.getStaticNotes();  

    // Получаем статические данные по сложности
    // Статические оценки
    Map<String, String> nodeStaticNotesInfo = metadataStaticNotes.get(node);

    // Данные для каждого из индексов по 80/20
    List<String> sortedFullIdx = null;//ImmutableIdxGetters.get_sorted_idx(node);

    // сам частотынй индекс индекс
    HashMap<String, Integer> sortedFreqIdx = null;// ImmutableIdxGetters.get_freq_idx(node);

    Integer totalAmount = 0;
    for (String word: sortedFullIdx) 
    	totalAmount += sortedFreqIdx.get(word);
    
    Double threshold = totalAmount*0.8;

    // Оценка - 20% слов
    Integer count_unique_words = sortedFullIdx.size();
    Double N20 = count_unique_words*0.2;
    Double N80 = count_unique_words - N20;

    // Оценка - 80% интегральной частоты
    Double sum = new Double(0);
    Integer N80_Amount = 0;
    for (String word: sortedFullIdx) {
      if (sum > threshold) {
        break;
      }
      N80_Amount++;
      sum += sortedFreqIdx.get(word);
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
