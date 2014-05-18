package DbC;

// https://code.google.com/p/cofoja/wiki/AddContracts

import com.google.java.contract.Invariant;
import com.google.java.contract.Requires;

@Invariant("size() >= 0")  // step 2
interface Stack<T> {
  public int size();  // step 1

  @Requires("size() >= 1")  // step 3
  public T pop();
  public void push(T obj);
}