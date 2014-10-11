package servlets.research;

import com.google.appengine.labs.repackaged.com.google.common.base.Optional;
import com.google.gson.Gson;

import servlets.FakeAppWrapper;
import store_gae_stuff.ActiveDistributionGenKind;
import store_gae_stuff.ContentPageKind;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

import static store_gae_stuff.OfyService.ofy;

public class GetDistribution extends HttpServlet {
  /**
	 * 
	 */
  private static final long serialVersionUID = 4122657047047348423L;
  static private FakeAppWrapper w; 
  static {
  	w = new FakeAppWrapper();
  }

	@Override
  public void doGet(
    HttpServletRequest request,
    HttpServletResponse response) throws ServletException, IOException
  {
    String activePageName = "Korra";
    ContentPageKind loadedPage =
      ofy().load().type(ContentPageKind.class).filter("name = ", activePageName).first().now();
    
    ActiveDistributionGenKind gen = loadedPage.getGenerator();
    
    if (gen == null) {
    	throw new IllegalStateException();
    }

    String jsonResponse = "";//new Gson().toJson(gen.getDistribution());
    
    response.setContentType("text/html");
    response.setStatus(HttpServletResponse.SC_OK);
    
    response.setCharacterEncoding("UTF-8");
    response.getWriter().println(jsonResponse);
  }
}
