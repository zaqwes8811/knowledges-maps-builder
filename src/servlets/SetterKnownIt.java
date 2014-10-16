package servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.checkthread.annotations.NotThreadSafe;

import com.google.gson.Gson;

@NotThreadSafe
public class SetterKnownIt extends HttpServlet {
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
  
  // FIXME: Update
  @Override
  public void doPut(HttpServletRequest request, HttpServletResponse response) {
  	try {
	  	BufferedReader br = 
	  			new BufferedReader(new InputStreamReader(request.getInputStream()));
	
	  	String data = br.readLine();
	  	
	  	System.out.println(data);
	  	
	    // name is null
  	} catch (IOException e) {
  		throw new IllegalStateException(e);
  	}
  }
}