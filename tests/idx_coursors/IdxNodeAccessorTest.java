package idx_coursors;

import com.google.common.base.Optional;
import common.Util;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA.
 * User: кей
 * Date: 20.07.13
 * Time: 15:43
 * To change this template use File | Settings | File Templates.
 */
public class IdxNodeAccessorTest {
  /*@Test(expected=NodeNoFound.class)
  public void testNoExistNode() throws NodeNoFound, NodeAlreadyExist, CorruptNode {
     String pathToNode = "z:/NoExist";
     IdxNodeAccessor accessor = IdxNodeAccessor.of(pathToNode);
  }

  @Test
  public void testThrowCtr() throws NodeAlreadyExist, CorruptNode {
    String pathToNode = "zd:/";

    // Если несколько блоков try-catch, то чтобы можно было видеть объекты ссыкли нужно создать
    //   вне блоков try. Тогда все-таки нужно использовать Optional. Если внутри блока, то тоже наверное
    //   Проблема в том, что пророй конструкторы могут генерировать исключения.
    Optional<IdxNodeAccessor> accessor = Optional.absent();
    try {
      accessor = Optional.of(IdxNodeAccessor.of(pathToNode));
    } catch (NodeNoFound e) {
      assertEquals(accessor, Optional.absent());
    }
  }
  */
  @Test
  public void testNodeRight() throws NodeNoFound, NodeAlreadyExist, NodeIsCorrupted {
    String pathToNode = "D:\\app_folder\\bec-node";
    Optional<ImmutableNodeMeansOfAccess> accessor = Optional.absent();
    try {
      accessor = Optional.of(IdxNodeAccessor.createImmutableConnection(pathToNode));
      //Util.print(accessor.get().getDistribution());

    } catch (NodeNoFound e) {
      assertEquals(accessor, Optional.absent());
    }
  }
}
