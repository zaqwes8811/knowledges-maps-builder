package business.math.hided;

import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: кей
 * Date: 09.07.13
 * Time: 19:24
 * To change this template use File | Settings | File Templates.
 */
public class Randomizers {
  private Randomizers() {}

  public static Randomizer create(Integer maxValue) {
    return new UniformRandomizer(maxValue);
  }

  private static class UniformRandomizer implements Randomizer {
    public UniformRandomizer(Integer maxValue) {
      MAX_VALUE = maxValue;
    }
    private final Integer MAX_VALUE;
    private final Random RANDOM = new Random();

    @Override
    public Integer getSample() {
      return RANDOM.nextInt(MAX_VALUE);
    }
  }
}