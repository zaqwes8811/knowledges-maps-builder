package service_layer

import instances.AppInstance
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody
import service_layer.protocols.PageSummaryValue
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Created by zaqwes on 5/6/15.
 */

Controller
RequestMapping
public class BleedingEdgeController
{
    private val app = AppInstance.getInstance()

    // FIXME: http://stackoverflow.com/questions/11645266/java-annotation-parameters-in-kotlin
    // https://github.com/gzoritchak/kotlin-examples/tree/master/kotlin-angular-spring-mvc
    RequestMapping(
            value=("/user_summary_kot"),  // not array(...), if Scala conneced need array
            method=array(RequestMethod.GET),
            headers=array("Accept=application/json")  // is deprecated. Use arrayOf() instead. but not compiled
    )
    ResponseBody
    public fun get(request: HttpServletRequest, res: HttpServletResponse): List<PageSummaryValue> {
        return app.getUserInformation()
    }
}