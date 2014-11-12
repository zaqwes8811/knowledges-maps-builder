package pipeline.math;


import java.util.ArrayList;
import java.util.Random;

import net.jcip.annotations.Immutable;

import org.javatuples.Pair;
import org.javatuples.Triplet;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

// TODO: Вещь довольно законченная, пока расширять не хочу.
// TODO: Проблема!! Похоже нужно обнулять частоты. Иначе индексы собъются.
// TODO: но как быть при выборке. while(not null)? Это может быть долго...
// TODO: Стоп - нулевые не должны вообще выпадать!
@Immutable
public final class GeneratorAnyDistribution {
	// All finals was removed.
  private final Integer countPoints_;
  private final Integer maxValue_;
  private final ImmutableList<ImmutableList<Integer>> codeBook_;
  private final Integer IDX_POSITION_ = 2;
  private final ArrayList<DistributionElement> d;
  
  public static GeneratorAnyDistribution create(ArrayList<DistributionElement> distribution) {
  	if (distribution == null)
  		throw new IllegalArgumentException();
  	
    if (distribution.isEmpty())
    	throw new IllegalArgumentException();
    
    return new GeneratorAnyDistribution(distribution);
  }

  private GeneratorAnyDistribution(ArrayList<DistributionElement> distribution) {
  	d = distribution;
  	
    // Transpose
    ArrayList<Integer> transposed = new ArrayList<Integer>();
    int countActive = 0;
    for (DistributionElement elem: distribution)
      if (elem.isActive()) {
        transposed.add(elem.getImportance());
    		countActive++;
      } else {
        transposed.add(0);  // просто обнуляем частоту, она не появится
      }
    
    if (countActive < 2)
    	throw new IllegalArgumentException("No active points");

    Triplet<ArrayList<Integer>, Integer, Integer> tupleFx = makeFx(transposed);
    ArrayList<Integer> Fx = tupleFx.getValue0();
    countPoints_ = tupleFx.getValue1();
    maxValue_ = tupleFx.getValue2();
    codeBook_ = ImmutableList.copyOf(makeRanges(Fx));
  }

  // 0..N-1
  public Integer getPosition() {
    // Используется рекурсивная реализация на базе бинарного поиска.
    // На модели она показала наилучшую масштабирумость и скорость работы.
    Float value = new Random().nextFloat()* maxValue_;
    
    Pair<Boolean, Optional<ImmutableList<Integer>>> s = split(codeBook_, countPoints_, value);
    
    if (!s.getValue1().isPresent())
    	throw new RuntimeException("No finded");
    
    // FIXME: тут однажды упало
    ImmutableList<Integer> result =  s.getValue1().get();
    Integer r = result.get(IDX_POSITION_);
    
    if (!d.get(r).isActive())
    	throw new IllegalStateException();
    
    return r;
  }

  private Triplet<ArrayList<Integer>, Integer, Integer> makeFx(ArrayList<Integer> distribution) {
    ArrayList<Integer> Fx = new ArrayList<Integer>();
    Integer Fxi = 0;
    Integer count = 0;
    for (final Integer frequency: distribution) {
      Fxi += frequency;
      Fx.add(Fxi);
      count++;
    }
    return Triplet.with(Fx, count, Fxi);
  }

  private ArrayList<ImmutableList<Integer>> makeRanges(ArrayList<Integer> Fx) {
    ArrayList<ImmutableList<Integer>> ranges = new ArrayList<ImmutableList<Integer>>();
    ranges.add(ImmutableList.of(0, Fx.get(0), 0));
    for (Integer i = 0; i < countPoints_ -1; i++) {
      ranges.add(ImmutableList.of(Fx.get(i), Fx.get(i+1), i+1));
    }
    return ranges;
  }

  private Pair<Boolean, Optional<ImmutableList<Integer>>> split(
    ImmutableList<ImmutableList<Integer>> ranges, Integer n, Float value) {
    Boolean contain = isContain(ranges, n, value);
    if (!contain) {
      Optional<ImmutableList<Integer>> none = Optional.absent();
      return Pair.with(false, none);
    }
    if (n == 1)
      return Pair.with(true, Optional.of(ranges.get(0)));
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
    return ranges.get(0).get(0) < value
      && value <= ranges.get(n-1).get(1);
  }
}
