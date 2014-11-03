package gae_store_space;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
public class SentenceKind {
  // ctor/...
  private SentenceKind() {}
  
  public SentenceKind(String sentence) {
    this.sentence = sentence;
  }

  @Id
  Long id;

  // value <= 500 symbols // TODO: 500 чего именно?
  String sentence;
  
  private Integer countWords;
  
  public void setCountWords(Integer value) {
  	countWords = value;
  }
  
  public Integer getCountWords() {
  	return countWords;
  }

  public String getSentence() {
    return sentence;
  }

  // tools
  @Override
  public String toString() {
    return "Value = "+sentence;
  }
}
