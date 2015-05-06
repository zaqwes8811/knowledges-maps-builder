package service_layer;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import pipeline.pages.PageFrontend;
import instances.AppInstance;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import pipeline.math.DistributionElement;
import service_layer.protocols.PathValue;
import service_layer.protocols.TextPackage;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
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

  public String getData(HttpServletRequest request) throws IOException {
    // http://stackoverflow.com/questions/1829784/should-i-close-the-servlet-outputstream
    // not need it
    //Closer closer = Closer.create();
    //String r = "";
    //try {
      BufferedReader reader =
        new BufferedReader(new InputStreamReader(request.getInputStream()));

      //closer.register(reader);

      ArrayList<String> lines = new ArrayList<String>();
      String line;
      while ((line = reader.readLine()) != null) {
        lines.add(line);
      }

    //} catch (Throwable e) {
    //  closer.rethrow(e);
    //} finally {
    //  closer.close();  // нужно ли его вообще закрывать?
    //}
    return Joiner.on("").join(lines);
  }

  @RequestMapping(value="/accept_text", method = RequestMethod.POST, headers="Accept=application/json")
  public void createOrRecreatePage(HttpServletRequest request, HttpServletResponse res) {
    try {
      TextPackage p = new ObjectMapper().readValue(getData(request), TextPackage.class);
      if (p.getText().isPresent() && p.getName().isPresent()) {
        app.createOrReplacePage(p.getName().get(), p.getText().get());
      } else {
        throw new IllegalArgumentException();
      }
    } catch (IOException e) {
      res.setStatus(HttpServletResponse.SC_NO_CONTENT);
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
          PageFrontend p = app.getPage(path.getPageName().get());
          return p.getLengthsSentences();
        }
      } catch (IOException ex) {}
    }
    return empty;
  }
}
