package web_relays;

import gae_store_space.AppInstance;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

//res.setStatus(HttpServletResponse.SC_FORBIDDEN);

@Controller
@RequestMapping("/cont")
public class RestfulController {	
	private AppInstance app = AppInstance.getInstance();
	
	@RequestMapping(value="plot", method = RequestMethod.GET)
	public @ResponseBody List<Integer> get(HttpServletRequest request, HttpServletResponse res) {
		 /*Map<String, String[]> parameters = request.getParameterMap();
		
		 for(String key : parameters.keySet()) {
		     System.out.println(key);
		     String[] vals = parameters.get(key);
		     for(String val : vals)
		         System.out.println(" -> " + val);
		 }
		
		 Person person = new Person();
		 person.setId(1);
		 person.setName("hmk");
		 */
		 
		 ArrayList<Integer> r = new ArrayList<Integer>();
		
		 return r;
	}
}