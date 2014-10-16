package servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.checkthread.annotations.NotThreadSafe;

import servlets.protocols.PathValue;

import com.google.gson.Gson;

@NotThreadSafe
public class SetterKnownIt extends HttpServlet {
  private static final long serialVersionUID = -409988761783328978L;

  @Override
  public void doPut(HttpServletRequest request, HttpServletResponse response) {
  	try {
	  	BufferedReader br = 
	  			new BufferedReader(new InputStreamReader(request.getInputStream()));
	
	  	String data = br.readLine();
	  	
	  	PathValue p = new Gson().fromJson(data, PathValue.class);
	  	
	  	System.out.println(p);
	  	
	    // name is null
  	} catch (IOException e) {
  		throw new IllegalStateException(e);
  	}
  }
}