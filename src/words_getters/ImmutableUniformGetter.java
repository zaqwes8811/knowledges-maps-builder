package words_getters;

import com.google.common.base.Optional;
import parsers.ImmutableBECParser;

/**
 */
public class ImmutableUniformGetter {
  public ImmutableUniformGetter(Optional<ImmutableBECParser> parser) {
    BEC_PARSER = parser;
  }

  private final Optional<ImmutableBECParser> BEC_PARSER;

  public static void main(String [] args) {

  }
}
