package com.l2jserver.gameserver.config.converter;

import org.aeonbits.owner.Converter;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PatternConverterTest {

  private static final Converter<Pattern> CONVERTER = new PatternConverter();

  private static Object[][] providePatterns() {
    return new Object[][] {
      {"[A-Z][a-z]{3,3}[A-Za-z0-9]*", "OmfgWTF1", true},
      {"[A-Z][a-z]{3,3}[A-Za-z0-9]*", "", false},
      {"[A-Z][a-z]{3,3}[A-Za-z0-9]+", "", false},
      {"[a-zA-Z0-9]*", "Zoey76", true}
    };
  }

  @ParameterizedTest
  @MethodSource("providePatterns")
  void convertTest(String pattern, String text, boolean expected) {
    assertEquals(CONVERTER.convert(null, pattern).matcher(text).matches(), expected);
  }
}
