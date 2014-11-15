package pipeline.estimators;

import gae_store_space.SentenceKind;

import java.util.Set;

public class SimpleImportanceProcessor implements ImportanceProcessor {
	@Override
	public Integer process(Integer freq, Set<SentenceKind> s) {
		return freq;
	}
};