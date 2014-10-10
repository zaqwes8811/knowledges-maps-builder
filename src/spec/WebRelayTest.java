package spec;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import common.Tools;
import frozen.dal.accessors_text_file_storage.FabricImmutableNodeControllersImpl;
import frozen.dal.accessors_text_file_storage.ImmutableNodeAccessor;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHandler;

import frozen.controllers.web_wrapper.BuilderControllers;
import frozen.controllers.web_wrapper.ContainerNodeControllers;
import frozen.crosscuttings.configurator.GlobalConfigurator;
import frozen.crosscuttings.configurator.ConfigurationFileIsCorrupted;
import frozen.crosscuttings.configurator.NoFoundConfigurationFile;
import org.junit.Test;

public class WebRelayTest {
  //private static Logger log = Logger.getLogger(AppContainer.class);

  @Test
  public void testMain2() throws Exception {
    //BasicConfigurator.configure();

    // TODO(zaqwes): Сделать через конфигурационные файлы. Можно ли и нужно ли?
    System.out.println("Working Directory = " + System.getProperty("user.dir"));

    Server server = new Server();
    SelectChannelConnector connector = new SelectChannelConnector();
    connector.setPort(8080);
    server.addConnector(connector);
 
    // Подключаем корень?
    ResourceHandler resource_handler = new ResourceHandler();
    resource_handler.setDirectoriesListed(true);
    resource_handler.setWelcomeFiles(new String[]{ "index.html" });
    resource_handler.setResourceBase("apps/views.views");

    // Список обработчиков?
    HandlerList handlers = new HandlerList();
    ServletHandler handler = new ServletHandler();
    handlers.setHandlers(new Handler[] { resource_handler/*, new DefaultHandler()*/, handler });
    // ! если не находи index.html Открывает вид папки!!
    
    // Подключаем к серверу
    server.setHandler(handlers);

    // Регистрируем сервлет?
    handler.addServletWithMapping("AppContainer$App", "/app");
    server.start();
    server.join();
  }
  
  // About:
  //   For local Jetty
  public HandlerList buildHandlers() {
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

  @Test
  public void testMain() throws Exception {
    try {
      BuilderControllers wrapper = new BuilderControllers();

      String pathToCfgFile = "app.yaml";
      ImmutableSet<String> namesNodes = new GlobalConfigurator(pathToCfgFile).getRegisteredNodes().get();
      ImmutableList<ImmutableNodeAccessor> a = wrapper.createControllersForAllNodes(
          namesNodes, new FabricImmutableNodeControllersImpl());

      ContainerNodeControllers container = new ContainerNodeControllers(a);
      HandlerList handlers = buildHandlers();

      Server server = new Server(8081);
      server.setHandler(handlers);
      server.start();
      server.join();
    } catch (NoFoundConfigurationFile e) {
      Tools.print(e);
      throw new RuntimeException();
    } catch (ConfigurationFileIsCorrupted e) {
      Tools.print(e);
      throw new RuntimeException();
    }
  }
}
