package pipeline.mapreduce;

import gae_store_space.SentenceKind;

import java.util.List;

public interface CounterMapper {

	public void map(List<SentenceKind> contentItemKinds);
}
