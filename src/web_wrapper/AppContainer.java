package web_wrapper;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.gson.Gson;
import common.Util;
import common.math.GeneratorAnyRandom;
import idx_coursors.IdxNodeAccessor;
import idx_coursors.ImmutableNodeMeansOfAccess;
import idx_coursors.NodeIsCorrupted;
import idx_coursors.NodeNoFound;
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
import java.util.*;


public class AppContainer {
  private AppContainer() {}
  private static final ImmutableList<ImmutableNodeMeansOfAccess> NODES;
  private static final ImmutableNodeMeansOfAccess ACTIVE_NODE_ACCESSOR;
  private static final GeneratorAnyRandom GENERATOR;

  // @Fake
  static public Integer getKey() {
    return GENERATOR.getCodeWord();
  }

  // Index: нужно для маркеровки
  // Word: само слово
  // Translates:
  // Context:
  //
  // Повторяемосеть конечно не учитывается.
  static public ImmutableList<Map<String, ImmutableList<String>>> getPackage() {
    List<Map<String, ImmutableList<String>>> fullPkg = new ArrayList<Map<String, ImmutableList<String>>>();

    for (int i = 0; i < 12; ++i) {
      Map<String, ImmutableList<String>> pkg = new HashMap<String, ImmutableList<String>>();
      Integer currentKey = getKey();
      pkg.put("word", ImmutableList.of(ACTIVE_NODE_ACCESSOR.getWord(currentKey)));
      pkg.put("content", ACTIVE_NODE_ACCESSOR.getContent(currentKey));
      pkg.put("translate", ImmutableList.of("No records"));
      fullPkg.add(pkg);
    }
    return ImmutableList.copyOf(fullPkg);
  }

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
    Optional<ImmutableList<ImmutableNodeMeansOfAccess>> nodes = Optional.absent();
    try {
      Optional<ImmutableSet<String>> namesNodes = AppConfigurator.getRegisteredNodes();

      // Загружает данные узла
      List<ImmutableNodeMeansOfAccess> tmpNodes = new ArrayList<ImmutableNodeMeansOfAccess>();
      for (String node: namesNodes.get()) {
        try {
          tmpNodes.add(IdxNodeAccessor.createImmutableConnection(node));
        } catch (NodeIsCorrupted e) {
          UI.showMessage("Node is corrupted - "+node);
        } catch (NodeNoFound e) {
          UI.showMessage("Node no found - "+node);
        }
      }
      nodes = Optional.of(ImmutableList.copyOf(tmpNodes));

    // Выходим, сервер не должен стартовать
    } catch (NoFoundConfFile e) {
      UI.showMessage("Configuration file no found - "+e.getFileName());
      closeApp();
    } catch (ConfFileIsCorrupted e) {
      UI.showMessage(e.WHAT_HAPPENED);
      closeApp();
    }

    // В try сделать нельзя - компилятор будет ругаться не неинициализованность
    NODES = nodes.get();  // Должна упасть если при ошибке дошла до сюда
    ACTIVE_NODE_ACCESSOR = NODES.asList().get(0);

    // Коннектим генератор случайных чисел и акссессор
    List<Integer> distribution = ACTIVE_NODE_ACCESSOR.getDistribution();
    GENERATOR = GeneratorAnyRandom.create(distribution);
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
    //System.out.println("Root Directory = "+System.getProperty("user.dir"));

    ///*
    Server server = createServer();

    // Регистрируем сервлет?
    server.start();
    server.join(); //*/
  }

  // @State
  //private

  // @Servlets
  public static class Pkg extends HttpServlet {
    // Используем идею REST - параметры GET запросе не передаются
    @Override
    protected void doGet(
        HttpServletRequest request,
        HttpServletResponse response) throws ServletException, IOException {

      response.setContentType("text/html");
      response.setStatus(HttpServletResponse.SC_OK);

      //TODO(zaqwes): Сделать адаптер для класса
      //String jsonResponse = new Gson().toJson(Getters.createFake().getPackage());
      String jsonResponse = new Gson().toJson(AppContainer.getPackage().get(0));

      response.setCharacterEncoding("UTF-8");
      response.getWriter().println(jsonResponse);
    }
  }
}