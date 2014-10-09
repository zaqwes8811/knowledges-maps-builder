package frozen.controllers.web_wrapper;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import store_gae_kinds.GeneratorDistributionsImpl;
import frozen.dal.accessors_text_file_storage.ImmutableNodeAccessor;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public class ListGetterImpl implements ListGetter {
  private final ImmutableNodeAccessor ACCESSOR_;
  private final Optional<GeneratorDistributionsImpl> GENERATOR_;
  public ListGetterImpl(ImmutableNodeAccessor accessor) {
    ACCESSOR_ = accessor;
    ArrayList<Integer> distribution = ACCESSOR_.getDistribution();
    GENERATOR_ = Optional.absent();//GeneratorDistributionsImpl.create(distribution);
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
      Integer currentKey = GENERATOR_.get().getPosition();

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