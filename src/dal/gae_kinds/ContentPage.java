package dal.gae_kinds;

import business.math.GeneratorAnyDistributionImpl;
import com.google.common.collect.ImmutableList;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Load;

import java.util.ArrayList;
import java.util.List;

import static dal.gae_kinds.OfyService.ofy;

/**
 * About: Отражает один элемент данный пользователя, например, один файл субтитров.
 */
@Entity
public class ContentPage {
  @Id
  Long id;

  @Index
  String name;
  @Load
  List<Key<Word>> words = new ArrayList<Key<Word>>();
  @Load
  List<Key<ContentItem>> items = new ArrayList<Key<ContentItem>>();

  private ContentPage() { }

  private boolean isEmpty() {
    return false;
  }

  // TODO: возможно нужен кеш. см. Guava cache.
  public ImmutableList<GeneratorAnyDistributionImpl.DistributionElement> getDistribution() {
    List<ContentItem> coupled = ofy().load().type(ContentItem.class)
      .filterKey("in", getItems()).list();

    return null;//ImmutableList.copyOf(new GeneratorAnyDistributionImpl.DistributionElement());
  }

  public ContentPage(String name, List<ContentItem> list, List<Word> words) {
    this.name = name;
    this.setWords(words);
    this.setItems(list);
  }

  // TODO: Удяляет на что ссылается из хранилища.
  public void empty() {

  }

  private void setItems(List<ContentItem> list) {
    for (ContentItem item: list) {
      items.add(Key.create(item));
    }
  }

  public List<Key<ContentItem>> getItems() {
    return items;
  }

  private void setWords(List<Word> words) {
    for (Word word: words) {
      this.words.add(Key.create(word));
    }
  }
}
