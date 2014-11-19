package web_relays;

import com.google.common.base.Optional;
import gae_store_space.AppInstance;
import gae_store_space.PageKind;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import pipeline.TextPipeline;
import web_relays.protocols.PageSummaryValue;
import web_relays.protocols.PathValue;
import web_relays.protocols.WordDataValue;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

// 406 trouble
// http://adrianmejia.com/blog/2012/04/27/spring-mvc-3-plus-ajax-getjson-and-solving-406-not-accepted/
@Controller
@RequestMapping("/")
public class ReadController {
	private AppInstance app = AppInstance.getInstance();

	@RequestMapping(value="/user_summary", method = RequestMethod.GET, headers="Accept=application/json")
	public @ResponseBody
	List<PageSummaryValue> get(HttpServletRequest request, HttpServletResponse res) {
		return app.getUserInformation(TextPipeline.defaultUserId);
	}

	@RequestMapping(value="/pkg", method = RequestMethod.GET, headers="Accept=application/json")
	public @ResponseBody
	WordDataValue getterSingleWord(HttpServletRequest request, HttpServletResponse res) {
		String value = request.getParameter("arg0");
		try {
			if (value == null)
				throw new IllegalArgumentException();

			PathValue path = new ObjectMapper().readValue(value, PathValue.class);

			Optional<PageKind> p = app.getPage(path.getPageName().get());
			if (!p.isPresent())
				throw new IllegalStateException();

			return p.get().getWordData().get();
		} catch (IOException ex) {
			res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}

		return null;
	}
}