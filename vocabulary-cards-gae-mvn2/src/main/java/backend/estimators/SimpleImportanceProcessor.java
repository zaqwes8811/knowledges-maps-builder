package backend.estimators;

import backend.ContentItem;

import java.util.Set;

public class SimpleImportanceProcessor implements ImportanceProcessor {
	@Override
	public Integer process(Integer freq, Set<ContentItem> s) {
		return freq;
	}
}