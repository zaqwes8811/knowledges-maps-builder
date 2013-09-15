package web_wrapper;

import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

class Pkg extends HttpServlet {
  private final Concentrator CONTAINER;
  public Pkg(Concentrator container) {
    CONTAINER = container;
  }

  // Используем идею REST - параметры GET запросе не передаются
  @Override
  protected void doGet(
    HttpServletRequest request,
    HttpServletResponse response) throws ServletException, IOException
  {

    response.setContentType("text/html");
    response.setStatus(HttpServletResponse.SC_OK);

    //TODO(zaqwes): Сделать декоратор для класса
    String jsonResponse = new Gson().toJson(CONTAINER.getPackageActiveNode());

    response.setCharacterEncoding("UTF-8");
    response.getWriter().println(jsonResponse);
  }
}