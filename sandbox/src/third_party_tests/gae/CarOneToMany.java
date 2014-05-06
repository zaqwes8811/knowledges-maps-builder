package third_party_tests.gae;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
public class CarOneToMany {
  @Id
  Long id;
  String vin;
  int color;
  //byte[] rawData;
  //@Ignore int irrelevant;

  private CarOneToMany() {}

  public CarOneToMany(String vin, int color) {
    this.vin = vin;
    this.color = color;
  }
}