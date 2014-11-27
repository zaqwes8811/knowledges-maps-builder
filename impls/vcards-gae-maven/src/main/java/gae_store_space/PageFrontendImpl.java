package gae_store_space;


import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import cross_cuttings_layer.AssertException;
import cross_cuttings_layer.OwnCollections;
import org.apache.commons.collections4.Predicate;
import org.apache.log4j.Logger;
import org.javatuples.Pair;
import pipeline.math.DistributionElement;
import web_relays.protocols.PathValue;
import web_relays.protocols.WordDataValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

public class PageFrontendImpl implements PageFrontend {
  private PageFrontendImpl() {}
  // Frontend
  // Формированием не управляет, но остальным управляет.
  // Обязательно отсортировано
  private ArrayList<NGramKind> unigramKinds = new ArrayList<>();

  // Хранить строго как в исходном контексте
  private ArrayList<SentenceKind> sentencesKinds = new ArrayList<>();
  private GeneratorKind generatorCache;
  private PageKind kind = null;

  // по столько будем шагать
  // По малу шагать плохо тем что распределение может снова стать равном.
  // 10 * 20 = 200 слов, почти уникальных, лучше меньше
  public static final Integer STEP_WINDOW_SIZE = 8;
  private static final Double SWITCH_THRESHOLD = 0.2;

  // Actions
  private static Logger log = Logger.getLogger(UserKind.class.getName());

  public static PageFrontendImpl buildEmpty() {
    return new PageFrontendImpl();
  }

  public void setGeneratorCache(GeneratorKind generatorCache) {
    this.generatorCache = generatorCache;
  }

  public PageKind getRawPage() {
    if (kind == null)
      throw new AssertionError();

    return kind;
  }

  public void set(PageKind k) {
    if (kind != null)
      throw new AssertionError();

    kind = k;
  }

  @Override
  public ArrayList<Integer> getLengthsSentences() {
    ArrayList<Integer> r = new ArrayList<>();
    for (SentenceKind k : sentencesKinds)
      r.add(k.getCountWords());
    return r;
  }

  private GeneratorKind getGeneratorCache() {
    if (generatorCache == null)
      throw new IllegalStateException();
    return generatorCache;
  }

  public void assign(PageFrontendImpl rhs) {
    unigramKinds = rhs.unigramKinds;
    sentencesKinds = rhs.sentencesKinds;
  }

  // Это при создании с нуля
  public PageFrontendImpl(
    String pageName,
    ArrayList<SentenceKind> items,
    ArrayList<NGramKind> words,
    String rawSource)
  {
    this.unigramKinds = words;
    this.sentencesKinds = items;
    this.kind = new PageKind(pageName, rawSource);
  }

  private Set<String> getNGramms(Integer boundary) {
    Integer end = sentencesKinds.size();

    if (sentencesKinds.size() > boundary)
      end = boundary;

    // [.., end)
    ArrayList<SentenceKind> kinds = new ArrayList<>(sentencesKinds.subList(0, end));

    return PageBuilder.buildPipeline().getNGrams(kinds);
  }

  private Integer getUnigramIndex(String ngram) {
    // FIXME: How hide it?
    class Tmp implements Predicate<NGramKind> {
      @Override
      public boolean evaluate(NGramKind o) {
        return o.getNGram().equals(ngram);
      }

      String ngram;
      public Tmp(String value) {
        ngram = value;
      }
    }

    Tmp p = new Tmp(ngram);

    Pair<NGramKind, Integer> k = OwnCollections.find(unigramKinds, p);
    if (k.getValue1().equals(-1))
      throw new IllegalStateException();

    Integer r = k.getValue1();
    checkAccessIndex(r);

    return r;
  }

  private void checkAccessIndex(Integer idx) {
    if (!(idx < unigramKinds.size())) {
      throw new IllegalArgumentException();
    }
  }

  @Override
  public ArrayList<DistributionElement> getImportanceDistribution() {
    GeneratorKind gen = getGeneratorCache();
    ArrayList<DistributionElement> r = gen.getCurrentDistribution();
    checkDistributionInvariant(r);
    return r;
  }

  private void checkDistributionInvariant(ArrayList<DistributionElement> d) {
    if (d.size() != unigramKinds.size())
      throw new IllegalStateException(
        String.format("Distribution size = %d / Element count = %d"
          , d.size(), unigramKinds.size()));
  }

  // About: Возвращать пустое распределение
  @Override
  public ArrayList<DistributionElement> buildImportanceDistribution() {
    // Сортируем - элементы могут прийти в случайном порядке
    Collections.sort(unigramKinds, NGramKind.createImportanceComparator());
    Collections.reverse(unigramKinds);

    // Form result
    ArrayList<DistributionElement> r = new ArrayList<>();
    for (NGramKind word : unigramKinds)
      r.add(new DistributionElement(word.getImportance()));

    r = applyBoundary(r);

    // генератора еще нету
    return r;
  }

  private ArrayList<DistributionElement> applyBoundary(ArrayList<DistributionElement> d) {
    checkDistributionInvariant(d);

    // Get word befor boundary
    Set<String> ngramms = getNGramms(getRawPage().boundaryPtr);

    for (String ngram: ngramms) {
      Integer index = getUnigramIndex(ngram);
      checkAccessIndex(index);

      // Проверка! Тестов как таковых нет, так что пока так
      if (!unigramKinds.get(index).getNGram().equals(ngram))
        throw new AssertException();

      d.get(index).markInBoundary();
    }
    return d;
  }

  private Integer getMaxImportancy() {
    return getGeneratorCache().getMaxImportance();
  }

  @Override
  public Optional<WordDataValue> getWordData() {
    GeneratorKind go = getGeneratorCache();  // FIXME: нужно нормально обработать

    Integer pointPosition = go.getPosition();
    NGramKind ngramKind =  getNGram(pointPosition);
    String value = ngramKind.pack();
    ImmutableList<SentenceKind> sentenceKinds = ngramKind.getContendKinds();

    ArrayList<String> content = new ArrayList<>();
    for (SentenceKind k: sentenceKinds)
      content.add(k.getSentence());

    WordDataValue r = new WordDataValue(value, content, pointPosition);

    //
    r.setImportance(ngramKind.getImportance());
    // max import
    r.setMaxImportance(getMaxImportancy());

    return Optional.of(r);
  }

  // FIXME: а логика разрешает Отсутствующее значение?
  // http://stackoverflow.com/questions/2758224/assertion-in-java
  // генераторы могут быть разными, но набор слов один.
  private NGramKind getNGram(Integer pos) {
    checkAccessIndex(pos);
    return unigramKinds.get(pos);
  }

  private void IncBoundary() {
    getRawPage().boundaryPtr += STEP_WINDOW_SIZE;
    if (getRawPage().boundaryPtr > sentencesKinds.size())
      getRawPage().boundaryPtr = sentencesKinds.size();
  }

  private Integer getAmongCurrentActivePoints() {
    return getGeneratorCache().getActiveCount();
  }

  private void setVolume(Integer val) {
    getRawPage().referenceVolume = val;
  }

  private void setDistribution(ArrayList<DistributionElement> d) {
    checkDistributionInvariant(d);
    getGeneratorCache().resetDistribution(d);
  }

  private void moveBoundary() {
    Integer currentVolume = getAmongCurrentActivePoints();

    if (currentVolume < SWITCH_THRESHOLD * getRawPage().referenceVolume) {
      // FIXME: no exception safe
      // перезагружаем генератор
      Integer currBoundary = getRawPage().boundaryPtr;
      IncBoundary();

      if (getAmongCurrentActivePoints() < 2)
        IncBoundary();  // пока один раз

      // подошли к концу
      if (!currBoundary.equals(getRawPage().boundaryPtr)) {
        ArrayList<DistributionElement> d = getImportanceDistribution();
        d = applyBoundary(d);

        setDistribution(d);
        setVolume(getAmongCurrentActivePoints());
      }
    }
  }

  @Override
  public void disablePoint(PathValue p) {
    Integer pos = p.pointPos;
    // лучше здесь
    if (getRawPage().referenceVolume.equals(0)) {
      if (getAmongCurrentActivePoints() < 2)
        throw new IllegalStateException();

      // создаем первый объем
      setVolume(getAmongCurrentActivePoints());
    }
    moveBoundary();

    // Читаем заново
    GeneratorKind g = getGeneratorCache();
    checkAccessIndex(pos);
    g.disablePoint(pos);

    // Если накопили все в пределах границы сделано, то нужно сдвинуть границу и перегрузить генератор.
    persist();
  }

  private void persist() {
    getRawPage().persist(getRawPage(), getGeneratorCache());
  }

  @Override
  public void atomicDeleteRawPage() {
    getRawPage().atomicDelete(getRawPage(), getGeneratorCache());
  }
}
