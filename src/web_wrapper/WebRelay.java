package web_wrapper;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import common.Util;
import idx_coursors.FabricImmutableNodeAccessors;
import idx_coursors.ImmutableNodeAccessor;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import through_functional.configurator.AppConfigurator;
import through_functional.configurator.ConfFileIsCorrupted;
import through_functional.configurator.NoFoundConfFile;

public class WebRelay {
  public static void main(String[] args) throws Exception {
    try {
      Wrapper wrapper = new Wrapper();

      String pathToCfgFile = "app.yaml";
      ImmutableSet<String> namesNodes = new AppConfigurator(pathToCfgFile).getRegisteredNodes().get();
      ImmutableList<ImmutableNodeAccessor> accessors = wrapper.getNodes(
          namesNodes, new FabricImmutableNodeAccessors());

      Concentrator container = new AppConcentrator(accessors);
      HandlerList handlers = Wrapper.buildHandlers(container);

      Server server = new Server(8081);
      server.setHandler(handlers);
      server.start();
      server.join();
    } catch (NoFoundConfFile e) {
      Util.print(e);
      throw new RuntimeException();
    } catch (ConfFileIsCorrupted e) {
      Util.print(e);
      throw new RuntimeException();
    }
  }
}
