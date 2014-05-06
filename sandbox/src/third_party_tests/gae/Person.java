package third_party_tests.gae;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.condition.IfTrue;
import com.googlecode.objectify.condition.IfNotNull;
import com.googlecode.objectify.condition.IfNotZero;

@Entity
public class Person {
  @Id Long id;
  String name;

  // The admin field is only indexed when it is true
  @Index(IfTrue.class) boolean admin;

  // You can provide multiple conditions, any of which will satisfy
  @Index({IfNotNull.class, IfNotZero.class}) Long serialNumber;
}