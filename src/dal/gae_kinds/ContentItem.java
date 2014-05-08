package dal.gae_kinds;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

/**
 * Created by zaqwes on 5/9/14.
 */
@Entity
public class ContentItem {
  //@Parent // TODO: как быть если делать выборку по parent? Может быть несколько parents? Кажется нет.
  @Id
  Long id;

  // value <= 500 symbols
  // TODO: 500 чего именно?
  String item;

  // Some add data for sorting on query.

  public String getItem() {
    return item;
  }

  private ContentItem() {}

  public ContentItem(String item) {
    this.item = item;
  }
}
