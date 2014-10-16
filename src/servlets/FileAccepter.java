package servlets;

import gae_store_space.ContentPageKind;
import gae_store_space.fakes.BuilderOneFakePage;
import gae_store_space.fakes.FakeAppWrapper;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.jcip.annotations.NotThreadSafe;
import servlets.protocols.WordDataValue;

import com.google.gson.Gson;

@NotThreadSafe
public class FileAccepter extends HttpServlet {
  private static final long serialVersionUID = -8709394805924640800L;
  
	private FakeAppWrapper app = FakeAppWrapper.getInstance(); 

	@Override
  public void doGet(
    HttpServletRequest request,
    HttpServletResponse response) throws ServletException, IOException
  {

    response.setContentType("text/html");
    response.setStatus(HttpServletResponse.SC_OK);
    
    String jsonResponse = new Gson().toJson(v);

    response.setCharacterEncoding("UTF-8");
    response.getWriter().println(jsonResponse);
  }
}
