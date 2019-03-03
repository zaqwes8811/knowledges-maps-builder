package http_api;

import backend.*;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import backend.KindPage;
import org.apache.commons.collections4.Predicate;
import org.apache.log4j.Logger;
import org.javatuples.Pair;
import backend.ContentItem;
import backend.TextPipeline;
import backend.UniGram;
import backend.math.DistributionElement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

public class PageWrapperBounded implements PageWrapper {
  private PageWrapperBounded() {}
  // Frontend
  // Формированием не управляет, но остальным управляет.
  // Обязательно отсортировано
  private ArrayList<UniGram> unigramKinds = new ArrayList<>();

  // Хранить строго как в исходном контексте
  private ArrayList<ContentItem> sentencesKinds = new ArrayList<>();
  private KindGenerator generatorCache;
  private KindPage kind = null;

  // по столько будем шагать
  // По малу шагать плохо тем что распределение может снова стать равном.
  // 10 * 20 = 200 слов, почти уникальных, лучше меньше
  public static final Integer STEP_WINDOW_SIZE = 8;
  private static final Double SWITCH_THRESHOLD = 0.2;

  // Actions
  private static Logger log = Logger.getLogger(UserKind.class.getName());

  public static PageWrapperBounded buildEmpty() {
    return new PageWrapperBounded();
  }

  public void setGeneratorCache(KindGenerator generatorCache) {
    this.generatorCache = generatorCache;
  }

  @Override
  public KindPage getRawPage() {
    if (kind == null)
      throw new AssertionError();

    return kind;
  }

  public void set(KindPage k) {
    if (kind != null)
      throw new AssertionError();

    kind = k;
  }

  @Override
  public ArrayList<Integer> getLengthsSentences() {
    ArrayList<Integer> r = new ArrayList<>();
    for (ContentItem k : sentencesKinds)
      r.add(k.getCountWords());
    return r;
  }

  private KindGenerator getGeneratorCache() {
    if (generatorCache == null)
      throw new IllegalStateException();
    return generatorCache;
  }

  public void assign(PageWrapperBounded rhs) {
    unigramKinds = rhs.unigramKinds;
    sentencesKinds = rhs.sentencesKinds;
  }

  // Это при создании с нуля
  public PageWrapperBounded(
    String pageName,
    String rawSource,
    ArrayList<ContentItem> items,
    ArrayList<UniGram> words)
  {
    this.unigramKinds = words;
    this.sentencesKinds = items;
    this.kind = new KindPage(pageName, rawSource);
  }

  private Set<String> getNGramms(Integer boundary) {
    Integer end = sentencesKinds.size();

    if (sentencesKinds.size() > boundary)
      end = boundary;

    // [.., end)
    ArrayList<ContentItem> kinds = new ArrayList<>(sentencesKinds.subList(0, end));

    return new TextPipeline().getNGrams(kinds);
  }

  private Integer getUnigramIndex(String ngram) {
    // FIXME: How hide it?
    class Tmp implements Predicate<UniGram> {
      @Override
      public boolean evaluate(UniGram o) {
        return o.getValue().equals(ngram);
      }

      String ngram;
      public Tmp(String value) {
        ngram = value;
      }
    }

    Tmp p = new Tmp(ngram);

    Pair<UniGram, Integer> k = OwnCollections.find(unigramKinds, p);
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
    KindGenerator gen = getGeneratorCache();
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
    Collections.sort(unigramKinds, UniGram.createImportanceComparator());
    Collections.reverse(unigramKinds);

    // Form result
    ArrayList<DistributionElement> r = new ArrayList<>();
    for (UniGram word : unigramKinds)
      r.add(new DistributionElement(word.getImportance()));

    r = applyBoundary(r);

    // генератора еще нету
    return r;
  }

  private ArrayList<DistributionElement> applyBoundary(ArrayList<DistributionElement> d) {
    checkDistributionInvariant(d);

    // Get word befor boundary
    Set<String> ngramms = getNGramms(getRawPage().getBoundaryPtr());

    for (String ngram: ngramms) {
      Integer index = getUnigramIndex(ngram);
      checkAccessIndex(index);

      // Проверка! Тестов как таковых нет, так что пока так
      assert unigramKinds.get(index).getValue().equals(ngram);

      d.get(index).markInBoundary();
    }
    return d;
  }

  private Integer getMaxImportancy() {
    return getGeneratorCache().getMaxImportance();
  }

  @Override
  public Optional<ValueWordData> getWordData() {
    KindGenerator go = getGeneratorCache();  // FIXME: нужно нормально обработать

    Integer pointPosition = go.getPosition();
    UniGram ngram =  getNGram(pointPosition);
    String value = ngram.getValue();
    ImmutableList<ContentItem> contentItems = ngram.getContendKinds();

    ArrayList<String> content = new ArrayList<>();
    for (ContentItem k: contentItems)
      content.add(k.getSentence());

    ValueWordData r = new ValueWordData(value, content, pointPosition);

    //
    r.setImportance(ngram.getImportance());
    // max import
    r.setMaxImportance(getMaxImportancy());

    return Optional.of(r);
  }

  // FIXME: а логика разрешает Отсутствующее значение?
  // http://stackoverflow.com/questions/2758224/assertion-in-java
  // генераторы могут быть разными, но набор слов один.
  private UniGram getNGram(Integer pos) {
    checkAccessIndex(pos);
    return unigramKinds.get(pos);
  }

  private void IncBoundary() {
    getRawPage().setBoundaryPtr(STEP_WINDOW_SIZE + getRawPage().getBoundaryPtr());
    if (getRawPage().getBoundaryPtr() > sentencesKinds.size())
      getRawPage().setBoundaryPtr(sentencesKinds.size());
  }

  private Integer getAmongCurrentActivePoints() {
    return getGeneratorCache().getActiveCount();
  }

  private void setVolume(Integer val) {
    getRawPage().setReferenceVolume(val);
  }

  private void setDistribution(ArrayList<DistributionElement> d) {
    checkDistributionInvariant(d);
    getGeneratorCache().resetDistribution(d);
  }

  private void moveBoundary() {
    Integer currentVolume = getAmongCurrentActivePoints();

    if (currentVolume < SWITCH_THRESHOLD * getRawPage().getReferenceVolume()) {
      // FIXME: no exception safe
      // перезагружаем генератор
      Integer currBoundary = getRawPage().getBoundaryPtr();
      IncBoundary();

      if (getAmongCurrentActivePoints() < 2)
        IncBoundary();  // пока один раз

      // подошли к концу
      if (!currBoundary.equals(getRawPage().getBoundaryPtr())) {
        ArrayList<DistributionElement> d = getImportanceDistribution();
        d = applyBoundary(d);

        setDistribution(d);
        setVolume(getAmongCurrentActivePoints());
      }
    }
  }

  @Override
  public void disablePoint(ValuePath p) {
    Integer pos = p.pointPos;
    // лучше здесь
    if (getRawPage().getReferenceVolume().equals(0)) {
      if (getAmongCurrentActivePoints() < 2)
        throw new IllegalStateException();

      // создаем первый объем
      setVolume(getAmongCurrentActivePoints());
    }
    moveBoundary();

    // Читаем заново
    KindGenerator g = getGeneratorCache();
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
