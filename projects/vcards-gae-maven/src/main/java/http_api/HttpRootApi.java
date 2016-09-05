package http_api;

import backend.AppInstance;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import java.io.BufferedReader;
import java.io.InputStreamReader;


// 406 trouble
// http://adrianmejia.com/blog/2012/04/27/spring-mvc-3-plus-ajax-getjson-and-solving-406-not-accepted/
@Controller
@RequestMapping("/")
public class HttpRootApi {
	private AppInstance app = AppInstance.getInstance();

	@RequestMapping(value="/user_summary", method = RequestMethod.GET, headers="Accept=application/json")
	public @ResponseBody
	List<ValuePageSummary> get(HttpServletRequest request, HttpServletResponse res) {
		return app.getUserInformation();
	}

	@RequestMapping(value="/pkg", method = RequestMethod.GET, headers="Accept=application/json")
	public @ResponseBody
	ValueWordData getterSingleWord(HttpServletRequest request, HttpServletResponse res) {
		String value = request.getParameter("arg0");
		if (value == null)
			throw new IllegalArgumentException();

		try {
			ValuePath path = new ObjectMapper().readValue(value, ValuePath.class);

			String pageName = path.getPageName().get();
			return app.getWordData(pageName);
		} catch (IOException ex) {
			res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}

		return null;
	}


	@RequestMapping(value="/reset_storage", method = RequestMethod.GET, headers="Accept=application/json")
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		app.eraseStore();
	}

	@RequestMapping(value="/know_it", method = RequestMethod.PUT, headers="Accept=application/json")
	public void doPut(HttpServletRequest request, HttpServletResponse response) {
		try {
			BufferedReader br =
					new BufferedReader(new InputStreamReader(request.getInputStream()));

			ValuePath p = new ObjectMapper().readValue(br.readLine(), ValuePath.class);

			app.disablePoint(p);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	@RequestMapping(value="/mark-known-and-get-new-word", method = RequestMethod.PUT)
	public @ResponseBody
	ValueWordData markKnownAndGetNewWord(HttpServletRequest request) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
			ValuePath p = new ObjectMapper().readValue(br.readLine(), ValuePath.class);
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