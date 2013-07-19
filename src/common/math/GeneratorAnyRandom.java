package common.math;

import com.google.common.base.Optional;
import com.google.common.collect.*;
import org.javatuples.Pair;
import org.javatuples.Triplet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: кей
 * Date: 19.07.13
 * Time: 13:52
 * To change this template use File | Settings | File Templates.
 */
public class GeneratorAnyRandom {
  private final Integer COUNT_POINTS;
  private final Integer MAX_VALUE;
  private final ImmutableList<ImmutableList<Integer>> CODE_BOOK;

  private Triplet<List<Integer>, Integer, Integer> makeFx(List<Integer> distribution) {
    List<Integer> Fx = new ArrayList<Integer>();
    Integer Fxi = 0;
    Integer count = 0;
    for (final Integer frequency: distribution) {
      Fxi += frequency;
      Fx.add(Fxi);
      count++;
    }
    return Triplet.with(Fx, count, Fxi);
  }

  private List<ImmutableList<Integer>> makeRanges(List<Integer> Fx) {
    List<ImmutableList<Integer>> ranges = new ArrayList<ImmutableList<Integer>>();
    ranges.add(ImmutableList.of(0, Fx.get(0), 0));
    for (Integer i = 0; i < COUNT_POINTS-1; i++) {
      ranges.add(ImmutableList.of(Fx.get(i), Fx.get(i+1), i+1));
    }
    return ranges;
  }

  private GeneratorAnyRandom(List<Integer> distribution) {
    Triplet<List<Integer>, Integer, Integer> resultMakeFx = makeFx(distribution);
    List<Integer> Fx = resultMakeFx.getValue0();
    COUNT_POINTS = resultMakeFx.getValue1();
    MAX_VALUE = resultMakeFx.getValue2();
    CODE_BOOK = ImmutableList.copyOf(makeRanges(Fx));
  }

  public Integer getCodeWord() {
    Integer INTERVAL_POS = 1;
    Integer IDX_POS = 2;
    Float value = new Random().nextFloat()*MAX_VALUE;
    //ImmutableList<ImmutableList<Integer>> result =
    split(CODE_BOOK, COUNT_POINTS, value);
    return 0;//result[INTERVAL_POS][IDX_POS]
  }

  private Pair<Boolean, Optional<ImmutableList<Integer>>> split(
    ImmutableList<ImmutableList<Integer>> ranges, Integer n, Float value) {
    Boolean contain = isContain(ranges, n, value);
    if (!contain) {
      Optional<ImmutableList<Integer>> none = Optional.absent();
      return Pair.with(false, none);
    }
    if (n == 1) return Pair.with(contain, Optional.of(ranges.get(0)));
    else {
      return Pair.with(false, Optional.of(ImmutableList.of(0)));
    }
  }

  private Boolean isContain(ImmutableList<ImmutableList<Integer>> ranges, Integer n, Float value) {
      return ranges.get(0).get(0) < value && value <= ranges.get(n-1).get(1);
  }

  //
  public static GeneratorAnyRandom create(List<Integer> distribution) {
    return new GeneratorAnyRandom(distribution);
  }

  public static void main(String[] args) {
    List<Integer> distribution = new ArrayList<Integer>(Arrays.asList(1,2,3,4,5));

    GeneratorAnyRandom generator = GeneratorAnyRandom.create(distribution);
    generator.getCodeWord();
  }
}
