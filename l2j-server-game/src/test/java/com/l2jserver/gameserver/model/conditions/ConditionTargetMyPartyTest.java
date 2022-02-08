package com.l2jserver.gameserver.model.conditions;

import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.skills.Skill;
import com.l2jserver.gameserver.network.serverpackets.SystemMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.l2jserver.gameserver.network.SystemMessageId.CANNOT_USE_ON_YOURSELF;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConditionTargetMyPartyTest {

  private final ConditionTargetMyParty conditionIncludeMe =
      new ConditionTargetMyParty("INCLUDE_ME");
  private final ConditionTargetMyParty conditionExceptMe = new ConditionTargetMyParty("EXCEPT_ME");
  @Mock private Skill skill;
  @Mock private L2Character effector;
  @Mock private L2Character effected;
  @Mock private L2PcInstance player;
  @Mock private L2PcInstance otherPlayer;

  @Test
  void test_null_player() {
    assertFalse(conditionIncludeMe.testImpl(effector, effected, skill, null));
  }

  @Test
  void test_self_target_exclude_me() {
    when(effector.getActingPlayer()).thenReturn(player);
    effector.sendPacket(CANNOT_USE_ON_YOURSELF);

    assertFalse(conditionExceptMe.testImpl(effector, player, skill, null));
  }

  @Test
  void test_player_in_party_target_not_in_party() {
    when(effector.getActingPlayer()).thenReturn(player);
    when(player.isInParty()).thenReturn(true);
    when(player.isInPartyWith(effected)).thenReturn(false);
    effector.sendPacket(any(SystemMessage.class));

    assertFalse(conditionIncludeMe.testImpl(effector, effected, skill, null));
  }

  @Test
  void test_player_in_party_with_target() {
    when(effector.getActingPlayer()).thenReturn(player);
    when(player.isInParty()).thenReturn(true);
    when(player.isInPartyWith(effected)).thenReturn(true);

    assertTrue(conditionIncludeMe.testImpl(effector, effected, skill, null));
  }

  @Test
  void test_player_not_in_party_target_not_player_or_player_summon() {
    when(effector.getActingPlayer()).thenReturn(player);
    when(player.isInParty()).thenReturn(false);
    when(effected.getActingPlayer()).thenReturn(otherPlayer);
    effector.sendPacket(any(SystemMessage.class));

    assertFalse(conditionIncludeMe.testImpl(effector, effected, skill, null));
  }

  @Test
  void test_player_in_party_target_player_or_player_summon() {
    when(effector.getActingPlayer()).thenReturn(player);
    when(player.isInParty()).thenReturn(false);
    when(effected.getActingPlayer()).thenReturn(player);

    assertTrue(conditionIncludeMe.testImpl(effector, effected, skill, null));
  }
}
