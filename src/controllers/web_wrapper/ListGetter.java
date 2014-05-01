package controllers.web_wrapper;

import com.google.common.collect.ImmutableList;

@Deprecated
public interface ListGetter {
  // Index: нужно для маркеровки
  // Word: само слово
  // Translates:
  // Context:
  ImmutableList<ImmutableList<ImmutableList<String>>> getPerWordData();
}
