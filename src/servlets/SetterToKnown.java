package servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.checkthread.annotations.NotThreadSafe;

import com.google.gson.Gson;

@NotThreadSafe
public class SetterToKnown extends HttpServlet {
  private static final long serialVersionUID = -409988761783328978L;
  
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