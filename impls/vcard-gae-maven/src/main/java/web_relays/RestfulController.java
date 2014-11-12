package web_relays;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

// 406 trouble
// http://adrianmejia.com/blog/2012/04/27/spring-mvc-3-plus-ajax-getjson-and-solving-406-not-accepted/
@Controller
@RequestMapping("/test")
public class RestfulController {	
	//private AppInstance app = AppInstance.getInstance();
	@RequestMapping(value="/plot", method = RequestMethod.GET, headers="Accept=application/json")
	public @ResponseBody List<Integer> get(HttpServletRequest request, HttpServletResponse res) {
		 ArrayList<Integer> r = new ArrayList<Integer>();
		 return r;
	}
}