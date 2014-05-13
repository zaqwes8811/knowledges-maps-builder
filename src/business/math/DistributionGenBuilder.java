package business.math;

import java.util.ArrayList;

/**
 * Created by zaqwes on 5/14/14.
 */
public class DistributionGenBuilder {
  public GeneratorAnyDistributionImpl create(ArrayList<Integer> distribution) {
    return GeneratorAnyDistributionImpl.create(distribution);
  }
}
