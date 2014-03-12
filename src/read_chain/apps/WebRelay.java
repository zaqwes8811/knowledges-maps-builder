package read_chain.apps;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import common.Util;
import info_core_accessors.FabricImmutableNodeControllersImpl;
import info_core_accessors.ImmutableNodeAccessor;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import read_chain.web_wrapper.BuilderControllers;
import read_chain.web_wrapper.ContainerNodeControllers;
import through_functional.configurator.GlobalConfigurator;
import through_functional.configurator.ConfigurationFileIsCorrupted;
import through_functional.configurator.NoFoundConfigurationFile;

public class WebRelay {
  // About:
  //   For local Jetty
  public static HandlerList buildHandlers() {
    // Подключаем ресурсы
    ResourceHandler resourceHandler = new ResourceHandler();
    resourceHandler.setDirectoriesListed(true);
    resourceHandler.setWelcomeFiles(new String[]{ "index.html" });
    resourceHandler.setResourceBase("./web-pages");

    // Регистрируем обработчики
    ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
    context.setContextPath("/");
    // Коннектим все
    HandlerList handlers = new HandlerList();
    handlers.setHandlers(new Handler[] { resourceHandler, context });
    return handlers;
  }

  public static void main(String[] args) throws Exception {
    try {
      BuilderControllers wrapper = new BuilderControllers();

      String pathToCfgFile = "app.yaml";
      ImmutableSet<String> namesNodes = new GlobalConfigurator(pathToCfgFile).getRegisteredNodes().get();
      ImmutableList<ImmutableNodeAccessor> accessors = wrapper.createControllersForAllNodes(
          namesNodes, new FabricImmutableNodeControllersImpl());

      ContainerNodeControllers container = new ContainerNodeControllers(accessors);
      HandlerList handlers = buildHandlers();

      Server server = new Server(8081);
      server.setHandler(handlers);
      server.start();
      server.join();
    } catch (NoFoundConfigurationFile e) {
      Util.print(e);
      throw new RuntimeException();
    } catch (ConfigurationFileIsCorrupted e) {
      Util.print(e);
      throw new RuntimeException();
    }
  }
}
