package web_wrapper;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import common.math.GeneratorAnyRandom;
import idx_coursors.*;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import through_functional.configurator.AppConfigurator;
import through_functional.configurator.ConfFileIsCorrupted;
import through_functional.configurator.NoFoundConfFile;

import java.util.*;


public class AppContainer {
  private final ImmutableList<ImmutableNodeAccessor> NODES;
  private final ImmutableNodeAccessor ACTIVE_NODE_ACCESSOR;
  private final GeneratorAnyRandom GENERATOR;

  // Index: нужно для маркеровки
  // Word: само слово
  // Translates:
  // Context:
  public ImmutableList<ImmutableList<ImmutableList<String>>> getPackageActiveNode() {

    List<String> rawKeys = new ArrayList<String>();
    List<ImmutableList<String>> values = new ArrayList<ImmutableList<String>>();
    Integer currentKey = GENERATOR.getCodeWord();

    // Добавляем, только если что-то есть
    ImmutableList<String> content = ACTIVE_NODE_ACCESSOR.getContent(currentKey);
    if (!content.isEmpty()) {
      rawKeys.add("content");
      values.add(content);
    }

    ImmutableList<String> translate = ImmutableList.of();
    if (!translate.isEmpty()) {
      rawKeys.add("translate");
      values.add(translate);
    }

    // Обазятельно!
    rawKeys.add("word");
    values.add(ImmutableList.of(ACTIVE_NODE_ACCESSOR.getWord(currentKey)));

    List<ImmutableList<String>> keys = new ArrayList<ImmutableList<String>>();
    keys.add(ImmutableList.copyOf(rawKeys));

    return ImmutableList.of(ImmutableList.copyOf(keys), ImmutableList.copyOf(values));
  }

  public AppContainer(ImmutableList<ImmutableNodeAccessor> nodes) {
    // В try сделать нельзя - компилятор будет ругаться не неинициализованность
    NODES = nodes;  // Должна упасть если при ошибке дошла до сюда
    ACTIVE_NODE_ACCESSOR = NODES.asList().get(0);

    // Коннектим генератор случайных чисел и акссессор
    List<Integer> distribution = ACTIVE_NODE_ACCESSOR.getDistribution();
    GENERATOR = GeneratorAnyRandom.create(distribution);
  }
}

