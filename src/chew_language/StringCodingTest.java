package chew_language;

import org.junit.Test;

public class StringCodingTest {

  @Test
  public void testCharBuffer() {
    char[] a = {'h', 'e', 'l', 'l', 'o', ' ', 'w', 'o', 'r', 'l', 'd'};
    String b = new String(a);
    assert b.equals("hello world");
  }
}
