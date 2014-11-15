package web_relays;

//import com.google.appengine.repackaged.org.apache.http.annotation.NotThreadSafe;
import gae_store_space.AppInstance;
import gae_store_space.PageKind;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;

import net.jcip.annotations.NotThreadSafe;
import web_relays.protocols.PathValue;
import web_relays.protocols.WordDataValue;

import com.google.common.base.Optional;

@NotThreadSafe
public class GetterSingleWord extends HttpServlet {
  private static final long serialVersionUID = -1906449812056035297L;
  
  private AppInstance app = AppInstance.getInstance(); 

	@Override
  public void doGet(
    HttpServletRequest request,
    HttpServletResponse response) throws ServletException, IOException
  {

		String value = request.getParameter("arg0");
		
		if (value == null) 
			throw new IllegalArgumentException();
		
		PathValue path = new ObjectMapper().readValue(value, PathValue.class);
		    //new Gson().fromJson(value, );
		
    // path
		response.setContentType("text/html");
    response.setStatus(HttpServletResponse.SC_OK);
    
    Optional<PageKind> p = app.getPage(path.getPageName().get());
    if (!p.isPresent())
    	throw new IllegalStateException();
    
    WordDataValue v = p.get().getWordData().get();
    
    String json = new ObjectMapper().writeValueAsString(v);
 
    response.setCharacterEncoding("UTF-8");
    response.getWriter().println(json);
  }
}