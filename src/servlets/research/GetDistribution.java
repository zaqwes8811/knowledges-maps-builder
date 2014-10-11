package servlets.research;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.googlecode.objectify.Key;

import servlets.FakeAppWrapper;
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
  
  @Override
  public void init(ServletConfig config) {
  	/*EasyKind k0 = new EasyKind();
  	EasyKind k1 = new EasyKind();
  	
  	// они не удаляются
  	ofy().save().entity(k1).now();
  	k0.k = Key.create(k1);
  	ofy().save().entity(k0).now();
  	
  	
  	List<EasyKind> l = ofy().load().type(EasyKind.class).list();
  	l.size();
  	
  	System.out.println(l);
  	*/
  	
  	// Own tables
  	
  	String activePageName = "Korra";
  	ContentPageKind p0 = new BuilderOneFakePage().buildContentPage(activePageName);
  	ofy().save().entity(p0).now();
  	
  	List<ContentPageKind> pages = 
  			ofy().load().type(ContentPageKind.class).list();
  	
  	System.out.println(pages);
  }

	@Override
  public void doGet(
    HttpServletRequest request,
    HttpServletResponse response) throws ServletException, IOException
  {
    /*String activePageName = "Korra";
    ContentPageKind loadedPage =
      ofy().load().type(ContentPageKind.class).filter("name = ", activePageName).first().now();
    
    ActiveDistributionGenKind gen = loadedPage.getGenerator();
    
    if (gen == null) {
    	throw new IllegalStateException();
    }*/

    String jsonResponse = "";//new Gson().toJson(gen.getDistribution());
    
    response.setContentType("text/html");
    response.setStatus(HttpServletResponse.SC_OK);
    
    response.setCharacterEncoding("UTF-8");
    response.getWriter().println(jsonResponse);
    
    List<Key<ContentPageKind>> keys = ofy().load().type(ContentPageKind.class).keys().list();
    ofy().delete().keys(keys);
  }
}
