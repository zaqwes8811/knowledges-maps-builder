package core.math;

//import com.google.appengine.repackaged.org.apache.http.annotation.Immutable;

/**
 * Created by zaqwes on 10/9/14.
 */

public class DistributionElement implements Comparable<DistributionElement> {
  // http://stackoverflow.com/questions/5560176/is-integer-immutable
  public Integer frequency;
  public Boolean enabled;

  public DistributionElement(Integer freq, Boolean ena) {
    frequency = freq;
    enabled = ena;
  }
  
  // FIXME: for persist
  DistributionElement() { }

  public  DistributionElement(Integer freq) {
    enabled = true;
    frequency = freq;
  }

  @Override
  public int compareTo(DistributionElement o2) {
    return frequency.compareTo(o2.frequency);
  }
}