package read_chain.web_wrapper;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import info_core_accessors.FabricImmutableNodeControllers;
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

public class BuilderControllers {
  public ImmutableList<ImmutableNodeAccessor> createControllersForAllNodes(
      ImmutableSet<String> nameNodes,
      FabricImmutableNodeControllers fabric)
        throws ConfigurationFileIsCorrupted,
               NoFoundConfigurationFile
    {
    Map<String, String> report = new HashMap<String, String>();
    List<ImmutableNodeAccessor> controllers = new ArrayList<ImmutableNodeAccessor>();
    for (String node: nameNodes) {
      try {
        ImmutableNodeAccessor accessor = fabric.create(node);
        controllers.add(accessor);
      } catch (NodeIsCorrupted e) {
        report.put(node, "Is corrupted");
      } catch (NodeNoFound e) {
        report.put(node, "No found");
      }
    }
    return ImmutableList.copyOf(controllers);
  }
}
