package com.l2jserver.gameserver.config.converter;

import com.l2jserver.gameserver.model.holders.SkillHolder;
import org.aeonbits.owner.Converter;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SkillHolderConverterTest {

  private static final Converter<SkillHolder> CONVERTER = new SkillHolderConverter();

  private static Object[][] provideSkills() {
    return new Object[][] {
      {"1504,1", 1504, 1},
      {"1499,10", 1499, 10}
    };
  }

  @ParameterizedTest
  @MethodSource("provideSkills")
  void convertTest(String input, int id, int level) {
    final var result = CONVERTER.convert(null, input);
    assertEquals(result.getSkillId(), id);
    assertEquals(result.getSkillLvl(), level);
  }
}
