package pipeline.mapreduce;

import pipeline.ContentItem;

import java.util.List;

public interface CounterMapper {

	public void map(List<ContentItem> contentItemKinds);
}
