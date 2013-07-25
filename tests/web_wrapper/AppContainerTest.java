package web_wrapper;

import common.Util;
import net.jcip.annotations.Immutable;
import org.junit.Test;

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
  @Test
  public void testCloseApp() throws Exception {
    /*List<Integer> result = new ArrayList<Integer>();
    Integer SIZE_EXPERIMENT = 10000;
    for (int i = 0; i < SIZE_EXPERIMENT; ++i) {
      result.add(AppContainer.getKey());
    }
    Util.print(result);*/
  }

  @Test
  public void testMain() throws Exception {
    for (int i = 0; i < 19; ++i) {
      Util.print(AppContainer.getPackage());
    }
  }
}
