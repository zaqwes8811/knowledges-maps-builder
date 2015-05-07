package service_layer

import javax.servlet.http.{HttpServletResponse, HttpServletRequest}

import instances.AppInstance
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.{RequestMethod, ResponseBody, RequestMapping}
import service_layer.protocols.PageSummaryValue


// http://stackoverflow.com/questions/11215621/how-to-effectively-use-scala-in-a-spring-mvc-project
@Controller
@RequestMapping
class ScalaSpringController {
  val app = AppInstance.getInstance

  @RequestMapping(
    value=Array("/user_summary_scala"),
    method=Array(RequestMethod.GET),
    headers=Array("Accept=application/json")
  )
  @ResponseBody
  def get(request: HttpServletRequest, res: HttpServletResponse): java.util.List[PageSummaryValue] =
    app.getUserInformation

}