package com.github.marschall;

import java.io.IOException
import java.util.Locale
import java.util.concurrent.CopyOnWriteArraySet
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeUnit.SECONDS
import java.util.logging.Level
import java.util.logging.Logger
import javax.servlet.http.HttpServletRequest
import org.eclipse.jetty.servlets.EventSource
import org.eclipse.jetty.servlets.EventSource.Emitter
import org.eclipse.jetty.servlets.EventSourceServlet

class ServerTimeServlet: EventSourceServlet() {


    override fun init() {
        super.init()
    }


    override fun destroy() {
        super.destroy()
    }

    override fun newEventSource(request: HttpServletRequest?): EventSource? {
        return null;
    }


}

fun formattedTime(): String {
    return "";//String.format(Locale.US, "%tT\r\n", System.currentTimeMillis())!!//FIXED in 1.0.1
}

val LOG: Logger = Logger.getLogger("event-source-sample")