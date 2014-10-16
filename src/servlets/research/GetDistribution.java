package servlets.research;

import gae_store_space.ActiveDistributionGenKind;
import gae_store_space.OnePageProcessor;
import gae_store_space.ContentPageKind;
import gae_store_space.fakes.FakeAppWrapper;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import sand.third_party_tests.gae.EasyKind;

import com.google.common.base.Optional;
import com.google.gson.Gson;
import com.googlecode.objectify.Key;

import static gae_store_space.OfyService.ofy;


// CASE: на вебе есть список генераторов страницы, берем имя генератора, и для него возврящаем 
//   распределение. Если генератор не найден - данные на вебе устарели.
public class GetDistribution extends HttpServlet {
  private static final long serialVersionUID = 4122657047047348423L;
  
  private FakeAppWrapper app = FakeAppWrapper.getInstance(); 
  
	@Override
  public void doGet(
    HttpServletRequest request,
    HttpServletResponse response) throws ServletException, IOException
  {
		// Срабатывает только один раз
		// TODO: Генератора реально может и не быть, или не найтись. Тогда лучше вернуть не ноль, а что-то другое 
		ContentPageKind page = app.getPage(FakeAppWrapper.defaultPageName);  // FIXME: страница тоже может быть не найдена
  	ActiveDistributionGenKind gen = page.getGenerator(FakeAppWrapper.defaultGenName);
  	
  	String r = "";
  	response.setContentType("text/html");

		r = new Gson().toJson(gen.getDistribution());
		response.setStatus(HttpServletResponse.SC_OK);

    response.setCharacterEncoding("UTF-8");
    response.getWriter().println(r);
  }
}
