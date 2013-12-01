package through_functional.hided;

import through_functional.hided.ProcessorTargets;
import org.junit.Test;
import through_functional.CrosscuttingsException;

import java.util.List;

public class ProcessorTargetsTest {
  @Test(expected = CrosscuttingsException.class)
  public void testRunParser() throws CrosscuttingsException {
    String spiderTargetFname = "./targets/spider_extractor_target";
    List<List<String>> listTargets = ProcessorTargets.runParser(spiderTargetFname);
  }

  @Test
  public void testPathSplitter() throws CrosscuttingsException {
    String spiderTargetFname = "./targets/spider_extractor_target.json";
    List<String> splittedPath =  ProcessorTargets.splitUrlToFilenameAndPath(spiderTargetFname);
  }
}
