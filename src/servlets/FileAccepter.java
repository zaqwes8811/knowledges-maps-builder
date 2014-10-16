package servlets;

import gae_store_space.fakes.FakeAppWrapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.jcip.annotations.NotThreadSafe;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;

@NotThreadSafe
public class FileAccepter extends HttpServlet {
  private static final long serialVersionUID = -8709394805924640800L;
  
	private FakeAppWrapper app = FakeAppWrapper.getInstance(); 
	
	// FIXME: нужно выделить имя файла, иначе похоже файл не идентифицировать.
	private ImmutableList<String> process(ArrayList<String> in) {
		// http://stackoverflow.com/questions/24769832/uploaded-file-only-contains-webkitformboundary
		if (in.size() < 3)
			throw new IllegalArgumentException();
		
		// выделяем заголовок
		// name
		// filename
		
		// Кажется будет перевод строки после заголовка
		
		return ImmutableList.copyOf(in);
	}

	// http://commons.apache.org/proper/commons-fileupload/using.html
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
	    	System.out.println(line);
	    }
	    
	    // purge from headers and bottoms
	  	
	  	// response
	  	response.setContentType("text/html");
	    response.setStatus(HttpServletResponse.SC_OK); 
	    
	    String jsonResponse = new Gson().toJson(9);

	    response.setCharacterEncoding("UTF-8");
	    response.getWriter().println(jsonResponse);
  	} catch (IOException e) {
  		throw new IllegalStateException(e);
  	}
  }
}
