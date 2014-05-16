package business.math;

import java.util.ArrayList;

// About:
//
// Conditions:
//   Индексы с нулевой частоной выпадать не должны.
public interface GeneratorDistributions {
  public Integer getPosition();
  public void reloadGenerator(ArrayList<GeneratorAnyDistribution.DistributionElement> distribution);
}
