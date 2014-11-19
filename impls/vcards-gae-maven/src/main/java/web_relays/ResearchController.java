package web_relays;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import gae_store_space.AppInstance;
import gae_store_space.PageKind;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import pipeline.math.DistributionElement;
import web_relays.protocols.PathValue;
import web_relays.protocols.TextPackage;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

@Controller
@RequestMapping("/research")
public class ResearchController {
  private AppInstance app = AppInstance.getInstance();

  @RequestMapping(value="/get_distribution", method = RequestMethod.GET, headers="Accept=application/json")
  public @ResponseBody
  ImmutableList<DistributionElement> getDistribution(HttpServletRequest request, HttpServletResponse res) {
    Optional<String> v = Optional.fromNullable(request.getParameter("arg0"));
    ImmutableList<DistributionElement> empty = ImmutableList.copyOf(new ArrayList<DistributionElement>());
    if (v.isPresent()) {
      try {
        PathValue path = new ObjectMapper().readValue(v.get(), PathValue.class);
        return app.getDistribution(path);
      } catch (IOException ex) {}
    } else {
      res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }
    return empty;
  }

  @RequestMapping(value="/accept_text", method = RequestMethod.POST, headers="Accept=application/json")
  public void putPage(HttpServletRequest request, HttpServletResponse res) {
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
      } else {
        throw new IllegalArgumentException();
      }
    } catch (IOException e) {
      res.setStatus(HttpServletResponse.SC_NO_CONTENT);
      //} catch (IllegalStateException e) {
      //response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      //} catch (IllegalArgumentException e) {
      //  response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }
  }

  @RequestMapping(value="/get_lengths_sentences", method = RequestMethod.GET, headers="Accept=application/json")
  public @ResponseBody
  ArrayList<Integer> getSentencesLengths(HttpServletRequest request, HttpServletResponse res) {
    ArrayList<Integer> empty = new ArrayList<Integer>();
    Optional<String> value = Optional.fromNullable(request.getParameter("arg0"));

    if (value.isPresent()) {
      try {
        PathValue path = new ObjectMapper().readValue(value.get(), PathValue.class);
        Optional<String> pageName = path.getPageName();
        if (pageName.isPresent()) {
          Optional<PageKind> p = app.getPage(path.getPageName().get());
          if (p.isPresent()) {
            return p.get().getLengthsSentences();
          }
        }
      } catch (IOException ex) {}
    }
    return empty;
  }
}
