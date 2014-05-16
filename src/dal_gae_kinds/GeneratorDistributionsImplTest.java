package dal_gae_kinds;

import business.math.GeneratorDistributionExc;
import business.math.GeneratorDistributions;
import dal_gae_kinds.GeneratorDistributionsImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertFalse;

/**
 * Created by zaqwes on 5/12/14.
 */
public class GeneratorDistributionsImplTest {
  @Test
  public void testCreate() throws GeneratorDistributionExc {
    ArrayList<Integer> distribution = new ArrayList<Integer>(Arrays.asList(1, 6, 0, 14, 5, 7));

    GeneratorDistributions generator = GeneratorDistributionsImpl.create(distribution);
    List<Integer> experiment = new ArrayList<Integer>();
    for (int i = 0; i < 10000; ++i)
      experiment.add(generator.getPosition());

    assertFalse(experiment.contains(new Integer(distribution.indexOf(0))));
  }
}
