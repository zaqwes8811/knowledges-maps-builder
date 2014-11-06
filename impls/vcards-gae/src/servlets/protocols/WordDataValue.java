package servlets.protocols;

import java.util.ArrayList;

public class WordDataValue {
	public WordDataValue(String word, ArrayList<String> sentences, Integer p) {
		this.word = word;
		this.sentences = sentences;
		this.pointPos = p;
	}
	public final String word;  // хорошо бы Optional, но скорее всего не сереализуется
	public final ArrayList<String> sentences;
	public final Integer pointPos;
	public Integer importance = 0;
	
	public void setImportance(Integer val) {
		this.importance = val;
	}
	
	// cluster range name - важность слова - три или 4 группы
}