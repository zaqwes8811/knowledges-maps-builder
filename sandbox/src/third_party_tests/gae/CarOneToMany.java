package third_party_tests.gae;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Load;

@Entity
public class CarOneToMany {
  @Id
  Long id;
  String vin;
  int color;
  //byte[] rawData;
  //@Ignore int irrelevant;

  // Coupling
  @Load // TODO: Why?
  Ref<Engine> engine;

  private CarOneToMany() {}

  public CarOneToMany(String vin, int color) {
    this.vin = vin;
    this.color = color;
  }

  public Engine getEngine() { return engine.get(); }
  public void setEngine(Engine value) { engine = Ref.create(value); }
}