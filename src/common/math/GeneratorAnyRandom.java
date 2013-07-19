package common.math;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: кей
 * Date: 19.07.13
 * Time: 13:52
 * To change this template use File | Settings | File Templates.
 */
public class GeneratorAnyRandom {
  private final Integer COUNT_POINTS;

  private List<Integer> makeFx(List<Integer> distribution) {
    List<Integer> Fx = new ArrayList<Integer>();
    Integer Fxi = 0;
    for (final Integer frequency: distribution) {
      Fxi += frequency;
      Fx.add(Fxi);
    }
    return Fx;
  }

  private GeneratorAnyRandom(List<Integer> distribution, Integer size) {
    // Кодовую книгу нужно задать жестко
    COUNT_POINTS = size;
  }

  //
  public static GeneratorAnyRandom create(List<Integer> distribution, Integer size) {

  }

  public static void main(String[] args) {

  }
}
