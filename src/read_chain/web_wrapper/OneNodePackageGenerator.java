package read_chain.web_wrapper;

import com.google.common.collect.ImmutableList;

/**
 * Created with IntelliJ IDEA.
 * User: кей
 * Date: 15.09.13
 * Time: 9:41
 * To change this template use File | Settings | File Templates.
 */
public interface OneNodePackageGenerator {
  // Index: нужно для маркеровки
  // Word: само слово
  // Translates:
  // Context:
  ImmutableList<ImmutableList<ImmutableList<String>>> getPackageActiveNode();

  //public static OneNodePackageGenerator create() { return null; }   // Будет знать о деталях реализации
}
