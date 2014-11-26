package gae_store_space;


import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.VoidWork;
import com.googlecode.objectify.Work;
import com.googlecode.objectify.annotation.Ignore;
import cross_cuttings_layer.AssertException;
import cross_cuttings_layer.OwnCollections;
import instances.AppInstance;
import org.apache.log4j.Logger;
import org.javatuples.Pair;
import pipeline.TextPipeline;
import pipeline.math.DistributionElement;
import web_relays.protocols.PathValue;
import web_relays.protocols.WordDataValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static gae_store_space.OfyService.ofy;

public class PageFrontend {
  private PageFrontend() {}
  // Frontend
  private static Logger log = Logger.getLogger(UserKind.class.getName());
  // Формированием не управляет, но остальным управляет.
  // Обязательно отсортировано
  //@Ignore
  private ArrayList<NGramKind> unigramKinds = new ArrayList<>();

  // Хранить строго как в исходном контексте
  //@Ignore
  private ArrayList<SentenceKind> sentencesKinds = new ArrayList<SentenceKind>();
  //@Ignore
  private GeneratorKind generatorCache;

  // по столько будем шагать
  // По малу шагать плохо тем что распределение может снова стать равном.
  // 10 * 20 = 200 слов, почти уникальных, лучше меньше
  //@Ignore
  //private
  static final Integer STEP_WINDOW_SIZE = 8;
  //@Ignore
  private static final Double SWITCH_THRESHOLD = 0.2;
  private PageKind get() {
    return Optional.fromNullable(kind).get();
  }

  private void set(PageKind k) {
    if (kind != null)
      throw new AssertionError();

    kind = k;
  }

  private PageKind kind = null;

  //@Ignore
  GAEStoreAccessManager store = new GAEStoreAccessManager();

  //@Ignore
  private static TextPipeline buildPipeline() {
    return new TextPipeline();
  }

  public ArrayList<Integer> getLengthsSentences() {
    ArrayList<Integer> r = new ArrayList<>();
    for (SentenceKind k : sentencesKinds)
      r.add(k.getCountWords());
    return r;
  }

  private static void checkNonEmpty(Set<Key<PageKind>> keys) {
    if (keys.isEmpty())
      throw new AssertionError();
  }

  // Транзакцией сделать нельзя - поиск это сразу больше 5 EG
  // Да кажется можно, просто не ясно зачем
  // DANGER: если не удача всегда! кидается исключение, это не дает загрузиться кешу!
  public static Optional<PageFrontend> restore(String pageName, Set<Key<PageKind>> keys) {
    checkNonEmpty(keys);
    try {
      Optional<PageKind> page = PageKind.getPageKind(pageName, keys);
      Optional<PageFrontend> r = Optional.of(new PageFrontend());

      // Страница восстановлена
      if (page.isPresent()) {
        PageKind p = page.get();
        PageFrontend tmp = buildPipeline().pass(p.name, p.rawSource);
        r.get().assign(tmp);
        r.get().generatorCache = GeneratorKind.restoreById(p.generator.getId()).get();
      }
      return r;
    } catch (StoreIsCorruptedException ex) {
      throw new RuntimeException(ex.getCause());
    }
  }

  private GeneratorKind getGeneratorCache() {
    if (!Optional.fromNullable(generatorCache).isPresent())
      throw new IllegalStateException();
    return generatorCache;
  }

  private void assign(PageFrontend rhs) {
    unigramKinds = rhs.unigramKinds;
    sentencesKinds = rhs.sentencesKinds;
  }

  public List<String> getGenNames() {
    ArrayList<String> r = new ArrayList<>();
    r.add(AppInstance.defaultGeneratorName);
    return r;
  }

  // Это при создании с нуля
  public PageFrontend(
    String pageName, ArrayList<SentenceKind> items, ArrayList<NGramKind> words, String rawSource)
  {
    this.unigramKinds = words;
    this.sentencesKinds = items;

    this.kind = new PageKind(pageName, rawSource);
  }

  public static Pair<PageKind, GeneratorKind> process(String name, String text) {
    // check-then-act/read-modify-write
    // FIXME: Looks like imposable without races.
    // Doesn't help really
    if (text.length() > GAEStoreAccessManager.LIMIT_DATA_STORE_SIZE)
      throw new IllegalArgumentException();

    // local work
    // FIXME: need processing but only for fill generator!
    TextPipeline processor = new TextPipeline();
    PageFrontend page = processor.pass(name, text);

    // FIXME: how to know object size - need todo it!
    if (page.get().getPageByteSize() > GAEStoreAccessManager.LIMIT_DATA_STORE_SIZE)
      throw new IllegalArgumentException();

    ArrayList<DistributionElement> d = page.buildSourceImportanceDistribution();

    GeneratorKind g = GeneratorKind.create(d);
    return Pair.with(page.get(), g);
  }

  private Set<String> getNGramms(Integer boundary) {
    Integer end = sentencesKinds.size();

    if (sentencesKinds.size() > boundary)
      end = boundary;

    // [.., end)
    ArrayList<SentenceKind> kinds = new ArrayList<>(sentencesKinds.subList(0, end));

    return buildPipeline().getNGrams(kinds);
  }

  private Integer getUnigramIndex(String ngram) {
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

  public ArrayList<DistributionElement> getDistribution() {
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
  private ArrayList<DistributionElement> buildSourceImportanceDistribution() {
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
    Set<String> ngramms = getNGramms(get().boundaryPtr);

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
    return this.generatorCache.getMaxImportance();
  }

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
    get().boundaryPtr += STEP_WINDOW_SIZE;
    if (get().boundaryPtr > sentencesKinds.size())
      get().boundaryPtr = sentencesKinds.size();
  }

  private Integer getAmongCurrentActivePoints() {
    return getGeneratorCache().getActiveCount();
  }

  private void setVolume(Integer val) {
    get().referenceVolume = val;
  }

  private void setDistribution(ArrayList<DistributionElement> d) {
    checkDistributionInvariant(d);
    getGeneratorCache().resetDistribution(d);
  }

  private void moveBoundary() {
    Integer currentVolume = getAmongCurrentActivePoints();

    if (currentVolume < SWITCH_THRESHOLD * get().referenceVolume) {
      // FIXME: no exception safe
      // перезагружаем генератор
      Integer currBoundary = get().boundaryPtr;
      IncBoundary();

      if (getAmongCurrentActivePoints() < 2)
        IncBoundary();  // пока один раз

      // подошли к концу
      if (!currBoundary.equals(get().boundaryPtr)) {
        ArrayList<DistributionElement> d = getDistribution();
        d = applyBoundary(d);

        setDistribution(d);
        setVolume(getAmongCurrentActivePoints());
      }
    }
  }

  public void disablePoint(PathValue p) {
    Integer pos = p.pointPos;
    // лучше здесь
    if (get().referenceVolume.equals(0)) {
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
    get().persist(get(), getGeneratorCache());
  }

  public void atomicDelete() {
    get().atomicDelete(get(), getGeneratorCache());
  }
}
