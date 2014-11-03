package pipeline.estimators;

import gae_store_space.SentenceKind;

import java.util.Set;

public class AdvImportanceProcessor implements ImportanceProcessor {
	@Override
	public Integer process(Integer freq, Set<SentenceKind> s) {
		// if (f < 5)
		return freq;
	}
}
