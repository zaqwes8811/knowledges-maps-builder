package servlets.research;

import com.google.gson.Gson;
import store_gae_stuff.ActiveDistributionGenKind;
import store_gae_stuff.ContentPageKind;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static store_gae_stuff.OfyService.ofy;

public class GetDistribution extends HttpServlet {
  /**
	 * 
	 */
  private static final long serialVersionUID = 4122657047047348423L;

	@Override
  public void doGet(
    HttpServletRequest request,
    HttpServletResponse response) throws ServletException, IOException
  {
    response.setContentType("text/html");
    response.setStatus(HttpServletResponse.SC_OK);

    String activePageName = "Korra";
    ContentPageKind loadedPage =
      ofy().load().type(ContentPageKind.class).filter("name = ", activePageName).first().now();

    // подгружаем генератор
    List<ActiveDistributionGenKind> gen = loadedPage.getGenerators();
    assert !gen.isEmpty();

    String jsonResponse = new Gson().toJson(gen.get(0).getDistribution());

    response.setCharacterEncoding("UTF-8");
    response.getWriter().println(jsonResponse);
  }
}
