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

/**
 * Created with IntelliJ IDEA.
 * User: кей
 * Date: 25.07.13
 * Time: 19:08
 * To change this template use File | Settings | File Templates.
 */
public class AppConcentratorTest {

  //TODO(zaqwes): Венуть tuple - результат и отчет - если отчет пуст - все хорошо


  @Test
  public void testGeneratePackage() throws Exception {
    try {
      Wrapper wrapper = new Wrapper();
      String pathToCfgFile = "app.yaml";
      ImmutableSet<String> namesNodes = new AppConfigurator(pathToCfgFile).getRegisteredNodes().get();
      ImmutableList<ImmutableNodeAccessor> accessors = wrapper.getNodes(
        namesNodes, new FabricImmutableNodeAccessors());
      AppConcentrator container = new AppConcentrator(accessors);
      container.getPackageActiveNode();
    } catch (NoFoundConfFile e) {
      UI.showMessage("Configuration file no found - "+e.getFileName());
    } catch (ConfFileIsCorrupted e) {
      UI.showMessage(e.WHAT_HAPPENED);
    }
  }
}
