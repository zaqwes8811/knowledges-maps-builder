package gae_store_space;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
public class UserKind {
  @Id Long id;

  // List<ContentPageKind> pages - Пока страницы независимы от пользователей в базе данных
  // но зависимы при действиях от лица пользователя.
}
