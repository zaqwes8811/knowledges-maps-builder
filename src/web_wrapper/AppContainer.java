package web_wrapper;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import com.google.gson.Gson;
import common.Util;
import idx_coursors.NodeIsCorrupted;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.servlet.ServletHandler;
import through_functional.configurator.AppConfigurator;
import through_functional.configurator.ConfFileIsCorrupted;
import through_functional.configurator.NoFoundConfFile;
import ui.UI;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class AppContainer {
  private AppContainer() {}
  //private static final Optional<ImmutableSet<String>> NODES;  // Программа должна отрубится если
    //   статическая часть не сработала
  private static final ImmutableSet<String> NODES;

  static public void closeApp() {
    // Выходим
    try {
      UI.showMessage("Press any key for out...");
      BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
      char c = (char)br.read();
      System.exit(0);

    } catch (IOException ioe) {
      UI.showMessage("Console io error.");
      System.exit(0);
    }
  }

  static {
    Optional<ImmutableSet<String>> tmp = Optional.absent();
    try {
      tmp = AppConfigurator.getRegisteredNodes();

    // Выходим, сервер не должен стартовать
    } catch (NoFoundConfFile e) {
      UI.showMessage("Configuration file no found - "+e.getFileName());
      closeApp();
    } catch (ConfFileIsCorrupted e) {
      UI.showMessage(e.WHAT_HAPPENED);
      closeApp();
    }

    // В try сделать нельзя - компилятор будет ругаться не неинициализованность
    NODES = tmp.get();  // Должна упасть если при ошибке дошла до сюда
  }

  private static Server createServer() {
    Server server = new Server();

    SelectChannelConnector connector = new SelectChannelConnector();
    connector.setPort(8080);
    server.addConnector(connector);

    // Подключаем корень?
    ResourceHandler resourceHandler = new ResourceHandler();
    resourceHandler.setDirectoriesListed(true);
    resourceHandler.setWelcomeFiles(new String[]{ "index.html" });
    resourceHandler.setResourceBase("./web-pages");


    // Список обработчиков?
    HandlerList handlers = new HandlerList();
    ServletHandler handler = new ServletHandler();
    handler.addServletWithMapping("web_wrapper.AppContainer$Pkg", "/pkg");
    handlers.setHandlers(new Handler[] { resourceHandler, handler });
    // ! если не находи index.html Открывает вид папки!!

    // Подключаем к серверу
    server.setHandler(handlers);
    return server;
  }

  public static void main(String[] args) throws Exception {
    // Похоже основная инициализация будет в static, т.к.

    // TODO(zaqwes): Сделать через конфигурационные файлы. Можно ли и нужно ли?
    System.out.println("Root Directory = "+System.getProperty("user.dir"));

    /*Server server = createServer();

    // Регистрируем сервлет?
    server.start();
    server.join(); */
  }

  // @State
  //private

  // @Servlets
  public static class Pkg extends HttpServlet {
    // Используем идею REST - параметры GET запросе не передаются
    protected void doGet(
        HttpServletRequest request,
        HttpServletResponse response) throws ServletException, IOException {

      response.setContentType("text/html");
      response.setStatus(HttpServletResponse.SC_OK);

      String jsonResponse = new Gson().toJson(Getters.createFake().getPackage());

      response.setCharacterEncoding("UTF-8");
      response.getWriter().println(jsonResponse);
    }
  }
}