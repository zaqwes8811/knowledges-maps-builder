import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
 
import java.io.IOException;
 
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.servlet.ServletHandler;

import com.google.gson.Gson;
import java.util.*;
import java.util.Random;

public class FileServer
{
    public static void main(String[] args) throws Exception
    {
        System.out.println("Working Directory = " +
                System.getProperty("user.dir"));


        Server server = new Server();
        SelectChannelConnector connector = new SelectChannelConnector();
        connector.setPort(8080);
        server.addConnector(connector);
 
        // Подключаем корень?
        ResourceHandler resource_handler = new ResourceHandler();
        resource_handler.setDirectoriesListed(true);
        resource_handler.setWelcomeFiles(new String[]{ "index.html" });
        resource_handler.setResourceBase(".");
 
        // Список обработчиков?
        HandlerList handlers = new HandlerList();
        ServletHandler handler = new ServletHandler();
        handlers.setHandlers(new Handler[] { resource_handler/*, new DefaultHandler()*/, handler });
        // ! если не находи index.html Открывает вид папки!!
        
        // Подключаем к серверу
        server.setHandler(handlers);
        
        handler.addServletWithMapping("FileServer$HelloServlet", "/test");
        handler.addServletWithMapping("FileServer$HelloServletAjax", "/ajax");
 
        server.start();
        server.join();
    }
    
    // Servlets
    
    public static class HelloServlet extends HttpServlet
    {
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
        {
            response.setContentType("text/html");
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().println("<h1>Hello SimpleServlet</h1>");
        }
   }
    
    public static class HelloServletAjax extends HttpServlet
    {
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
        {
            response.setContentType("text/html");
            response.setStatus(HttpServletResponse.SC_OK);
            
            Gson gson = new Gson();

            ArrayList<ArrayList<Float>> ints = new ArrayList<ArrayList<Float>>(10000);

    		Random randomGenerator = new Random();
    	    for (float idx = 0; idx < 700.0; idx += 0.1){
              ArrayList<Float> tmp = new  ArrayList<Float>();
              float randomInt = 0.5f;
              if (idx < 350.0) {
                  randomInt = 0.5f;
              } else {
                  randomInt = 0.2f;
              }
    	      randomInt += randomGenerator.nextFloat();
              tmp.add(idx);
              tmp.add(randomInt/10);
    	      ints.add(tmp);
    	    }
            //ints.add(new Float(0.0));
    		String json = gson.toJson(ints);
    		
            response.getWriter().println(json);
        }
   }
}