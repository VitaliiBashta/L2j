package com.l2jserver.datapack.handlers.effecthandlers.instant;

import com.l2jserver.gameserver.model.CharEffectList;
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
class InstantDispelByNameTest {

  private static final int SKILL_ID = 4342;

  @Mock private BuffInfo buffInfo;
  @Mock private L2Character effected;
  @Mock private CharEffectList effectList;

  private static InstantDispelByName effect;

  @BeforeAll
  static void init() {
    final var set = new StatsSet(Map.of("name", "InstantDispelByName"));
    final var params = new StatsSet(Map.of("id", SKILL_ID));
    effect = new InstantDispelByName(null, null, set, params);
  }

  @Test
  void test_null_effected() {
    assertDoesNotThrow(() -> effect.onStart(buffInfo));
  }

  @Test
  void test_effect_dispel() {
    when(buffInfo.getEffected()).thenReturn(effected);
    when(effected.getEffectList()).thenReturn(effectList);

    assertDoesNotThrow(() -> effect.onStart(buffInfo));
  }
}
