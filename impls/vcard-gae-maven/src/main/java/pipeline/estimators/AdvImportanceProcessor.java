package pipeline.estimators;

import gae_store_space.SentenceKind;

import java.util.Collections;
import java.util.Comparator;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

public class AdvImportanceProcessor implements ImportanceProcessor {
	private static Integer MAX_SENT_LENGTH_EST = 20;  // может и превысит
	private static Integer SCALE_FACTOR = 10;
	private static Integer MAX_FOR_LOW_FREQ = 5;
	private static Integer PROCESS_THRESH = 1;
	
	private Integer getLocalMaxSentenceLength(ImmutableSet<SentenceKind> s) {
		SentenceKind elem = Collections.max(s, 
				new Comparator<SentenceKind>() {
					@Override
					public int compare(SentenceKind o1, SentenceKind o2) {
						return Integer.compare(o1.getCountWords(), o2.getCountWords());
					}
				});
		return elem.getCountWords();
	}
	
	@Override
	public Integer process(Integer freq, Set<SentenceKind> s) {
		Integer r = freq * SCALE_FACTOR;
		
		// if (f < 5)
		// FIXME: не ясно как отмасштабировать - если не одно предложение, может самое длинное?
		// FIXME: пока int но вообще лучше что-то поточнее
		if (freq.equals(PROCESS_THRESH)) {
			Integer maxLocalLength = getLocalMaxSentenceLength(ImmutableSet.copyOf(s));
			Double tmp = (1.0 * maxLocalLength * MAX_FOR_LOW_FREQ) / (PROCESS_THRESH * MAX_SENT_LENGTH_EST) * SCALE_FACTOR;
			r = tmp.intValue();
		}
		
		return r;
	}
}
