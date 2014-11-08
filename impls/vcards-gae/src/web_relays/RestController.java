package web_relays;

import gae_store_space.AppInstance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/cont")
public class RestController {	
	//private AppInstance app = AppInstance.getInstance();
	
	public RestController(){
	   System.out.println("init RestController");
	}
	
	//this method responses to GET request http://localhost/spring-mvc-json/rest/cont
	// return Person object as json
	
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
		 
		 //res.setStatus(HttpServletResponse.SC_FORBIDDEN);
		ArrayList<Integer> r = new ArrayList<Integer>();
		
		 return r;
	}
	
	//this method response to POST request http://localhost/spring-mvc-json/rest/cont/person
	// receives json data sent by client --> map it to Person object
	// return Person object as json
	@RequestMapping(value="person", method = RequestMethod.POST)
	public @ResponseBody Person post( @RequestBody final  Person person) {    
	
	   System.out.println(person.getId() + " " + person.getName());
	   return person;
	}
}