package com.l2jserver.gameserver.config.converter;

import com.l2jserver.gameserver.model.holders.ItemHolder;
import org.aeonbits.owner.Converter;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ItemHolderConverterTest {

  private static final Converter<ItemHolder> CONVERTER = new ItemHolderConverter();

  private static Object[][] provideItems() {
    return new Object[][] {
      {"57,100000", 57, 100000},
      {"57,12345678910", 57, 12345678910L}
    };
  }

  @ParameterizedTest
  @MethodSource("provideItems")
  void convertTest(String input, int id, long count) {
    final var result = CONVERTER.convert(null, input);
    assertEquals(result.getId(), id);
    assertEquals(result.getCount(), count);
  }
}
