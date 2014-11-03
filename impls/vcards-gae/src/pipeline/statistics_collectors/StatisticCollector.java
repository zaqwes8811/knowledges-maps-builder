package pipeline.statistics_collectors;

import gae_store_space.SentenceKind;

import java.util.ArrayList;

import pipeline.mapreduce.CountReducer;
import pipeline.mapreduce.CountReducerImpl;
import pipeline.mapreduce.CounterMapper;
import pipeline.mapreduce.CounterMapperImpl;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

// Не очень хорошо - трудно будет параметризовать
// Это не обязательно будет через MapReduce
public class StatisticCollector {
	public Multimap<String, SentenceKind> buildHisto(ArrayList<SentenceKind> items) {
  	Multimap<String, SentenceKind> histo = HashMultimap.create();
    
    CountReducer reducer = new CountReducerImpl(histo);
    CounterMapper mapper = new CounterMapperImpl(reducer);

    mapper.map(items);
    
    return histo;
  }
}