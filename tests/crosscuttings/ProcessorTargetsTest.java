package crosscuttings;

import through_functional.hided.ProcessorTargets;
import org.junit.Test;
import through_functional.CrosscuttingsException;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: кей
 * Date: 29.04.13
 * Time: 21:51
 * To change this template use File | Settings | File Templates.
 */
public class ProcessorTargetsTest {
  @Test
  public void testRunParser() {
    String spiderTargetFname = "apps/targets/spider_extractor_target";
    try {
      List<List<String>> listTargets = ProcessorTargets.runParser(spiderTargetFname);
      //for (List<String> target : listTargets)
      //  ImmutableProcessorTargets.print(target);
    } catch (CrosscuttingsException e) {
      System.out.println(e.getMessage());
    }
  }

  @Test
  public void testPathSplitter() {
    print("\nNEW TEST");
    String spiderTargetFname = "apps/targets/spider_extractor_target.json";
    try {
      List<String> splittedPath =  ProcessorTargets.splitUrlToFilenameAndPath(spiderTargetFname);
      print(splittedPath);
    } catch (CrosscuttingsException e) {
      System.out.println(e.getMessage());
    }
  }

  static void print(Object msg) {
    System.out.println(msg);
  }
}
