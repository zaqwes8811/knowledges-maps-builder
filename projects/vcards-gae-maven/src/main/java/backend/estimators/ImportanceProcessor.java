package backend.estimators;

import backend.ContentItem;

import java.util.Set;

public interface ImportanceProcessor {
	public Integer process(Integer freq, Set<ContentItem> s);
}
