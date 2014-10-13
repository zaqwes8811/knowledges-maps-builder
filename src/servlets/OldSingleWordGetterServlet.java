package servlets;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.gson.Gson;
import frozen.controllers.web_wrapper.BuilderControllers;
import frozen.controllers.web_wrapper.ContainerNodeControllers;
import frozen.crosscuttings.configurator.GlobalConfigurator;
import frozen.dal.accessors_text_file_storage.FabricImmutableNodeControllersImpl;
import frozen.dal.accessors_text_file_storage.ImmutableNodeAccessor;
import org.checkthread.annotations.NotThreadSafe;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Deprecated
@NotThreadSafe
public class OldSingleWordGetterServlet extends HttpServlet {
  private ContainerNodeControllers container_;
  private final String PATH_TO_CONFIGURATION_FILE_ = "own-app.yaml";
  
  @Override
  public void doGet(
  		HttpServletRequest request,
  		HttpServletResponse response) throws ServletException, IOException
  	{
  	
  	// TODO: syncronised
  	try {
      if (null == container_) {
        BuilderControllers builder = new BuilderControllers();
        ImmutableSet<String> namesNodes =
            new GlobalConfigurator(PATH_TO_CONFIGURATION_FILE_).getRegisteredNodes().get();
        
        ImmutableList<ImmutableNodeAccessor> controllers = 
        		builder.createControllersForAllNodes(
        				namesNodes, new FabricImmutableNodeControllersImpl());

        container_ = new ContainerNodeControllers(controllers);
      }
  	} catch (Exception e) {
      // Вот проблемы создают эти проверяемые исключения
  	}

    response.setContentType("text/html");
    response.setStatus(HttpServletResponse.SC_OK);

    // TODO: Сделать декоратор для класса
    // TODO: Это хардкод!
    Integer idxNode = 0;
    String jsonResponse = new Gson().toJson(idxNode);

    response.setCharacterEncoding("UTF-8");
    response.getWriter().println(jsonResponse);
  }
}