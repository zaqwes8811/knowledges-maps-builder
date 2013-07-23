package crosscuttings;

import com.google.common.collect.ImmutableSet;
import common.Util;
import org.junit.Test;

//
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
