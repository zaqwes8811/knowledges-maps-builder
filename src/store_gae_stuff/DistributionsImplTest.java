package store_gae_stuff;

import business.math.DistributionElement;
import business.math.GeneratorDistributionException;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertFalse;

/**
 * Created by zaqwes on 5/12/14.
 */
public class DistributionsImplTest {
  @Test
  public void testCreate() throws GeneratorDistributionException {
    ArrayList<Integer> distribution = new ArrayList<Integer>(Arrays.asList(1, 6, 0, 14, 5, 7));
    ArrayList<DistributionElement> real = new ArrayList<DistributionElement>();

    for (Integer value: distribution) {
      real.add(new DistributionElement(value, true));
    }

    Distributions generator = DistributionsImpl.create(real);
    List<Integer> experiment = new ArrayList<Integer>();
    for (int i = 0; i < 10000; ++i)
      experiment.add(generator.getPosition());

    assertFalse(experiment.contains(new Integer(distribution.indexOf(0))));
  }
}
