package servlets.research;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
  static private FakeAppWrapper w; 
  static {
  	w = new FakeAppWrapper();
  }
  
  static final String activePageName = "Korra";
  
  @Override
  public void init(ServletConfig config) {	
  	List<Key<ContentPageKind>> keys = ofy().load().type(ContentPageKind.class).keys().list();
  	ofy().delete().keys(keys).now();
  	List<Key<ActiveDistributionGenKind>> keys_gen = ofy().load().type(ActiveDistributionGenKind.class).keys().list();
  	ofy().delete().keys(keys_gen).now();
  	
  	// Own tables
  	ContentPageKind p0 = new BuilderOneFakePage().buildContentPage(activePageName);
  	ofy().save().entity(p0).now();
  	ofy().save().entity(p0).now();
  	
  	List<ContentPageKind> pages = 
  			ofy().load().type(ContentPageKind.class).list();
  	
  	if (pages.size() != 1) {
  		throw new IllegalStateException();
  	}
  	
  	// add generator
  	ActiveDistributionGenKind g = ActiveDistributionGenKind.create(p0.getRawDistribution());
  	ofy().save().entity(g).now();
  	p0.setGenerator(g);
  	ofy().save().entity(p0).now();  
  	
  	// may work
  }

	@Override
  public void doGet(
    HttpServletRequest request,
    HttpServletResponse response) throws ServletException, IOException
  {
    try {
      List<ContentPageKind> pages = 
      		ofy().load().type(ContentPageKind.class).filter("name = ", activePageName).list();
      
      if (pages.size() != 1) {
    		throw new IllegalStateException();
    	}
    
	    ActiveDistributionGenKind gen = pages.get(0).getGenerator();
	    
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
