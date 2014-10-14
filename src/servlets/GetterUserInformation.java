package servlets;

import gae_store_space.ContentPageKind;
import gae_store_space.ContentPageKind.WordDataValue;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import store_gae_stuff.fakes.BuilderOneFakePage;
import store_gae_stuff.fakes.FakeAppWrapper;

import com.google.gson.Gson;

// 
public class GetterUserInformation extends HttpServlet {
	/**
	 * 
	 */
  private static final long serialVersionUID = 5249671566813631715L;
  
	private FakeAppWrapper app = FakeAppWrapper.getInstance(); 

	@Override
  public void doGet(
    HttpServletRequest request,
    HttpServletResponse response) throws ServletException, IOException
  {

    response.setContentType("text/html");
    response.setStatus(HttpServletResponse.SC_OK);
    
    //ContentPageKind p = app.getPage(BuilderOneFakePage.defailtPageName);
    //WordDataValue v = p.getWordData(BuilderOneFakePage.defaultGenName).get();
    String jsonResponse = "";//new Gson().toJson(v);

    response.setCharacterEncoding("UTF-8");
    response.getWriter().println(jsonResponse);
  }
}
