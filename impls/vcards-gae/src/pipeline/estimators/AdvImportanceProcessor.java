package pipeline.estimators;

import gae_store_space.SentenceKind;

import java.util.Set;

public class AdvImportanceProcessor implements ImportanceProcessor {
	@Override
	public Integer process(Integer freq, Set<SentenceKind> s) {
		// if (f < 5)
		// FIXME: не ясно как отмасштабировать - если не одно предложение, может самое длинное?
		// FIXME: пока int но вообще лучше что-то поточнее
		return freq;
	}
}
