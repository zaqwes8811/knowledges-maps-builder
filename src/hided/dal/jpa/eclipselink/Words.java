package hided.dal.jpa.eclipselink;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Words {
  @Id
  @GeneratedValue(strategy = GenerationType.TABLE)
  private Integer id;
  private String value;

  private final Set<Sentences> sentences = new HashSet<Sentences>();

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String description) {
    this.value = description;
  }

  @OneToMany
  public Set<Sentences> getSentences() {
    return sentences;
  }
}
