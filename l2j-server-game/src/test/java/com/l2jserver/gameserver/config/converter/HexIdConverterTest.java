package com.l2jserver.gameserver.config.converter;

import org.aeonbits.owner.Converter;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigInteger;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HexIdConverterTest {

  private static final String BIG_NUMBER = "-1eeb34fce0c64b610338d1269d8cfea4";
  private final Converter<BigInteger> CONVERTER = new HexIdConverter();

  private static Stream<Arguments> provideKeyValues() {
    return Stream.of(Arguments.of(BIG_NUMBER, new BigInteger(BIG_NUMBER, 16)));
  }

  @ParameterizedTest
  @MethodSource("provideKeyValues")
  void convertTest(String hexId, BigInteger expected) {
    assertEquals(CONVERTER.convert(null, hexId), expected);
  }
}
