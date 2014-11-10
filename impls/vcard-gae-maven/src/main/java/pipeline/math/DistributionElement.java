package pipeline.math;

import java.io.Serializable;

import cross_cuttings_layer.CrossIO;

//http://stackoverflow.com/questions/5560176/is-integer-immutable - сохраняется в базе!
public class DistributionElement implements Comparable<DistributionElement>, Serializable {
  private static final long serialVersionUID = -3004460083142639254L;

	private Integer frequency;
  private Boolean unknown = true;
  private Boolean inBoundary = false;
  
  public Integer getImportancy()
  {
  	return frequency;
  }
  
  public void markInBoundary() {
  	inBoundary = true;
  }
  
  public void markKnown() {
  	unknown = false;
  }
  
  public Boolean isUnknown() {
  	return unknown;
  }
  
  public void setBoundary(Boolean value) {
  	inBoundary = value;
  }
  
  public Boolean isActive() {
  	return unknown && inBoundary;
  }
  
  public DistributionElement(Integer freq, Boolean ena) {
    frequency = freq;
    unknown = ena;
  }
  
  public DistributionElement(Integer freq) {
    frequency = freq;
  }

  @Override
  public int compareTo(DistributionElement o2) {
    return frequency.compareTo(o2.frequency);
  }
}