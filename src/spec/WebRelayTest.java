package spec;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import frozen.controllers.web_wrapper.BuilderControllers;
import frozen.controllers.web_wrapper.ContainerNodeControllers;
import frozen.crosscuttings.configurator.ConfigurationFileIsCorrupted;
import frozen.crosscuttings.configurator.GlobalConfigurator;
import frozen.crosscuttings.configurator.NoFoundConfigurationFile;
import frozen.dal.accessors_text_file_storage.FabricImmutableNodeControllersImpl;
import frozen.dal.accessors_text_file_storage.ImmutableNodeAccessor;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class WebRelayTest {
  private static final LocalServiceTestHelper helper =
    new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

  @Before
  public void setUp() { helper.setUp(); }

  @After
  public void tearDown() {
    helper.tearDown();
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
  public void blockedTestCore() throws Exception {
    // FIXME: перенаправить логи, лог от хранилища не попадает в основной вывод.
    // FIXME: Сделать через конфигурационные файлы. Можно ли и нужно ли?
    System.out.println("Working Directory = " + System.getProperty("user.dir"));

    Server server = new Server();
    SelectChannelConnector connector = new SelectChannelConnector();
    connector.setPort(8080);
    server.addConnector(connector);
 
    // Подключаем корень?
    ResourceHandler resourceHandler = new ResourceHandler();
    resourceHandler.setDirectoriesListed(true);
    resourceHandler.setWelcomeFiles(new String[]{"index.html"});
    resourceHandler.setResourceBase("apps/views.views");  // что это-то?

    // Список обработчиков?
    // FIXME: Если не находи index.html Открывает вид папки!!
    HandlerList handlers = new HandlerList();
    ServletHandler handler = new ServletHandler();
    handlers.setHandlers(new Handler[] { resourceHandler, handler });

    // Подключаем к серверу
    server.setHandler(handlers);

    // Регистрируем сервлет?
    handler.addServletWithMapping("servlets.OldSingleWordGetterServlet", "/get_word_data");
///*
    server.start();
    server.join();//*/
  }

  @Test
  public void blockedTestServerOld() throws Exception {
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
      throw new RuntimeException();
    } catch (ConfigurationFileIsCorrupted e) {
      throw new RuntimeException();
    }
  }
}
