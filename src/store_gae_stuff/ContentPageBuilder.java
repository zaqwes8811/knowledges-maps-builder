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
  public ContentPage build(String name, ArrayList<ContentItem> contentElements) {
    // TODO: BAD! В страницу собрана обработка
    Multimap<String, ContentItem> wordHistogramSink = HashMultimap.create();
    CountReducer reducer = new CountReducer(wordHistogramSink);
    CounterMapper mapper = new CounterMapper(reducer);

    // Split
    mapper.map(contentElements);  // TODO: implicit, but be so

    // WARNING: Порядок важен! Важно сохранить елементы до того как присоединять
    //   к словам.
    //
    // Persist content items
    // TODO: как вынести в транзакцию?
    ofy().save().entities(contentElements).now();

    // Persist words
    // TODO: сделать через временный не связанны с базой тип, а потом все разом сохранить
    ArrayList<WordItem> words = new ArrayList<WordItem>();
    for (String word: wordHistogramSink.keySet()) {
      Collection<ContentItem> value = wordHistogramSink.get(word);
      words.add(WordItem.create(word, value));
    }

    // Sort words by frequency and assign idx
    Collections.sort(words, WordItem.createFrequencyComparator());
    Collections.reverse(words);

    for (int i = 0; i < words.size(); i++)
      words.get(i).setSortedIdx(i);

    {
      // TODO: вынести все операции с базой данных сюда
      ofy().save().entities(words).now();
    }

    // Слова сортированы
    return new ContentPage(name, contentElements, words);
  }
}
