package gae_store_space;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import java.util.HashSet;
import java.util.Set;

// Must be full thread-safe
//
// Очень важен - попробую им гарантировать согласованность
@Entity
public class UserSummaryKind {
  @Id
  Long id;

  Set<String> pagesNames = new HashSet<>();

  public synchronized boolean tryPushPage(String pageName) {
    return true;
  }
}
