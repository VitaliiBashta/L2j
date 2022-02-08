package com.l2jserver.datapack.handlers.effecthandlers.instant;

import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.L2Summon;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
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
class InstantDespawnTest {

  private static final int CHANCE = 75;

  @Mock private BuffInfo buffInfo;
  @Mock private L2Character effected;
  @Mock private L2PcInstance player;
  @Mock private L2Summon summon;

  private static InstantDespawn effect;

  @BeforeAll
  static void init() {
    var set = new StatsSet(Map.of("name", "InstantDespawn"));
    var params = new StatsSet(Map.of("chance", CHANCE));
    effect = new InstantDespawn(null, null, set, params);
  }

  @Test
  void test_null_effected() {
    assertDoesNotThrow(() -> effect.onStart(buffInfo));
  }

  @Test
  void test_null_player() {
    when(buffInfo.getEffected()).thenReturn(effected);
    when(effected.getActingPlayer()).thenReturn(null);

    assertDoesNotThrow(() -> effect.onStart(buffInfo));
  }

  @Test
  void test_null_summon() {
    when(buffInfo.getEffected()).thenReturn(effected);
    when(effected.getActingPlayer()).thenReturn(player);
    when(player.getSummon()).thenReturn(null);

    assertDoesNotThrow(() -> effect.onStart(buffInfo));
  }

  @Test
  void test_chance_fail() {
    when(buffInfo.getEffected()).thenReturn(effected);
    when(effected.getActingPlayer()).thenReturn(player);
    when(player.getSummon()).thenReturn(summon);

    assertDoesNotThrow(() -> effect.onStart(buffInfo));
  }

  @Test
  void test_chance_success() {
    when(buffInfo.getEffected()).thenReturn(effected);
    when(effected.getActingPlayer()).thenReturn(player);
    when(player.getSummon()).thenReturn(summon);

    assertDoesNotThrow(() -> effect.onStart(buffInfo));
  }
}
