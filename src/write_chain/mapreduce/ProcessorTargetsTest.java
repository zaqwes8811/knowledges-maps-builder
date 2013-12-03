package write_chain.mapreduce;

import org.junit.Test;
import through_functional.CrosscuttingsException;
import write_chain.mapreduce.ProcessorTargets;

import java.util.List;

public class ProcessorTargetsTest {
  @Test(expected = CrosscuttingsException.class)
  public void testRunParser() throws CrosscuttingsException {
    String spiderTargetFilename = "./targets/spider_extractor_target";
    List<List<String>> listTargets = ProcessorTargets.runParser(spiderTargetFilename);
  }

  @Test
  public void testPathSplitter() throws CrosscuttingsException {
    String spiderTargetFilename = "./targets/spider_extractor_target.json";
    List<String> splittedPath =  ProcessorTargets.splitUrlToFilenameAndPath(spiderTargetFilename);
  }
}
