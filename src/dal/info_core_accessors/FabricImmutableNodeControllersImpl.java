package dal.info_core_accessors;

public class FabricImmutableNodeControllersImpl implements FabricImmutableNodeControllers {
  @Override
  public ImmutableNodeAccessor create(String pathToNode) throws NodeNoFound, NodeIsCorrupted {
    return IdxNodeAccessor.createImmutableConnection(pathToNode);
  }
}
