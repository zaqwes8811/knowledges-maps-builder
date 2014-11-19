package gae_store_space;

import com.google.common.base.Optional;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Serialize;

import java.util.HashSet;
import java.util.Set;

// Must be full thread-safe
//
// Очень важен - попробую им гарантировать согласованность
@Entity
public class UserSummaryKind {
  private UserSummaryKind() {
    if (!Optional.fromNullable(pagesNames).isPresent())
      pagesNames = new HashSet<>();
  }

  @Id
  private Long id;

  @Serialize
  private Set<String> pagesNames;

  public synchronized boolean tryPushPage(String pageName) {
    if (pagesNames.contains(pageName))
      return false;

    pagesNames.add(pageName);
    return true;
  }

  public synchronized boolean isContain(String pageName) {
    return pagesNames.contains(pageName);
  }
}
