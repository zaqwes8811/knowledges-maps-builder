package read_chain.web_wrapper;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import common.Util;
import info_core_accessors.FabricImmutableNodeAccessors;
import info_core_accessors.ImmutableNodeAccessor;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import through_functional.configurator.GlobalConfigurator;
import through_functional.configurator.ConfigurationFileIsCorrupted;
import through_functional.configurator.NoFoundConfigurationFile;

public class WebRelay {
  public static void main(String[] args) throws Exception {
    try {
      Wrapper wrapper = new Wrapper();

      String pathToCfgFile = "app.yaml";
      ImmutableSet<String> namesNodes = new GlobalConfigurator(pathToCfgFile).getRegisteredNodes().get();
      ImmutableList<ImmutableNodeAccessor> accessors = wrapper.getNodes(
          namesNodes, new FabricImmutableNodeAccessors());

      ListGetter container = new ListGetterImpl(accessors);
      HandlerList handlers = Wrapper.buildHandlers(container);

      Server server = new Server(8081);
      server.setHandler(handlers);
      server.start();
      server.join();
    } catch (NoFoundConfigurationFile e) {
      Util.print(e);
      throw new RuntimeException();
    } catch (ConfigurationFileIsCorrupted e) {
      Util.print(e);
      throw new RuntimeException();
    }
  }
}
