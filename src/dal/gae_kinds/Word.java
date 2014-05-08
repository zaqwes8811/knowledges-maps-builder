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
  // hashCode() - need it?
}
