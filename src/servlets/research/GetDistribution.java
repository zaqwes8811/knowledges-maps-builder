package servlets.research;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.labs.repackaged.com.google.common.base.Optional;
import com.google.gson.Gson;
import com.googlecode.objectify.Key;

import servlets.FakeAppWrapper;
import store_gae_stuff.ActiveDistributionGenKind;
import store_gae_stuff.ContentPageKind;
import store_gae_stuff.EasyKind;
import store_gae_stuff.fakes.BuilderOneFakePage;
import static store_gae_stuff.OfyService.ofy;

public class GetDistribution extends HttpServlet {
  /**
	 * 
	 */
  private static final long serialVersionUID = 4122657047047348423L;
  
  private FakeAppWrapper w = FakeAppWrapper.getInstance(); 

  
	@Override
  public void doGet(
    HttpServletRequest request,
    HttpServletResponse response) throws ServletException, IOException
  {
    try {
    	String genName = "No";
    	
	    // TODO: Генератора реально может и не быть, или не найтись. Тогда лучше вернуть не ноль, а что-то другое 
    	ActiveDistributionGenKind gen = Optional.of(w.getPage(FakeAppWrapper.defaultPageName).getGenerator()).get();
	    
	    if (gen == null) {
	    	throw new IllegalStateException();
	    }
	
	    String jsonResponse = new Gson().toJson(gen.getDistribution());
	    
	    response.setContentType("text/html");
	    response.setStatus(HttpServletResponse.SC_OK);
	    
	    response.setCharacterEncoding("UTF-8");
	    response.getWriter().println(jsonResponse);
    } catch (Exception e) {
    	//e.rethrow();
    	throw e;
    } finally {
    	
    }
  }
}
