package gae_store_space;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
public class ContentItemKind {
  // ctor/...
  private ContentItemKind() {}
  public ContentItemKind(String item) {
    this.sentence = item;
  }

  @Id
  Long id;

  // value <= 500 symbols // TODO: 500 чего именно?
  String sentence;

  public String getSentence() {
    return sentence;
  }

  // tools
  @Override
  public String toString() {
    return "Value = "+sentence;
  }
}
