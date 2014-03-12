package read_chain.web_wrapper;

import com.google.common.collect.ImmutableList;
import info_core_accessors.*;


// TODO: перейти на получение map, а не списков. Идея реаширения была избыточной.
public class ContainerNodeControllers {
  private final ImmutableList<ImmutableNodeAccessor> NODES;
  public ImmutableList<ImmutableList<ImmutableList<String>>> getPerWordData(Integer node) {
    ImmutableNodeAccessor accessor = NODES.asList().get(node);
    ListGetter getter = new ListGetterImpl(accessor);
    return getter.getPerWordData();
  }


  public ContainerNodeControllers(ImmutableList<ImmutableNodeAccessor> nodes) {
    // В try сделать нельзя - компилятор будет ругаться не неинициализованность
    NODES = nodes;  // Должна упасть если при ошибке дошла до сюда
  }
}

