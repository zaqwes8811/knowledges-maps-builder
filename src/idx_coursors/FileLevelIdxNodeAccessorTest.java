package idx_coursors;

import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: кей
 * Date: 20.07.13
 * Time: 15:43
 * To change this template use File | Settings | File Templates.
 */
public class FileLevelIdxNodeAccessorTest {
  @Test(expected=NodeNoFound.class)
  public void testNoExistNode() throws NodeNoFound {
     String pathToNode = "z:/NoExist";
     FileLevelIdxNodeAccessor accessor = FileLevelIdxNodeAccessor.create(pathToNode);
  }
}
