package through_functional.configurator;

// Сделать уточнения, хотя бы перелить в какой строке.
public class ConfFileIsCorrupted extends Exception {
  public final String WHAT_HAPPENED;
  public ConfFileIsCorrupted(Exception e) {
    super(e);
    WHAT_HAPPENED = e.getMessage();
  }

  public ConfFileIsCorrupted(Exception e, String whatHappened) {
    super(e);
    WHAT_HAPPENED = whatHappened;
  }
}
