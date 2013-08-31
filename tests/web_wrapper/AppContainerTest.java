package web_wrapper;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import idx_coursors.*;
import org.junit.Test;
import through_functional.configurator.AppConfigurator;
import through_functional.configurator.ConfFileIsCorrupted;
import through_functional.configurator.NoFoundConfFile;
import ui.UI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: кей
 * Date: 25.07.13
 * Time: 19:08
 * To change this template use File | Settings | File Templates.
 */
public class AppContainerTest {

  //TODO(zaqwes): Венуть tuple - результат и отчет - если отчет пуст - все хорошо
  private ImmutableList<ImmutableNodeAccessor> getNodes(
      ImmutableSet<String> nameNodes, IFabricImmutableNodeAccessors fabric)
      throws ConfFileIsCorrupted, NoFoundConfFile {
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

  @Test
  public void testGeneratePackage() throws Exception {
    try {
      ImmutableSet<String> namesNodes = AppConfigurator.getRegisteredNodes().get();
      ImmutableList<ImmutableNodeAccessor> accessors = getNodes(
        namesNodes, new FabricImmutableNodeAccessors());
      new AppContainer(accessors).getPackage();
    } catch (NoFoundConfFile e) {
      UI.showMessage("Configuration file no found - "+e.getFileName());
    } catch (ConfFileIsCorrupted e) {
      UI.showMessage(e.WHAT_HAPPENED);
    }
  }
}
