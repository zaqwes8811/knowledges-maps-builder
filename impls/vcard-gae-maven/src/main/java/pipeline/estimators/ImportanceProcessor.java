package pipeline.estimators;

import gae_store_space.SentenceKind;

import java.util.Set;

public interface ImportanceProcessor {
	public Integer process(Integer freq, Set<SentenceKind> s);
}
