package store_gae_stuff;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

/**
 * About:
 *
 */
@Entity
public class ContentItemKind {
  // ctor/...
  private ContentItemKind() {}
  public ContentItemKind(String item, Long pos) {
    this.sentence = item;
    this.pos = pos;
  }

  /// Persist
  //@Parent // TODO: как быть если делать выборку по parent? Может быть несколько parents? Кажется нет.
  @Id
  Long id;

  // value <= 500 symbols
  // TODO: 500 чего именно?
  String sentence;

  @Index
  Long pos;  // номер в тексте 0..pos..N-1 - все с нуля путь и это будет тоже

  public String getSentence() {
    return sentence;
  }
  public Long getId() { return id; }

  // tools
  @Override
  public String toString() {
    return "Value = "+sentence;
  }
}
