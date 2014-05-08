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

  // List coupled content items.

  // TODO: Хорошо бы сохранять их, а не просто слова. Почитать Effective java.
  // Идея на этапе mapreduce передавать в качестве ключей объекты слов, и пользоваться
  // ими как ключами. Но слова имеют ссылки, то сразу большие проблемы с эквивалетностью!
  // На этапе формирования индекса может все не так просто, но потом метод equals будет работать очень странно.
  // TODO:Stop it!
  // equals()
  // hashCode() - need it?
}
