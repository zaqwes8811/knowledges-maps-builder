package servlets.research;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TextAccepter extends HttpServlet {
  private static final long serialVersionUID = -432680049858395942L;

  @Override
  public void doPost(
    HttpServletRequest request,
    HttpServletResponse response) throws ServletException, IOException
  {

		try {
	  	BufferedReader reader = 
	  			new BufferedReader(new InputStreamReader(request.getInputStream()));
	
	  	ArrayList<String> lines = new ArrayList<String>();
	  	String line;
	    while ((line = reader.readLine()) != null) {
	    	lines.add(line);
	    }
	    
	    // purge from headers and bottoms
	    //process(lines);
	  	
	  	// response
	  	response.setContentType("text/html");
	    response.setStatus(HttpServletResponse.SC_OK); 
  	} catch (IOException e) {
  		throw new IllegalStateException(e);
  	}
  }
}
