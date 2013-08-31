package idx_coursors;

/**
 * Created with IntelliJ IDEA.
 * User: кей
 * Date: 31.08.13
 * Time: 18:45
 * To change this template use File | Settings | File Templates.
 */
public interface IFabricImmutableNodeAccessors {
  public ImmutableNodeAccessor create(String pathToNode) throws NodeNoFound, NodeIsCorrupted;
}
