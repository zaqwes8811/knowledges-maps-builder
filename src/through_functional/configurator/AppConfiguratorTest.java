package through_functional.configurator;

import com.google.common.collect.ImmutableSet;
import common.Util;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created with IntelliJ IDEA.
 * User: кей
 * Date: 21.09.13
 * Time: 18:41
 * To change this template use File | Settings | File Templates.
 */
public class AppConfiguratorTest {
  @Test(expected = NoFoundConfFile.class)
  public void testGetPathToAppFolder() throws Exception {
    String path = "app.yaml.noExist";
    String tmp = new AppConfigurator(path).getPathToAppFolder().get();
  }

  @Test
  public void testFileExist() throws Exception{
    try {
      String path = "app.yaml";
      String tmp = new AppConfigurator(path).getPathToAppFolder().get();
    } catch (NoFoundConfFile e) {
      assertTrue(false);
      //throw e;
    }
  }

  @Test//(expected = ConfFileIsCorrupted.class)
  public void testGetNodes() throws Exception {
    /*try {
      ImmutableSet<String> tmp = AppConfigurator.getRegisteredNodes().get();
      Util.print(tmp);
    } catch (NoFoundConfFile e) {
      Util.print(e.getFileName());
    } */
  }
}
