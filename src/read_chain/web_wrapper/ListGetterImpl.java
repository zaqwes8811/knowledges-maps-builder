package read_chain.web_wrapper;

import com.google.common.collect.ImmutableList;
import common.math.GeneratorAnyRandom;
import info_core_accessors.*;

import java.util.*;


// TODO: перейти на получение map, а не списков. Идея реаширения была избыточной.
@Deprecated
public class ListGetterImpl implements ListGetter {
  private final ImmutableList<ImmutableNodeAccessor> NODES;
  private static class OneNodePacker implements ListGetter {
    private final ImmutableNodeAccessor ACCESSOR_;
    private final GeneratorAnyRandom GENERATOR_;
    public OneNodePacker(ImmutableNodeAccessor accessor) {
      ACCESSOR_ = accessor;
      List<Integer> distribution = ACCESSOR_.getDistribution();
      GENERATOR_ = GeneratorAnyRandom.create(distribution);
    }

    // Index: нужно для маркеровки
    // Word: само слово
    // Translates:
    // Context:
    @Override
    public ImmutableList<ImmutableList<ImmutableList<String>>> getPerWordData() {
      List<String> rawKeys = new ArrayList<String>();
      List<ImmutableList<String>> values = new ArrayList<ImmutableList<String>>();

      // Отправляем только если есть контекст
      while (true) {
        boolean finded = false;
        Integer currentKey = GENERATOR_.getCodeWord();

        // Добавляем, только если что-то есть
        ImmutableList<String> content = ACCESSOR_.getContent(currentKey);
        if (!content.isEmpty()) {
          rawKeys.add("content");
          values.add(content);
          finded = true;
        }

        if (finded) {
          ImmutableList<String> translate = ImmutableList.of();
          if (!translate.isEmpty()) {
            rawKeys.add("translate");
            values.add(translate);
          }

          // Обязательно!
          rawKeys.add("word");
          values.add(ImmutableList.of(ACCESSOR_.getWord(currentKey)));
          break;
        }
      }
      List<ImmutableList<String>> keys = new ArrayList<ImmutableList<String>>();
      keys.add(ImmutableList.copyOf(rawKeys));
      return ImmutableList.of(ImmutableList.copyOf(keys), ImmutableList.copyOf(values));
    }
  }

  @Override
  public ImmutableList<ImmutableList<ImmutableList<String>>> getPerWordData() {
    ImmutableNodeAccessor accessor = NODES.asList().get(0);
    ListGetter getter = new OneNodePacker(accessor);
    return getter.getPerWordData();
  }


  public ListGetterImpl(ImmutableList<ImmutableNodeAccessor> nodes) {
    // В try сделать нельзя - компилятор будет ругаться не неинициализованность
    NODES = nodes;  // Должна упасть если при ошибке дошла до сюда
  }
}

