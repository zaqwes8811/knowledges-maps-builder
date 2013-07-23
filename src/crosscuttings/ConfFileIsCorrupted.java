package crosscuttings;

/**
 * Created with IntelliJ IDEA.
 * User: кей
 * Date: 23.07.13
 * Time: 15:06
 * To change this template use File | Settings | File Templates.
 */
public class ConfFileIsCorrupted extends Exception {
  public ConfFileIsCorrupted(Exception e) {
    super(e);
  }
}
