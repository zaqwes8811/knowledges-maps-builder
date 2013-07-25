package common.math;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import org.javatuples.Pair;
import org.javatuples.Triplet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

// Класс способен генерировать последовательности любого дискретного распределения
//
// На вход подается...
//
// TODO(zaqwes): ImmutableLists для триплета избыточны, лучше сделать через Tuples - Triplet
//
// @Immutable
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
    // Используется рекурсивная реализация на базе бинарного поиска.
    // На модели она показала наилучшую масштабирумость и скорость работы.
    Integer INTERVAL_POS = 1;
    Integer IDX_POS = 2;
    Float value = new Random().nextFloat()*MAX_VALUE;
    ImmutableList<Integer> result =  split(CODE_BOOK, COUNT_POINTS, value).getValue1().get();
    return result.get(2);
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
      Integer oneSize = n/2;
      Integer twoSize = n-oneSize;
      Pair<Boolean, Optional<ImmutableList<Integer>>> resultSubTree =
          split(ranges.subList(0, oneSize), oneSize, value);
      if (!resultSubTree.getValue0()) {
        resultSubTree = split(ranges.subList(oneSize, n), twoSize, value);
      }
      return resultSubTree;
    }
  }

  private Boolean isContain(ImmutableList<ImmutableList<Integer>> ranges, Integer n, Float value) {
      return ranges.get(0).get(0) < value && value <= ranges.get(n-1).get(1);
  }

  // Любой список с числами
  // @throws: RandomGeneratorException
  public static GeneratorAnyRandom create(List<Integer> distribution) {
    if (distribution.isEmpty()) throw new RandomGeneratorException("In list must be no empty.");
    return new GeneratorAnyRandom(distribution);
  }

  public static void main(String[] args) throws  RandomGeneratorException {
    List<Integer> distribution = new ArrayList<Integer>(Arrays.asList(1,6,0,14,5,7));

    GeneratorAnyRandom generator = GeneratorAnyRandom.create(distribution);
    List<Integer> experiment = new ArrayList<Integer>();
    for (int i = 0; i < 10000; ++i)
      //experiment.add(distribution.get(generator.getCodeWord()));
      experiment.add((generator.getCodeWord()));

    System.out.println(experiment);
  }
}
