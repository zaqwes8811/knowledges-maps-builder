package core.math;

//http://stackoverflow.com/questions/5560176/is-integer-immutable
// сохраняется в базе!
public class DistributionElement implements Comparable<DistributionElement> {
  // FIXME: make get/set
	public Integer frequency;
  public Boolean enabled;
  
  // FIXME: for persist
  DistributionElement() { }
  
  public DistributionElement(Integer freq, Boolean ena) {
    frequency = freq;
    enabled = ena;
  }
  
  public  DistributionElement(Integer freq) {
    enabled = true;
    frequency = freq;
  }

  @Override
  public int compareTo(DistributionElement o2) {
    return frequency.compareTo(o2.frequency);
  }
}