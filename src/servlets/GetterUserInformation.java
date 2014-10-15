package servlets;

import gae_store_space.fakes.BuilderOneFakePage;
import gae_store_space.fakes.FakeAppWrapper;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import servlets.protocols.PageSummaryValue;

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
    
    List<PageSummaryValue> v = app.getUserInformation(BuilderOneFakePage.defaultUserId);
    String jsonResponse = new Gson().toJson(v);

    response.setCharacterEncoding("UTF-8");
    response.getWriter().println(jsonResponse);
  }
}
