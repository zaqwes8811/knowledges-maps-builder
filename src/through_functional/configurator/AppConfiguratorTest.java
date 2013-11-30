package through_functional.configurator;

import org.junit.Test;

public class AppConfiguratorTest {
  @Test(expected = NoFoundConfFile.class)
  public void testGetPathToAppFolder() throws Exception {
    String path = "noExist-app.yaml";
    new AppConfigurator(path).getPathToAppFolder().get();
  }

  @Test
  public void testFileExist() throws Exception {
    String path = "./app.yaml";
    new AppConfigurator(path).getPathToAppFolder().get();
  }
}
