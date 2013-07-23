package crosscuttings;

import com.google.common.collect.ImmutableSet;
import common.Util;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: кей
 * Date: 23.07.13
 * Time: 14:53
 * To change this template use File | Settings | File Templates.
 */
public class AppConfiguratorTest {
  @Test//(expected = ConfFileIsCorrupted.class)
  public void testGetPathToAppFolder() throws Exception {
    try {
     String tmp = AppConfigurator.getPathToAppFolder().get();
    Util.print(tmp);
    } catch (NoFoundConfFile e) {
      Util.print(e.getFileName());
    }
  }

  @Test//(expected = ConfFileIsCorrupted.class)
  public void testGetNodes() throws Exception {
    try {
      ImmutableSet<String> tmp = AppConfigurator.getRegisteredNodes().get();
      Util.print(tmp);
    } catch (NoFoundConfFile e) {
      Util.print(e.getFileName());
    }
  }
}
