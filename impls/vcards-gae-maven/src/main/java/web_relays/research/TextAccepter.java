package web_relays.research;

import cross_cuttings_layer.GlobalIO;
import gae_store_space.AppInstance;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import web_relays.protocols.TextPackage;

// http://www.htmlgoodies.com/beyond/javascript/read-text-files-using-the-javascript-filereader.html#fbid=VhHKUeuMVFK
public class TextAccepter extends HttpServlet {
  private static final long serialVersionUID = -432680049858395942L;
  
  private AppInstance app = AppInstance.getInstance();

  @Override
  public void doPost(
    HttpServletRequest request,
    HttpServletResponse response) throws ServletException, IOException
  {
  	response.setContentType("text/html");
		try {
	  	BufferedReader reader = 
	  			new BufferedReader(new InputStreamReader(request.getInputStream()));
	
	  	ArrayList<String> lines = new ArrayList<String>();
	  	String line;
	    while ((line = reader.readLine()) != null) {
	    	lines.add(line);
	    }

	    String data = Joiner.on("").join(lines);
	    TextPackage p = new ObjectMapper().readValue(data, TextPackage.class);
	    
	    // purge from headers and bottoms
	    if (p.getText().isPresent() && p.getName().isPresent()) {
	    	app.createOrRecreatePage(p.getName().get(), p.getText().get());

		  	// response
		    response.setStatus(HttpServletResponse.SC_OK); 
	    } else { 
	    	throw new IllegalArgumentException();
	    }
  	} catch (IOException e) {
	    response.setStatus(HttpServletResponse.SC_NO_CONTENT);
  	//} catch (IllegalStateException e) {
  		//response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
  	//} catch (IllegalArgumentException e) {
	  //  response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
  	}
  }
}
