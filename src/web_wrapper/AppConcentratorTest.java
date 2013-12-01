package web_wrapper;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import idx_coursors.*;
import org.junit.Test;
import static org.junit.Assert.*;
import through_functional.configurator.AppConfigurator;
import through_functional.configurator.ConfFileIsCorrupted;
import through_functional.configurator.NoFoundConfFile;
import ui.UI;

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
