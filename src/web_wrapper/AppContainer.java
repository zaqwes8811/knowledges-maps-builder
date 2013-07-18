package web_wrapper;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import java.io.IOException;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.servlet.ServletHandler;

import com.google.gson.Gson;



public class AppContainer {

  public static void main(String[] args) throws Exception {

    // TODO(zaqwes): Сделать через конфигурационные файлы. Можно ли и нужно ли?
    System.out.println("Working Directory = "+System.getProperty("user.dir"));

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
    handlers.setHandlers(new Handler[] { resourceHandler, handler });
      // ! если не находи index.html Открывает вид папки!!
    
    // Подключаем к серверу
    server.setHandler(handlers);

    // Регистрируем сервлет?
    handler.addServletWithMapping("web_wrapper.AppContainer$Pkg", "/pkg");
    server.start();
    server.join();
  }

  // Переменная здесь видна из сервлета?
  // YES. private static String hellos;

  public static class Pkg extends HttpServlet {

    // Используем идею REST - параметры GET запросе не передаются
    protected void doGet(
        HttpServletRequest request,
        HttpServletResponse response) throws ServletException, IOException {

      response.setContentType("text/html");
      response.setStatus(HttpServletResponse.SC_OK);

      response.setCharacterEncoding("UTF-8");
      response.getWriter().println("{}");//json_response);
    }
  }
}