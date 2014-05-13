package hided.controllers.web_wrapper;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import dal.accessors_text_file_storage.FabricImmutableNodeControllers;
import dal.accessors_text_file_storage.ImmutableNodeAccessor;
import dal.accessors_text_file_storage.NodeIsCorrupted;
import dal.accessors_text_file_storage.NodeNoFound;
import hided.crosscuttings.configurator.ConfigurationFileIsCorrupted;
import hided.crosscuttings.configurator.NoFoundConfigurationFile;

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
