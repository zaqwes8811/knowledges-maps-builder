package read_chain.web_wrapper;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import info_core_accessors.IFabricImmutableNodeAccessors;
import info_core_accessors.ImmutableNodeAccessor;
import info_core_accessors.NodeIsCorrupted;
import info_core_accessors.NodeNoFound;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import through_functional.configurator.ConfigurationFileIsCorrupted;
import through_functional.configurator.NoFoundConfigurationFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Wrapper {
  public static HandlerList buildHandlers(OneNodePackageGenerator container) {
    // Подключаем ресурсы
    ResourceHandler resourceHandler = new ResourceHandler();
    resourceHandler.setDirectoriesListed(true);
    resourceHandler.setWelcomeFiles(new String[]{ "index.html" });
    resourceHandler.setResourceBase("./web-pages");

    // Регистрируем обработчики
    ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
    context.setContextPath("/");
    //context.addServlet(new ServletHolder(new Pkg(container)),"/pkg");

    // Коннектим все
    HandlerList handlers = new HandlerList();
    handlers.setHandlers(new Handler[] { resourceHandler, context });
    return handlers;
  }

  public ImmutableList<ImmutableNodeAccessor> getNodes(
    ImmutableSet<String> nameNodes, IFabricImmutableNodeAccessors fabric)
    throws ConfigurationFileIsCorrupted, NoFoundConfigurationFile
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
