package third_party_tests.gae;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Ignore;

@Entity
public class BigCar {
  @Id
  Long id;
  String vin;
  int color;
  byte[] rawData;
  @Ignore int irrelevant;

  private BigCar() {}

  public BigCar(String vin, int color) {
    this.vin = vin;
    this.color = color;
  }
}