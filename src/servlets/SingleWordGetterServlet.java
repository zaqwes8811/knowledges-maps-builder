package servlets;

//import com.google.appengine.repackaged.org.apache.http.annotation.NotThreadSafe;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.jcip.annotations.NotThreadSafe;

import java.io.IOException;

@NotThreadSafe
public class SingleWordGetterServlet extends HttpServlet {
  private static final long serialVersionUID = -1906449812056035297L;
  
  private FakeAppWrapper app = FakeAppWrapper.getInstance(); 

	@Override
  public void doGet(
    HttpServletRequest request,
    HttpServletResponse response) throws ServletException, IOException
  {

    response.setContentType("text/html");
    response.setStatus(HttpServletResponse.SC_OK);

    // TODO: Сделать декоратор для класса
    // TODO: Это хардкод!
    Integer idxNode = 0;
    String jsonResponse = new Gson().toJson(idxNode);

    response.setCharacterEncoding("UTF-8");
    response.getWriter().println(jsonResponse);
  }
}