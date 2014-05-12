package dal.gae_kinds;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Load;

import java.util.ArrayList;
import java.util.List;

/**
 * About: Отражает один элемент данный пользователя, например, один файл субтитров.
 */
@Entity
public class ContentPage {
  @Id
  Long id;

  @Index String name;

  private ContentPage() {}


  public ContentPage(String name) {
    this.name = name;
  }

  // Content items
  @Load
  List<Key<ContentItem>> items = new ArrayList<Key<ContentItem>>();

  public void setItems(List<ContentItem> list) {
    for (ContentItem item: list) {
      items.add(Key.create(item));
    }
  }

  public List<Key<ContentItem>> getItems() { return items; }

  // Words
  @Load
  List<Key<Word>> words = new ArrayList<Key<Word>>();
}
