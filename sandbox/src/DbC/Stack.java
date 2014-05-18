package DbC;

// https://code.google.com/p/cofoja/wiki/AddContracts

import com.google.java.contract.Invariant;

@Invariant("size() >= 0")
interface Stack<T> {
  public int size();
  public T pop();
  public void push(T obj);
}