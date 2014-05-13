package dal.gae_kinds;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import java.util.ArrayList;

/**
 * Created by zaqwes on 5/14/14.
 */
@Entity
public class DistributionGenBuilder {
  @Id
  Long id;
  public GeneratorAnyDistributionImpl create(ArrayList<Integer> distribution) {
    return GeneratorAnyDistributionImpl.create(distribution);
  }
}
