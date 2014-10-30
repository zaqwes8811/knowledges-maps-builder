package servlets.research;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.python.google.common.base.Joiner;

import servlets.protocols.PathValue;

import com.google.common.base.Optional;
import com.google.gson.Gson;

// http://www.htmlgoodies.com/beyond/javascript/read-text-files-using-the-javascript-filereader.html#fbid=VhHKUeuMVFK
public class TextAccepter extends HttpServlet {
  private static final long serialVersionUID = -432680049858395942L;
  
  private class TextPackage {
  	TextPackage() {}
  	
  	String text;
  	String name;
  	
  	public Optional<String> getText() {
  		return Optional.fromNullable(text);
  	}
  	
  	public Optional<String> getName() {
  		return Optional.fromNullable(name);
  	}
  }

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
	    
	    String data = Joiner.on("").join(lines);
	    TextAccepter p = new Gson().fromJson(data, TextAccepter.class);
	    
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
