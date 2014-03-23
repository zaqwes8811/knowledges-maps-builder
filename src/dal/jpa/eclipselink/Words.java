package dal.jpa.eclipselink;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Words {
  @Id
  @GeneratedValue(strategy = GenerationType.TABLE)
  private Integer id;
  private String value;

  // Хеши предложений
  @OneToMany(mappedBy = "hashSentence")
  private final Set<Integer> sentences = new HashSet<Integer>();

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

  public Set<Integer> getMembers() {
    return sentences;
  }
}
