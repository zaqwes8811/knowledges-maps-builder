package store_gae_kinds;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

/**
 * About:
 *
 */
@Entity
public class ContentItem {
  /// Persist
  //@Parent // TODO: как быть если делать выборку по parent? Может быть несколько parents? Кажется нет.
  @Id
  Long id;

  // value <= 500 symbols
  // TODO: 500 чего именно?
  String item;

  @Index
  Long idx;
  //Long size;

  /// Own
  public String get() {
    return item;
  }
  public Long getId() { return id; }

  public void setIdx(Long value) {
    idx = value;
  }

  private ContentItem() {}

  public ContentItem(String item) {
    this.item = item;
  }

  public String toString() {
    return "Value = "+item;
  }
}
