package common.math;

import com.google.common.collect.*;

import java.util.ArrayList;
import java.util.Arrays;
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
  private final ImmutableList<ImmutableList<Integer>> CODE_BOOK;

  private List<Integer> makeFx(List<Integer> distribution) {
    List<Integer> Fx = new ArrayList<Integer>();
    Integer Fxi = 0;
    for (final Integer frequency: distribution) {
      Fxi += frequency;
      Fx.add(Fxi);
    }
    return Fx;
  }

  private List<ImmutableList<Integer>> makeRanges(List<Integer> Fx) {
    List<ImmutableList<Integer>> ranges = new ArrayList<ImmutableList<Integer>>();
    ranges.add(ImmutableList.of(0, Fx.get(0), 0));
    for (Integer i = 0; i < COUNT_POINTS-1; i++) {
      ranges.add(ImmutableList.of(Fx.get(i), Fx.get(i+1), i+1));
    }
    return ranges;
  }

  private GeneratorAnyRandom(List<Integer> distribution, Integer size) {
    // Кодовую книгу нужно задать жестко
    COUNT_POINTS = size;
    CODE_BOOK = ImmutableList.copyOf(makeRanges(makeFx(distribution)));
  }

  //
  public static GeneratorAnyRandom create(List<Integer> distribution, Integer size) {
    return new GeneratorAnyRandom(distribution, size);
  }

  public static void main(String[] args) {
    List<Integer> distribution = new ArrayList<Integer>(Arrays.asList(1,2,3,4,5));

    GeneratorAnyRandom generator = GeneratorAnyRandom.create(distribution, distribution.size());
  }
}
