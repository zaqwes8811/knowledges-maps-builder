package through_functional;

// Очень общее. Не ясно что произошло.
public class CrosscuttingsException extends RuntimeException {
  public CrosscuttingsException(Exception e) {
    super(e);
  }

  public CrosscuttingsException() {
    super();
  }
}
