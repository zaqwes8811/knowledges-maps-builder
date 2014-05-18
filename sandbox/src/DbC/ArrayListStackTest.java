package DbC;

import org.junit.Test;

// On cofoja in IDEA http://java.dzone.com/articles/using-google-contracts-java

public class ArrayListStackTest {
  @Test
  public void testPop() throws Exception {
    Stack<Integer> stack = new ArrayListStack<Integer>();
    stack.pop();
  }
}
