package read_chain.web_wrapper;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import info_core_accessors.*;
import org.junit.Test;
import through_functional.configurator.GlobalConfigurator;

public class ContainerNodeControllersTest {
  @Test
  public void testGeneratePackage() throws Exception {
    BuilderControllers builder = new BuilderControllers();
    String pathToCfgFile = "./my.yaml";
    ImmutableSet<String> namesNodes = new GlobalConfigurator(pathToCfgFile).getRegisteredNodes().get();
    ImmutableList<ImmutableNodeAccessor> controllers = builder.createControllersForAllNodes(
        namesNodes, new FabricImmutableNodeControllersImpl());
    ContainerNodeControllers container = new ContainerNodeControllers(controllers);
    container.getPerWordData(0);
  }
}
