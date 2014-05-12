package dal.gae_kinds;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

/**
 * Created by zaqwes on 5/9/14.
 */
@Entity
public class Word {
  @Id
  Long id;
  @Index
  String word;
  @Index
  Integer frequency;  // Сколько раз встретилось слово.

  public void setFrequency(Integer value) {
    frequency = value;
  }

  Integer sortedIdx;  // 0-N в порядке возрастания по frequency

  // List coupled content items.
  public void addContentItem(ContentItem item) {

  }

  //ArrayList<Ref<ContentItem>> refs;


  // TODO: Хорошо бы сохранять их, а не просто слова. Почитать Effective java.
  // Идея на этапе mapreduce передавать в качестве ключей объекты слов, и пользоваться
  // ими как ключами. Но слова имеют ссылки, то сразу большие проблемы с эквивалетностью!
  // На этапе формирования индекса может все не так просто, но потом метод equals будет работать очень странно.
  // TODO:Stop it!
  // equals()
  // hashCode() - need it?
  private Word() {}
  public Word(String word) {
    this.word = word;
    sortedIdx = -1;
  }
}
