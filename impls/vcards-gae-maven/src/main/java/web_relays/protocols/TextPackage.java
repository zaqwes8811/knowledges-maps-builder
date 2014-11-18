package web_relays.protocols;

import com.google.common.base.Optional;

/**
 * Created by zaqwes on 11/19/2014.
 */
public class TextPackage {
  public TextPackage() {}

  public void setText(String text) {
    this.text = text;
  }

  public void setName(String name) {
    this.name = name;
  }

  String text;
  String name;

  public Optional<String> getText() {
    return Optional.fromNullable(text);
  }

  public Optional<String> getName() {
    return Optional.fromNullable(name);
  }
}