package idx_coursors;

import com.google.common.base.Optional;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class IdxNodeAccessorTest {
  @Test
  public void testNodeRight() throws NodeNoFound, NodeAlreadyExist, NodeIsCorrupted {
    String pathToNode = "./app-folder/bec-node";
    Optional<ImmutableNodeAccessor> accessor = Optional.absent();
    try {
      accessor = Optional.of(IdxNodeAccessor.createImmutableConnection(pathToNode));
      assertEquals(false, accessor.equals(Optional.absent()));
    } catch (NodeNoFound e) {
      assertEquals(accessor, Optional.absent());
    }
  }
}
