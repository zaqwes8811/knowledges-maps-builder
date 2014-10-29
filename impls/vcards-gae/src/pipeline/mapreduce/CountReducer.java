package pipeline.mapreduce;

import gae_store_space.SentenceKind;

public interface CountReducer {
	public void reduce(String key, SentenceKind value);
}
