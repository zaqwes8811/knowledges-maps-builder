package backend.mapreduce;

public final class Word {
	public final String stem;
	public final String source;
	
	public Word(String stem, String source) {
		this.stem = stem;
		this.source = source;
	}
	
	public static Word build(String value) {
		/*englishStemmer stemmer = new englishStemmer();
  	String lowWord = value.toLowerCase();
  	stemmer.setCurrent(lowWord);
    if (stemmer.stem()) {
      return new Word(stemmer.getCurrent(), value);  
    } else {*/
      return new Word(value, value); 
    //}
  }
} 