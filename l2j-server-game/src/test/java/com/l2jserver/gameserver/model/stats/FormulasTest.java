package com.l2jserver.gameserver.model.stats;

import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.skills.Skill;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static com.l2jserver.gameserver.enums.ShotType.BLESSED_SPIRITSHOTS;
import static com.l2jserver.gameserver.enums.ShotType.SPIRITSHOTS;
import static java.lang.Double.NaN;
import static java.lang.Double.POSITIVE_INFINITY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FormulasTest {

  private static final int HP_REGENERATE_PERIOD_CHARACTER = 3000;

  private static final int HP_REGENERATE_PERIOD_DOOR = 300000;

  @Mock(lenient = true)
  private L2Character character;

  @Mock(lenient = true)
  private Skill skill;

  private static Stream<Arguments> provide() {
    // TODO(Zoey76): Take care of the "bad" values.
    return Stream.of(
        Arguments.of(0, true, 1, false, false, 0, 0.0, false, false, 0.0),
        Arguments.of(0, true, 0, false, false, 0, 0.0, false, false, NaN),
        Arguments.of(0, false, 1, false, false, 0, 0.0, false, false, NaN),
        Arguments.of(0, false, 0, false, true, 500, 0.0, false, false, 0.0),
        Arguments.of(600, false, 0, false, true, 500, 0.0, false, false, 500.0),
        Arguments.of(3000, false, 0, false, true, 600, 0.0, false, false, 1665.0),
        Arguments.of(0, false, 0, false, false, 0, 500.0, false, false, 0.0),
        Arguments.of(600, false, 0, false, false, 0, 500.0, false, false, 500.),
        Arguments.of(3000, false, 0, false, false, 0, 600.0, false, false, 1665.0),
        Arguments.of(1400, false, 0, false, true, 0, 0.0, true, false, POSITIVE_INFINITY),
        Arguments.of(1400, false, 0, false, true, 0, 0.0, false, true, POSITIVE_INFINITY),
        Arguments.of(1400, false, 0, true, true, 0, 0.0, true, false, 840.0),
        Arguments.of(1400, false, 0, true, true, 0, 0.0, false, true, 840.0));
  }

  @Test
  void test_get_regenerate_period() {
    when(character.isDoor()).thenReturn(false);

    assertEquals(HP_REGENERATE_PERIOD_CHARACTER, Formulas.getRegeneratePeriod(character));
  }

  @Test
  void test_get_regenerate_period_door() {
    when(character.isDoor()).thenReturn(true);

    assertEquals(HP_REGENERATE_PERIOD_DOOR, Formulas.getRegeneratePeriod(character));
  }

  @ParameterizedTest
  @MethodSource("provide")
  void test_calculate_cast_time(
      int hitTime,
      boolean isChanneling,
      int channelingSkillId,
      boolean isStatic,
      boolean isMagic, //
      int mAtkSpeed,
      double pAtkSpeed,
      boolean isChargedSpiritshots,
      boolean isChargedBlessedSpiritShots,
      double expected) {
    when(character.getMAtkSpd()).thenReturn(mAtkSpeed);
    when(character.getPAtkSpd()).thenReturn(pAtkSpeed);
    when(character.isChargedShot(SPIRITSHOTS)).thenReturn(isChargedSpiritshots);
    when(character.isChargedShot(BLESSED_SPIRITSHOTS)).thenReturn(isChargedBlessedSpiritShots);
    when(skill.getHitTime()).thenReturn(hitTime);
    when(skill.isChanneling()).thenReturn(isChanneling);
    when(skill.getChannelingSkillId()).thenReturn(channelingSkillId);
    when(skill.isStatic()).thenReturn(isStatic);
    when(skill.isMagic()).thenReturn(isMagic);

    assertEquals(expected, Formulas.calcCastTime(character, skill));
  }
}
