package bleeding_edge;

import org.junit.Test;

import com.carrotsearch.junitbenchmarks.AbstractBenchmark;

public class MyTest extends AbstractBenchmark {
  @Test
  public void twentyMillis() throws Exception {
    Thread.sleep(20);
  }
}
