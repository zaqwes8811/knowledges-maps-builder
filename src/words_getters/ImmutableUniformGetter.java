package words_getters;

import com.google.common.base.Optional;
import parsers.ImmutableBECParser;

/**
 */
public class ImmutableUniformGetter {

  // Да, лучше передать, тогда будет Стратегией?
  public ImmutableUniformGetter(Optional<ImmutableBECParser> parser) {
    BEC_PARSER = parser;
  }

  private final Optional<ImmutableBECParser> BEC_PARSER;

  public static void main(String [] args) {

  }
}
