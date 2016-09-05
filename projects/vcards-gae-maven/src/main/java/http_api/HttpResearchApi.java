package http_api;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import backend.AppInstance;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import backend.math.DistributionElement;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;

@Controller
@RequestMapping("/research")
public class HttpResearchApi
{
    private AppInstance app = AppInstance.getInstance();

    @RequestMapping(value="/get_distribution", method = RequestMethod.GET, headers="Accept=application/json")
    public @ResponseBody
    ImmutableList<DistributionElement> getDistribution(HttpServletRequest request, HttpServletResponse res) {
    Optional<String> v = Optional.fromNullable(request.getParameter("arg0"));
    ImmutableList<DistributionElement> empty = ImmutableList.copyOf(new ArrayList<DistributionElement>());
    if (v.isPresent()) {
      try {
        ValuePath path = new ObjectMapper().readValue(v.get(), ValuePath.class);
        return app.getDistribution(path);
      } catch (IOException ex) {}
    } else {
      res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }
    return empty;
    }

    public String extractTextFromReq(HttpServletRequest request) throws IOException
    {
        BufferedReader reader =
            new BufferedReader(new InputStreamReader(request.getInputStream()));

        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }
        return builder.toString();
    }

    @RequestMapping(value="/accept_text", method = RequestMethod.POST,
            headers="Accept=application/json")
    public void createOrRecreatePage(HttpServletRequest request, HttpServletResponse res) {
        try {
            ValueTextPackage p = new ObjectMapper().readValue(extractTextFromReq(request), ValueTextPackage.class);
            app.createOrReplacePage(p.name, p.text);
            res.setStatus(HttpServletResponse.SC_OK);
        } catch (IOException e) {
          res.setStatus(HttpServletResponse.SC_NO_CONTENT);
        }
    }

    @RequestMapping(value="/accept_dict", method = RequestMethod.POST,
            headers="Accept=application/json")
    public void createOrRecreateDict(HttpServletRequest request, HttpServletResponse res)
    {
        try {
            String text = extractTextFromReq(request);
            ValueTextPackage p = new ObjectMapper().readValue(text, ValueTextPackage.class);
            app.createOrReplaceDict(p.name, p.text);
            res.setStatus(HttpServletResponse.SC_OK);
        } catch (IOException e) {
            res.setStatus(HttpServletResponse.SC_NO_CONTENT);
        }
    }


    @RequestMapping(value="/get_lengths_sentences", method = RequestMethod.GET, headers="Accept=application/json")
    public @ResponseBody ArrayList<Integer> getSentencesLengths(
          HttpServletRequest request, HttpServletResponse res) {
    ArrayList<Integer> empty = new ArrayList<Integer>();
    Optional<String> value = Optional.fromNullable(request.getParameter("arg0"));

    if (value.isPresent()) {
      try {
        ValuePath path = new ObjectMapper().readValue(value.get(), ValuePath.class);
        Optional<String> pageName = path.getPageName();
        if (pageName.isPresent()) {
          PageWrapper p = app.getPage(path.getPageName().get());
          return p.getLengthsSentences();
        }
      } catch (IOException ex) {}
    }
    return empty;
    }
}
