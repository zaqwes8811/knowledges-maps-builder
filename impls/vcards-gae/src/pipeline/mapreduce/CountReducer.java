package pipeline.mapreduce;


public interface CountReducer<V> {
	public void reduce(String key, V value);
}
