package pipeline.statistics_collectors;

import pipeline.ContentItem;

import java.util.ArrayList;

import pipeline.mapreduce.CountReducer;
import pipeline.mapreduce.CountReducerImpl;
import pipeline.mapreduce.CounterMapper;
import pipeline.mapreduce.CounterMapperImpl;
import pipeline.mapreduce.SourceMapper;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

// Не очень хорошо - трудно будет параметризовать
// Это не обязательно будет через MapReduce
public class StatisticCollector {
	public Multimap<String, ContentItem> buildNGramHisto(ArrayList<ContentItem> items) {
  	
		Multimap<String, ContentItem> histo = HashMultimap.create();

    CountReducer<ContentItem> reducer = new CountReducerImpl<ContentItem>(histo);
    
    CounterMapper mapper = new CounterMapperImpl(reducer);

    mapper.map(items);
    
    return histo;
  }
	
	public Multimap<String, String> buildStemSourceHisto(ArrayList<ContentItem> items) {
		Multimap<String, String> sources = HashMultimap.create();
		CountReducer<String> r = new CountReducerImpl<String>(sources);
		CounterMapper m = new SourceMapper(r);
		m.map(items);
		return sources;
	}
}