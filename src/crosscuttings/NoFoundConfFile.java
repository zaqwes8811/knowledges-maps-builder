package crosscuttings;

/**
 * Created with IntelliJ IDEA.
 * User: кей
 * Date: 23.07.13
 * Time: 14:45
 * To change this template use File | Settings | File Templates.
 */
public class NoFoundConfFile extends Exception {
  public NoFoundConfFile(String msg) {
    super(msg);
  }
  public NoFoundConfFile(Exception e) {
    super(e);
  }

  // Если просто getM.. то открывает детали реализации!
  private String fileName;
  public void setFileName(String name) {
    fileName = name;
  }
  public String getFileName() {return fileName;}
}
