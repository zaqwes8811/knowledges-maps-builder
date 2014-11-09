package pipeline.mapreduce;

public final class Word {
	public final String stem;
	public final String source;
	
	public Word(String stem, String source) {
		this.stem = stem;
		this.source = source;
	}
} 