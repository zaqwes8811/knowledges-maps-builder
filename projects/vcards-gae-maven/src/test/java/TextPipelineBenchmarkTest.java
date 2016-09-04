// http://labs.carrotsearch.com/junit-benchmarks-tutorial.html
// !!http://labs.carrotsearch.com/junit-benchmarks-tutorial.html

import org.junit.Rule;
import org.junit.rules.TestRule;

import com.carrotsearch.junitbenchmarks.BenchmarkRule;
import com.google.caliper.Benchmark;

public class TextPipelineBenchmarkTest {// extends Benchmark {
	@Rule
	public TestRule benchmarkRun = new BenchmarkRule();
	
	void get(int rep) {
	  // loop
	}
}
