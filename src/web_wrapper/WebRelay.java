package web_wrapper;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import idx_coursors.FabricImmutableNodeAccessors;
import idx_coursors.ImmutableNodeAccessor;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import through_functional.configurator.AppConfigurator;
import through_functional.configurator.ConfFileIsCorrupted;
import through_functional.configurator.NoFoundConfFile;

/**
 * Created with IntelliJ IDEA.
 * User: кей
 * Date: 15.09.13
 * Time: 9:35
 * To change this template use File | Settings | File Templates.
 */
public class WebRelay {
  public static void main(String[] args) throws Exception {
    // Сервлеты
    try {
      ///*
      Wrapper wrapper = new Wrapper();

      ImmutableSet<String> namesNodes = AppConfigurator.getRegisteredNodes().get();
      ImmutableList<ImmutableNodeAccessor> accessors = wrapper.getNodes(
          namesNodes, new FabricImmutableNodeAccessors());//*/

      Concentrator container = new AppConcentrator(accessors);
        //FakeConcentrator();//(accessors);

      HandlerList handlers = Wrapper.buildHandlers(container);

      Server server = new Server(8080);
      server.setHandler(handlers);
      server.start();
      server.join();
    } catch (NoFoundConfFile e) {
      throw new RuntimeException();
    } catch (ConfFileIsCorrupted e) {
      throw new RuntimeException();
    }
  }
}