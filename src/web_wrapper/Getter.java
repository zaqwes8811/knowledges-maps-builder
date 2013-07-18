package web_wrapper;

import java.lang.String;
import java.util.List;
import java.util.Map;

// Пусть пока только геттеры, не знаю как назвать нормально
public interface Getter {
  public List<Map<String, List<String>>> getPackage();
}