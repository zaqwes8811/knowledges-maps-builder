package servlets;

import gae_store_space.AppInstance;
import gae_store_space.PageKind;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import servlets.protocols.PathValue;

import com.google.common.base.Optional;
import com.google.gson.Gson;

public class GetLengthSentences extends HttpServlet {

  private static final long serialVersionUID = 7822507282342481748L;
  
  private AppInstance app = AppInstance.getInstance(); 

	@Override
  public void doGet(
    HttpServletRequest request,
    HttpServletResponse response) throws ServletException, IOException
  {

		Optional<String> value = Optional.fromNullable(request.getParameter("arg0"));
		
		if (value.isPresent()) {
			PathValue path = new Gson().fromJson(value.get(), PathValue.class);
			Optional<String> pageName = path.getPageName();
			if (pageName.isPresent()) {
		    Optional<PageKind> p = app.getPage(path.getPageName().get());
		    if (p.isPresent()) {
			    ArrayList<Integer> v = p.get().getLengthsSentences();
			    
			    String json = new Gson().toJson(v);
			 
			    response.setContentType("text/html");
			    response.setStatus(HttpServletResponse.SC_OK);
			    response.setCharacterEncoding("UTF-8");
			    response.getWriter().println(json);
			    return;
		    }
	    }
    }
		// можно обработать какие-то ошибки
  }

}
