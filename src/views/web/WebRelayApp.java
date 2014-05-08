package views.web;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import common.Util;
import dal.accessors_text_file_storage.FabricImmutableNodeControllersImpl;
import dal.accessors_text_file_storage.ImmutableNodeAccessor;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import controllers.web_wrapper.BuilderControllers;
import controllers.web_wrapper.ContainerNodeControllers;
import crosscuttings.configurator.GlobalConfigurator;
import crosscuttings.configurator.ConfigurationFileIsCorrupted;
import crosscuttings.configurator.NoFoundConfigurationFile;

public class WebRelayApp {
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
