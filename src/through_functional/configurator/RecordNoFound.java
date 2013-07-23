package through_functional.configurator;

/**
 * Created with IntelliJ IDEA.
 * User: кей
 * Date: 23.07.13
 * Time: 15:16
 * To change this template use File | Settings | File Templates.
 */
public class RecordNoFound extends Exception {
  public RecordNoFound(Exception e, String requestedPath) {
    super(e);
    REQUESTED_PATH = requestedPath;
  }

  public final String REQUESTED_PATH;

}
