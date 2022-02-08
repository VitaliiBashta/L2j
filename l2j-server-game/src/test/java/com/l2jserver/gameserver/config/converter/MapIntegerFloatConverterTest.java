package com.l2jserver.gameserver.config.converter;

import org.aeonbits.owner.Converter;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MapIntegerFloatConverterTest {

  private static final Converter<Map<Integer, Float>> CONVERTER = new MapIntegerFloatConverter();

  private static Object[][] provideKeyValues() {
    return new Object[][] {
      {
        "264,3600;265,3600;266,3600;267,3600",
        Map.of(264, 3600.0F, 265, 3600.0F, 266, 3600.0F, 267, 3600.0F)
      },
      {
        "264, 3600; 265, 3600; 266, 3600; 267, 3600",
        Map.of(264, 3600.0F, 265, 3600.0F, 266, 3600.0F, 267, 3600.0F)
      },
      {"", Map.of()},
      {null, Map.of()}
    };
  }

  @ParameterizedTest
  @MethodSource("provideKeyValues")
  void convertTest(String keyValues, Map<Integer, Float> expected) {
    assertEquals(CONVERTER.convert(null, keyValues), expected);
  }
}
