package business.math;

import java.util.ArrayList;

/**
 * Created by zaqwes on 5/14/14.
 */
public interface GeneratorDistributions {
  public static class DistributionElement implements Comparable<DistributionElement> {
    public Integer frequency;
    public Boolean enabled;

    @Override
    public int compareTo(DistributionElement o2) {
      return frequency.compareTo(o2.frequency);
    }
  }

  public Integer getPosition();
  public void reloadGenerator(ArrayList<Integer> distribution);
}
