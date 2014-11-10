package pipeline.statistics_collectors;

import gae_store_space.SentenceKind;

import java.util.ArrayList;

import org.javatuples.Pair;

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
	public Multimap<String, SentenceKind> buildNGramHisto(ArrayList<SentenceKind> items) {
  	
		Multimap<String, SentenceKind> histo = HashMultimap.create();

    CountReducer<SentenceKind> reducer = new CountReducerImpl<SentenceKind>(histo);
    
    CounterMapper mapper = new CounterMapperImpl(reducer);

    mapper.map(items);
    
    return histo;
  }
	
	public Multimap<String, String> buildStemSourceHisto(ArrayList<SentenceKind> items) {
		Multimap<String, String> sources = HashMultimap.create();
		CountReducer<String> r = new CountReducerImpl<String>(sources);
		CounterMapper m = new SourceMapper(r);
		m.map(items);
		return sources;
	}
}