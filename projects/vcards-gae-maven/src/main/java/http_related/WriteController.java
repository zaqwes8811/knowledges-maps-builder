package http_related;

import backend.AppInstance;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Controller
@RequestMapping("/")
public class WriteController {
  private final AppInstance app = AppInstance.getInstance();

  @RequestMapping(value="/reset_storage", method = RequestMethod.GET, headers="Accept=application/json")
  public void doGet(HttpServletRequest request, HttpServletResponse response) {
    app.eraseStore();
  }

  @RequestMapping(value="/know_it", method = RequestMethod.PUT, headers="Accept=application/json")
  public void doPut(HttpServletRequest request, HttpServletResponse response) {
    try {
      BufferedReader br =
        new BufferedReader(new InputStreamReader(request.getInputStream()));

      PathValue p = new ObjectMapper().readValue(br.readLine(), PathValue.class);

      app.disablePoint(p);
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  @RequestMapping(value="/mark-known-and-get-new-word", method = RequestMethod.PUT)
  public @ResponseBody
  WordDataValue markKnownAndGetNewWord(HttpServletRequest request) {
    try {
      BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
      PathValue p = new ObjectMapper().readValue(br.readLine(), PathValue.class);
      String pageName = p.getPageName().get();

      {
        // FIXME: make atomic
        synchronized (app) {
          app.disablePoint(p);
          return app.getWordData(pageName);
        }
      }
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }
}
