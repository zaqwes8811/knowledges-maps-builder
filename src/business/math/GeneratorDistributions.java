package business.math;

import com.google.appengine.repackaged.org.apache.http.annotation.Immutable;

import java.util.ArrayList;

// About:
//
// Conditions:
//   Индексы с нулевой частоной выпадать не должны.
public interface GeneratorDistributions {

  @Immutable  // TODO: Maybe
  public static class DistributionElement implements Comparable<DistributionElement> {
    // http://stackoverflow.com/questions/5560176/is-integer-immutable
    public final Integer frequency;
    public final Boolean enabled;

    public DistributionElement(Integer freq, Boolean ena) {
      frequency = freq;
      enabled = ena;
    }

    @Override
    //@NotNull  // TODO: Какое-то предупреждение в idea ide
    public int compareTo(DistributionElement o2) {
      return frequency.compareTo(o2.frequency);
    }
  }

  public Integer getPosition();
  public void reloadGenerator(ArrayList<DistributionElement> distribution);
}
