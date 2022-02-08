package com.l2jserver.gameserver.config.converter;

import com.l2jserver.gameserver.model.Location;
import org.aeonbits.owner.Converter;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LocationConverterTest {

	private static final Converter<Location> CONVERTER = new LocationConverter();

  private static Stream<Arguments> provideLocations() {
    return Stream.of(
        Arguments.of("83425,148585,-3406", new Location(83425, 148585, -3406)),
        Arguments.of("148695,46725,-3414,200", new Location(148695, 46725, -3414, 200)),
        Arguments.of("149999,46728,-3414,200,5000", new Location(149999, 46728, -3414, 200, 5000)));
  }

  @ParameterizedTest
  @MethodSource("provideLocations")
  void convertTest(String input, Location expected) {
    final var result = CONVERTER.convert(null, input);
    assertEquals(result, expected);
  }
}
