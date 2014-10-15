package servlets.protocols;

import java.util.ArrayList;

public class WordDataValue {
	public WordDataValue(String word, ArrayList<String> sentences) {
		this.word = word;
		this.sentences = sentences;
	}
	public final String word;  // хорошо бы Optional, но скорее всего не сереализуется
	public final ArrayList<String> sentences;
	
	public final Integer pointPos = 0;  // FIXME: impl.
	
	// cluster range name - важность слова - три или 4 группы
}