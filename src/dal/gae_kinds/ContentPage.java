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

  // TODO: troubles. Может добавить метод выкалывания точек?
  // TODO: Может лучше сделать ссылкой-ключом?
  GeneratorAnyDistributionImpl gen;  // TODO: Как быть с ним? Они логическое целое.

  /// Methods
  private ContentPage() { }

  private boolean isEmpty() {
    return false;
  }

  // TODO: возможно нужен кеш. см. Guava cache.
  public ImmutableList<GeneratorAnyDistributionImpl.DistributionElement> getDistribution() {
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
