package web_wrapper;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import common.Util;
import idx_coursors.IdxNodeAccessor;
import idx_coursors.ImmutableNodeMeansOfAccess;
import idx_coursors.NodeIsCorrupted;
import idx_coursors.NodeNoFound;
import net.jcip.annotations.Immutable;
import org.junit.Test;
import through_functional.configurator.AppConfigurator;
import through_functional.configurator.ConfFileIsCorrupted;
import through_functional.configurator.NoFoundConfFile;
import ui.UI;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: кей
 * Date: 25.07.13
 * Time: 19:08
 * To change this template use File | Settings | File Templates.
 */
public class AppContainerTest {
  private ImmutableList<ImmutableNodeMeansOfAccess> getNodes(ImmutableSet<String> nameNodes)
      throws ConfFileIsCorrupted, NoFoundConfFile {

    List<ImmutableNodeMeansOfAccess> tmpNodes = new ArrayList<ImmutableNodeMeansOfAccess>();
    for (String node: nameNodes) {
      // Загружает данные узла
      try {
        tmpNodes.add(IdxNodeAccessor.createImmutableConnection(node));
      } catch (NodeIsCorrupted e) {
        UI.showMessage("Node is corrupted - " + node);
      } catch (NodeNoFound e) {
        UI.showMessage("Node no found - "+node);
      }
    }
    ImmutableList<ImmutableNodeMeansOfAccess> nodes = ImmutableList.copyOf(tmpNodes);
    return nodes;
  }

  @Test
  public void testGeneratePackage() throws Exception {
    try {
      ImmutableSet<String> namesNodes = AppConfigurator.getRegisteredNodes().get();
      new AppContainer(getNodes(namesNodes)).getPackage();
    } catch (NoFoundConfFile e) {
      UI.showMessage("Configuration file no found - "+e.getFileName());
      //closeApp();
    } catch (ConfFileIsCorrupted e) {
      UI.showMessage(e.WHAT_HAPPENED);
      //closeApp();
    }
  }
}
