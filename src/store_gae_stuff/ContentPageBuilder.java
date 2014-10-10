package store_gae_stuff;

import business.mapreduce.CountReducer;
import business.mapreduce.CounterMapper;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import static store_gae_stuff.OfyService.ofy;

public class ContentPageBuilder {
  public ContentPageKind build(String name, ArrayList<ContentItemKind> contentElements) {
    // TODO: BAD! В страницу собрана обработка
    Multimap<String, ContentItemKind> wordHistogramSink = HashMultimap.create();
    CountReducer reducer = new CountReducer(wordHistogramSink);
    CounterMapper mapper = new CounterMapper(reducer);

    // Split
    mapper.map(contentElements);  // TODO: implicit, but be so

    ArrayList<WordItemKind.WordValue> value = new ArrayList<WordItemKind.WordValue>();
    for (String word: wordHistogramSink.keySet()) {
      Collection<ContentItemKind> content = wordHistogramSink.get(word);
      int rawFrequency = content.size();
      value.add(new WordItemKind.WordValue(word, rawFrequency, content));
    }

    // Sort words by frequency and assign idx
    Collections.sort(value, new WordItemKind.WordValueFrequencyComparator());
    Collections.reverse(value);

    ArrayList<WordItemKind> words = new ArrayList<WordItemKind>();
    {
      // WARNING: Порядок важен! Важно сохранить елементы до того как присоединять
      //   к словам.
      //
      // Persist content contentItems
      // TODO: как вынести в транзакцию?
      ofy().save().entities(contentElements).now();

      for (int i = 0; i < value.size(); i++) {
        WordItemKind.WordValue v = value.get(i);
        words.add(WordItemKind.create(v.word, v.items, v.frequency));
        words.get(i).setPointPos(i);
      }

      // TODO: вынести все операции с базой данных сюда
      ofy().save().entities(words).now();
    }

    // Слова сортированы
    return new ContentPageKind(name, contentElements, words);
  }
}
