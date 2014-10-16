package servlets;

//import com.google.appengine.repackaged.org.apache.http.annotation.NotThreadSafe;
import com.google.gson.Gson;

import gae_store_space.OnePageBuilder;
import gae_store_space.ContentPageKind;
import gae_store_space.fakes.FakeAppWrapper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import servlets.protocols.WordDataValue;
import net.jcip.annotations.NotThreadSafe;

import java.io.IOException;

@NotThreadSafe
public class GetterSingleWord extends HttpServlet {
  private static final long serialVersionUID = -1906449812056035297L;
  
  private FakeAppWrapper app = FakeAppWrapper.getInstance(); 

	@Override
  public void doGet(
    HttpServletRequest request,
    HttpServletResponse response) throws ServletException, IOException
  {

    response.setContentType("text/html");
    response.setStatus(HttpServletResponse.SC_OK);
    
    ContentPageKind p = app.getPage(OnePageBuilder.defailtPageName);
    WordDataValue v = p.getWordData(OnePageBuilder.defaultGenName).get();
    String jsonResponse = new Gson().toJson(v);

    response.setCharacterEncoding("UTF-8");
    response.getWriter().println(jsonResponse);
  }
}