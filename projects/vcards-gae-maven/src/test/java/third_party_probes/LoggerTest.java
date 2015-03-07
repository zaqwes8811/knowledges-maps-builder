package third_party_probes;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.junit.Test;

// http://javarevisited.blogspot.ru/2011/05/top-10-tips-on-logging-in-java.html
//
// http://www.tutorialspoint.com/log4j/
public class LoggerTest {
  private static org.apache.log4j.Logger log = Logger.getLogger(LoggerTest.class.getName());

  @Test
  public void test() {
    //PropertyConfigurator.configure("log4j.properties");
    BasicConfigurator.configure();
    //log.info("trace");
  }
}
