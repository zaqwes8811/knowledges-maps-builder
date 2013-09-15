package web_wrapper;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import idx_coursors.IFabricImmutableNodeAccessors;
import idx_coursors.ImmutableNodeAccessor;
import idx_coursors.NodeIsCorrupted;
import idx_coursors.NodeNoFound;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import through_functional.configurator.ConfFileIsCorrupted;
import through_functional.configurator.NoFoundConfFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Wrapper {
  public static HandlerList buildHandlers(Concentrator container) {
    // Подключаем ресурсы
    ResourceHandler resourceHandler = new ResourceHandler();
    resourceHandler.setDirectoriesListed(true);
    resourceHandler.setWelcomeFiles(new String[]{ "index.html" });
    resourceHandler.setResourceBase("./web-pages");

    // Регистрируем обработчики
    ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
    context.setContextPath("/");
    context.addServlet(new ServletHolder(new Pkg(container)),"/pkg");

    // Коннектим все
    HandlerList handlers = new HandlerList();
    handlers.setHandlers(new Handler[] { resourceHandler, context });
    return handlers;
  }

  public ImmutableList<ImmutableNodeAccessor> getNodes(
    ImmutableSet<String> nameNodes, IFabricImmutableNodeAccessors fabric)
    throws ConfFileIsCorrupted, NoFoundConfFile
  {
    Map<String, String> report = new HashMap<String, String>();
    List<ImmutableNodeAccessor> accessors = new ArrayList<ImmutableNodeAccessor>();
    for (String node: nameNodes) {
      try {
        ImmutableNodeAccessor accessor = fabric.create(node);
        accessors.add(accessor);
      } catch (NodeIsCorrupted e) {
        report.put(node, "Is corrupted");
      } catch (NodeNoFound e) {
        report.put(node, "No found");
      }
    }
    return ImmutableList.copyOf(accessors);
  }

}
