package info_core_accessors;

/**
 * Created with IntelliJ IDEA.
 * User: кей
 * Date: 31.08.13
 * Time: 18:49
 * To change this template use File | Settings | File Templates.
 */
public class FabricImmutableNodeAccessors implements IFabricImmutableNodeAccessors {
  @Override
  public ImmutableNodeAccessor create(String pathToNode) throws NodeNoFound, NodeIsCorrupted {
    return IdxNodeAccessor.createImmutableConnection(pathToNode);
  }
}
