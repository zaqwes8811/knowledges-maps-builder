package read_chain.web_wrapper;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.gson.Gson;

import info_core_accessors.FabricImmutableNodeAccessors;
import info_core_accessors.ImmutableNodeAccessor;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.checkthread.annotations.NotThreadSafe;
import through_functional.configurator.AppConfigurator;
import ui.UI;

import java.io.IOException;

@NotThreadSafe
public class Pkg extends HttpServlet {
  private Concentrator CONTAINER;
  @Override
  public void doGet(
  		HttpServletRequest request,
  		HttpServletResponse response) throws ServletException, IOException
  	{
  	
  	try {
  	if(null == CONTAINER) {
  		Wrapper wrapper = new Wrapper();

      String pathToCfgFile = "my.yaml";
      ImmutableSet<String> namesNodes = new AppConfigurator(pathToCfgFile).getRegisteredNodes().get();
      ImmutableList<ImmutableNodeAccessor> accessors = wrapper.getNodes(
          namesNodes, new FabricImmutableNodeAccessors());

      CONTAINER = new AppConcentrator(accessors);
  	}
  	} catch (Exception e) {
  		UI.showMessage(e);
  	}

    response.setContentType("text/html");
    response.setStatus(HttpServletResponse.SC_OK);

    //TODO(zaqwes): Сделать декоратор для класса
    String jsonResponse = new Gson().toJson(CONTAINER.getPackageActiveNode());

    response.setCharacterEncoding("UTF-8");
    response.getWriter().println(jsonResponse);
  }
}