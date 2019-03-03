package http_api;

import java.util.ArrayList;

public class ValueWordData {
	public ValueWordData(String word, ArrayList<String> sentences, Integer p) {
		this.word = word;
		this.sentences = sentences;
		this.pointPos = p;
	}
	public final String word;  // хорошо бы Optional, но скорее всего не сереализуется
	public final ArrayList<String> sentences;
	public final Integer pointPos;

	public Integer getImportance() {
		return importance;
	}

	private Integer importance = 0;

	//public Integer getImportance
	
	public void setImportance(Integer val) {
		this.importance = val;
	}

	public Integer getMaxImportance() {
		return maxImportance;
	}

	private Integer maxImportance = 0;
	
	public void setMaxImportance(Integer val) {
		this.maxImportance = val;
	}
	
	// cluster range name - важность слова - три или 4 группы
}