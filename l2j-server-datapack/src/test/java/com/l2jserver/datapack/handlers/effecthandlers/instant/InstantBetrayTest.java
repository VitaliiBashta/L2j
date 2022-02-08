package com.l2jserver.datapack.handlers.effecthandlers.instant;

import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.skills.BuffInfo;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InstantBetrayTest {

  @Mock private BuffInfo buffInfo;
  @Mock private L2Character effected;

  private static InstantBetray effect;

  @BeforeAll
  static void init() {
    final var set = new StatsSet(Map.of("name", "InstantBetray"));
    final var params = new StatsSet(Map.of("chance", "80", "time", "30"));
    effect = new InstantBetray(null, null, set, params);
  }

  @Test
  void test_null_effected() {
    assertDoesNotThrow(() ->effect.onStart(buffInfo));
  }

  @Test
  void test_effected_is_raid() {
    when(buffInfo.getEffected()).thenReturn(effected);
    when(effected.isRaid()).thenReturn(true);

    assertDoesNotThrow(() ->effect.onStart(buffInfo));
  }

  @Test
  void test_effected_not_servitor_summon_raid_minion() {
    when(buffInfo.getEffected()).thenReturn(effected);
    when(effected.isRaid()).thenReturn(false);
    when(effected.isServitor()).thenReturn(false);
    when(effected.isSummon()).thenReturn(false);
    when(effected.isRaidMinion()).thenReturn(false);

    assertDoesNotThrow(() ->effect.onStart(buffInfo));
  }

}
