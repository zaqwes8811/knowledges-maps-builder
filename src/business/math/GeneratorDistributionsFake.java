package business.math;

import java.util.ArrayList;

/**
 * Created by zaqwes on 5/15/14.
 */
public class GeneratorDistributionsFake implements GeneratorDistributions {
  @Override
  public Integer getPosition() {
    return 0;
  }

  @Override
  public void reloadGenerator(ArrayList<DistributionElement> distribution) {

  }
}
