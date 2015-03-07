package pipeline.estimators;

import pipeline.ContentItem;

import java.util.Set;

public interface ImportanceProcessor {
	public Integer process(Integer freq, Set<ContentItem> s);
}
