package business.math;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by zaqwes on 5/12/14.
 */
public class GeneratorAnyDistributionTest {
  @Test
  public void testCreate() throws  RandomGeneratorException {
    List<Integer> distribution = new ArrayList<Integer>(Arrays.asList(1, 6, 0, 14, 5, 7));

    GeneratorAnyDistribution generator = GeneratorAnyDistribution.create(distribution);
    List<Integer> experiment = new ArrayList<Integer>();
    for (int i = 0; i < 10000; ++i)
      //experiment.add(distribution.get(generator.getCodeWord()));
      experiment.add((generator.getCodeWord()));

    System.out.println(experiment);
  }
}
