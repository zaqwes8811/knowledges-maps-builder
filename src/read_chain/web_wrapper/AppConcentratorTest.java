package read_chain.web_wrapper;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import info_core_accessors.*;
import org.junit.Test;
import through_functional.configurator.AppConfigurator;

public class AppConcentratorTest {
  @Test
  public void testGeneratePackage() throws Exception {
    Wrapper wrapper = new Wrapper();
    String pathToCfgFile = "./my.yaml";
    ImmutableSet<String> namesNodes = new AppConfigurator(pathToCfgFile).getRegisteredNodes().get();
    ImmutableList<ImmutableNodeAccessor> accessors = wrapper.getNodes(
      namesNodes, new FabricImmutableNodeAccessors());
    AppConcentrator container = new AppConcentrator(accessors);
    container.getPackageActiveNode();
  }
}
