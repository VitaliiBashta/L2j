package com.l2jserver.datapack.handlers.targethandlers;

import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.skills.Skill;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.l2jserver.gameserver.handler.ITargetTypeHandler.EMPTY_TARGET_LIST;
import static com.l2jserver.gameserver.model.skills.targets.AffectScope.NONE;
import static com.l2jserver.gameserver.model.skills.targets.AffectScope.SINGLE;
import static com.l2jserver.gameserver.model.zone.ZoneId.PVP;
import static com.l2jserver.gameserver.network.SystemMessageId.INCORRECT_TARGET;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EnemyOnlyTest {

  @Mock private Skill skill;
  @Mock private L2Character activeChar;
  @Mock private L2Character target;
  @Mock private L2PcInstance player;
  @Mock private L2PcInstance targetPlayer;

  private final EnemyOnly enemyOnly = new EnemyOnly();

  @Test
  void test_invalid_affect_scope_should_return_empty_target_list() {
    when(skill.getAffectScope()).thenReturn(NONE);

    final var actual = enemyOnly.getTargetList(skill, activeChar, false, target);
    assertEquals(EMPTY_TARGET_LIST, actual);
  }

  @Test
  void test_null_target_should_return_empty_target_list() {
    when(skill.getAffectScope()).thenReturn(SINGLE);

    final var actual = enemyOnly.getTargetList(skill, activeChar, false, null);
    assertEquals(EMPTY_TARGET_LIST, actual);
  }

  @Test
  void test_self_target_should_return_empty_target_list_with_invalid_target_message() {
    when(skill.getAffectScope()).thenReturn(SINGLE);
    when(target.getObjectId()).thenReturn(1);
    when(activeChar.getObjectId()).thenReturn(1);
    activeChar.sendPacket(INCORRECT_TARGET);

    final var actual = enemyOnly.getTargetList(skill, activeChar, false, target);
    assertEquals(EMPTY_TARGET_LIST, actual);
  }

  @Test
  void test_dead_target_should_return_empty_target_list_with_invalid_target_message() {
    when(skill.getAffectScope()).thenReturn(SINGLE);
    when(target.getObjectId()).thenReturn(1);
    when(activeChar.getObjectId()).thenReturn(2);
    when(target.isDead()).thenReturn(true);
    activeChar.sendPacket(INCORRECT_TARGET);

    final var actual = enemyOnly.getTargetList(skill, activeChar, false, target);
    assertEquals(EMPTY_TARGET_LIST, actual);
  }

  @Test
  void test_non_attackable_target_should_return_empty_target_list_with_invalid_target_message() {
    when(skill.getAffectScope()).thenReturn(SINGLE);
    when(target.getObjectId()).thenReturn(1);
    when(activeChar.getObjectId()).thenReturn(2);
    when(target.isDead()).thenReturn(false);
    when(target.isNpc()).thenReturn(true);
    when(target.isAttackable()).thenReturn(false);
    activeChar.sendPacket(INCORRECT_TARGET);

    final var actual = enemyOnly.getTargetList(skill, activeChar, false, target);
    assertEquals(EMPTY_TARGET_LIST, actual);
  }

  @Test
  void test_attackable_target_should_return_target() {
    when(skill.getAffectScope()).thenReturn(SINGLE);
    when(target.getObjectId()).thenReturn(1);
    when(activeChar.getObjectId()).thenReturn(2);
    when(target.isDead()).thenReturn(false);
    when(target.isNpc()).thenReturn(true);
    when(target.isAttackable()).thenReturn(true);

    final var actual = enemyOnly.getTargetList(skill, activeChar, false, target);
    assertEquals(target, actual.get(0));
  }

  @Test
  void test_null_player_should_return_empty_target_list() {
    when(skill.getAffectScope()).thenReturn(SINGLE);
    when(target.getObjectId()).thenReturn(1);
    when(activeChar.getObjectId()).thenReturn(2);
    when(target.isDead()).thenReturn(false);
    when(target.isNpc()).thenReturn(false);
    when(activeChar.getActingPlayer()).thenReturn(null);

    final var actual = enemyOnly.getTargetList(skill, activeChar, false, target);
    assertEquals(EMPTY_TARGET_LIST, actual);
  }

  @Test
  void test_player_in_olympiad_should_return_target_if_target_is_on_the_other_side() {
    when(skill.getAffectScope()).thenReturn(SINGLE);
    when(target.getObjectId()).thenReturn(1);
    when(activeChar.getObjectId()).thenReturn(2);
    when(target.isDead()).thenReturn(false);
    when(target.isNpc()).thenReturn(false);
    when(activeChar.getActingPlayer()).thenReturn(player);
    when(player.isInOlympiadMode()).thenReturn(true);
    when(target.getActingPlayer()).thenReturn(targetPlayer);
    when(player.getOlympiadSide()).thenReturn(0);
    when(targetPlayer.getOlympiadSide()).thenReturn(1);

    final var actual = enemyOnly.getTargetList(skill, activeChar, false, target);
    assertEquals(target, actual.get(0));
  }

  @Test
  void test_player_in_olympiad_should_return_empty_target_list_if_target_is_on_the_same_side() {
    when(skill.getAffectScope()).thenReturn(SINGLE);
    when(target.getObjectId()).thenReturn(1);
    when(activeChar.getObjectId()).thenReturn(2);
    when(target.isDead()).thenReturn(false);
    when(target.isNpc()).thenReturn(false);
    when(activeChar.getActingPlayer()).thenReturn(player);
    when(player.isInOlympiadMode()).thenReturn(true);
    when(target.getActingPlayer()).thenReturn(targetPlayer);
    when(player.getOlympiadSide()).thenReturn(0);
    when(targetPlayer.getOlympiadSide()).thenReturn(0);
    player.sendPacket(INCORRECT_TARGET);

    final var actual = enemyOnly.getTargetList(skill, activeChar, false, target);
    assertEquals(EMPTY_TARGET_LIST, actual);
  }

  @Test
  void test_player_in_party_with_target_should_return_empty_target_list() {
    when(skill.getAffectScope()).thenReturn(SINGLE);
    when(target.getObjectId()).thenReturn(1);
    when(activeChar.getObjectId()).thenReturn(2);
    when(target.isDead()).thenReturn(false);
    when(target.isNpc()).thenReturn(false);
    when(activeChar.getActingPlayer()).thenReturn(player);
    when(player.isInOlympiadMode()).thenReturn(false);
    when(player.isInDuelWith(target)).thenReturn(false);
    when(player.isInPartyWith(target)).thenReturn(true);
    player.sendPacket(INCORRECT_TARGET);

    final var actual = enemyOnly.getTargetList(skill, activeChar, false, target);
    assertEquals(EMPTY_TARGET_LIST, actual);
  }

  @Test
  void test_player_in_pvp_zone_should_return_target() {
    when(skill.getAffectScope()).thenReturn(SINGLE);
    when(target.getObjectId()).thenReturn(1);
    when(activeChar.getObjectId()).thenReturn(2);
    when(target.isDead()).thenReturn(false);
    when(target.isNpc()).thenReturn(false);
    when(activeChar.getActingPlayer()).thenReturn(player);
    when(player.isInOlympiadMode()).thenReturn(false);
    when(player.isInDuelWith(target)).thenReturn(false);
    when(player.isInPartyWith(target)).thenReturn(false);
    when(player.isInsideZone(PVP)).thenReturn(true);

    final var actual = enemyOnly.getTargetList(skill, activeChar, false, target);
    assertEquals(target, actual.get(0));
  }

  @Test
  void test_player_in_clan_with_target_should_return_empty_target_list() {
    when(skill.getAffectScope()).thenReturn(SINGLE);
    when(target.getObjectId()).thenReturn(1);
    when(activeChar.getObjectId()).thenReturn(2);
    when(target.isDead()).thenReturn(false);
    when(target.isNpc()).thenReturn(false);
    when(activeChar.getActingPlayer()).thenReturn(player);
    when(player.isInOlympiadMode()).thenReturn(false);
    when(player.isInDuelWith(target)).thenReturn(false);
    when(player.isInPartyWith(target)).thenReturn(false);
    when(player.isInsideZone(PVP)).thenReturn(false);
    when(player.isInClanWith(target)).thenReturn(true);
    player.sendPacket(INCORRECT_TARGET);

    final var actual = enemyOnly.getTargetList(skill, activeChar, false, target);
    assertEquals(EMPTY_TARGET_LIST, actual);
  }

  @Test
  void test_player_in_alliance_with_target_should_return_empty_target_list() {
    when(skill.getAffectScope()).thenReturn(SINGLE);
    when(target.getObjectId()).thenReturn(1);
    when(activeChar.getObjectId()).thenReturn(2);
    when(target.isDead()).thenReturn(false);
    when(target.isNpc()).thenReturn(false);
    when(activeChar.getActingPlayer()).thenReturn(player);
    when(player.isInOlympiadMode()).thenReturn(false);
    when(player.isInDuelWith(target)).thenReturn(false);
    when(player.isInPartyWith(target)).thenReturn(false);
    when(player.isInsideZone(PVP)).thenReturn(false);
    when(player.isInClanWith(target)).thenReturn(false);
    when(player.isInAllyWith(target)).thenReturn(true);
    player.sendPacket(INCORRECT_TARGET);

    final var actual = enemyOnly.getTargetList(skill, activeChar, false, target);
    assertEquals(EMPTY_TARGET_LIST, actual);
  }

  @Test
  void test_player_in_command_channel_with_target_should_return_empty_target_list() {
    when(skill.getAffectScope()).thenReturn(SINGLE);
    when(target.getObjectId()).thenReturn(1);
    when(activeChar.getObjectId()).thenReturn(2);
    when(target.isDead()).thenReturn(false);
    when(target.isNpc()).thenReturn(false);
    when(activeChar.getActingPlayer()).thenReturn(player);
    when(player.isInOlympiadMode()).thenReturn(false);
    when(player.isInDuelWith(target)).thenReturn(false);
    when(player.isInPartyWith(target)).thenReturn(false);
    when(player.isInsideZone(PVP)).thenReturn(false);
    when(player.isInClanWith(target)).thenReturn(false);
    when(player.isInAllyWith(target)).thenReturn(false);
    when(player.isInCommandChannelWith(target)).thenReturn(true);
    player.sendPacket(INCORRECT_TARGET);

    final var actual = enemyOnly.getTargetList(skill, activeChar, false, target);
    assertEquals(EMPTY_TARGET_LIST, actual);
  }

  @Test
  void test_player_same_siege_side_than_target_should_return_empty_target_list() {
    when(skill.getAffectScope()).thenReturn(SINGLE);
    when(target.getObjectId()).thenReturn(1);
    when(activeChar.getObjectId()).thenReturn(2);
    when(target.isDead()).thenReturn(false);
    when(target.isNpc()).thenReturn(false);
    when(activeChar.getActingPlayer()).thenReturn(player);
    when(player.isInOlympiadMode()).thenReturn(false);
    when(player.isInDuelWith(target)).thenReturn(false);
    when(player.isInPartyWith(target)).thenReturn(false);
    when(player.isInsideZone(PVP)).thenReturn(false);
    when(player.isInClanWith(target)).thenReturn(false);
    when(player.isInAllyWith(target)).thenReturn(false);
    when(player.isInCommandChannelWith(target)).thenReturn(false);
    when(player.isOnSameSiegeSideWith(target)).thenReturn(true);
    player.sendPacket(INCORRECT_TARGET);

    final var actual = enemyOnly.getTargetList(skill, activeChar, false, target);
    assertEquals(EMPTY_TARGET_LIST, actual);
  }

  @Test
  void test_player_at_clan_war_with_target_should_return_target() {
    when(skill.getAffectScope()).thenReturn(SINGLE);
    when(target.getObjectId()).thenReturn(1);
    when(activeChar.getObjectId()).thenReturn(2);
    when(target.isDead()).thenReturn(false);
    when(target.isNpc()).thenReturn(false);
    when(activeChar.getActingPlayer()).thenReturn(player);
    when(player.isInOlympiadMode()).thenReturn(false);
    when(player.isInDuelWith(target)).thenReturn(false);
    when(player.isInPartyWith(target)).thenReturn(false);
    when(player.isInsideZone(PVP)).thenReturn(false);
    when(player.isInClanWith(target)).thenReturn(false);
    when(player.isInAllyWith(target)).thenReturn(false);
    when(player.isInCommandChannelWith(target)).thenReturn(false);
    when(player.isOnSameSiegeSideWith(target)).thenReturn(false);
    when(player.isAtWarWith(target)).thenReturn(true);

    final var actual = enemyOnly.getTargetList(skill, activeChar, false, target);
    assertEquals(target, actual.get(0));
  }

  @Test
  void test_player_cannot_pvp_target_should_return_empty_target_list() {
    when(skill.getAffectScope()).thenReturn(SINGLE);
    when(target.getObjectId()).thenReturn(1);
    when(activeChar.getObjectId()).thenReturn(2);
    when(target.isDead()).thenReturn(false);
    when(target.isNpc()).thenReturn(false);
    when(activeChar.getActingPlayer()).thenReturn(player);
    when(player.isInOlympiadMode()).thenReturn(false);
    when(player.isInDuelWith(target)).thenReturn(false);
    when(player.isInPartyWith(target)).thenReturn(false);
    when(player.isInsideZone(PVP)).thenReturn(false);
    when(player.isInClanWith(target)).thenReturn(false);
    when(player.isInAllyWith(target)).thenReturn(false);
    when(player.isInCommandChannelWith(target)).thenReturn(false);
    when(player.isOnSameSiegeSideWith(target)).thenReturn(false);
    when(player.isAtWarWith(target)).thenReturn(false);
    when(player.checkIfPvP(target)).thenReturn(false);
    player.sendPacket(INCORRECT_TARGET);

    final var actual = enemyOnly.getTargetList(skill, activeChar, false, target);
    assertEquals(EMPTY_TARGET_LIST, actual);
  }

  @Test
  void test_player_can_pvp_target_should_return_target() {
    when(skill.getAffectScope()).thenReturn(SINGLE);
    when(target.getObjectId()).thenReturn(1);
    when(activeChar.getObjectId()).thenReturn(2);
    when(target.isDead()).thenReturn(false);
    when(target.isNpc()).thenReturn(false);
    when(activeChar.getActingPlayer()).thenReturn(player);
    when(player.isInOlympiadMode()).thenReturn(false);
    when(player.isInDuelWith(target)).thenReturn(false);
    when(player.isInPartyWith(target)).thenReturn(false);
    when(player.isInsideZone(PVP)).thenReturn(false);
    when(player.isInClanWith(target)).thenReturn(false);
    when(player.isInAllyWith(target)).thenReturn(false);
    when(player.isInCommandChannelWith(target)).thenReturn(false);
    when(player.isOnSameSiegeSideWith(target)).thenReturn(false);
    when(player.isAtWarWith(target)).thenReturn(false);
    when(player.checkIfPvP(target)).thenReturn(true);

    final var actual = enemyOnly.getTargetList(skill, activeChar, false, target);
    assertEquals(target, actual.get(0));
  }
}
