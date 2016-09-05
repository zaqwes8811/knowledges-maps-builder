package backend.mapreduce;

import backend.ContentItem;

import java.util.List;

public interface CounterMapper {

	public void map(List<ContentItem> contentItemKinds);
}
