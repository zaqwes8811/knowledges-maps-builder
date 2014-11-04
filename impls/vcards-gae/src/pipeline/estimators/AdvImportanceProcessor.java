package pipeline.estimators;

import gae_store_space.SentenceKind;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

public class AdvImportanceProcessor implements ImportanceProcessor {
	private Integer getMaxLength(ImmutableSet<SentenceKind> s) {
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
		Integer r = freq;
		
		// if (f < 5)
		// FIXME: не ясно как отмасштабировать - если не одно предложение, может самое длинное?
		// FIXME: пока int но вообще лучше что-то поточнее
		if (freq.equals(1)) {
			
		}
		
		return r;
	}
}
