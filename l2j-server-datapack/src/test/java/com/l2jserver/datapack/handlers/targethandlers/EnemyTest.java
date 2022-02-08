package com.l2jserver.datapack.handlers.targethandlers;

import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.holders.SkillUseHolder;
import com.l2jserver.gameserver.model.skills.Skill;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.l2jserver.gameserver.handler.ITargetTypeHandler.EMPTY_TARGET_LIST;
import static com.l2jserver.gameserver.model.skills.targets.AffectScope.NONE;
import static com.l2jserver.gameserver.model.skills.targets.AffectScope.SINGLE;
import static com.l2jserver.gameserver.network.SystemMessageId.INCORRECT_TARGET;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EnemyTest {

  @Mock private Skill skill;
  @Mock private SkillUseHolder skillUseHolder;
  @Mock private L2Character activeChar;
  @Mock private L2Character target;
  @Mock private L2PcInstance player;

  private final Enemy enemy = new Enemy();

  @Test
  void test_invalid_affect_scope_should_return_empty_target_list() {
    when(skill.getAffectScope()).thenReturn(NONE);

    final var actual = enemy.getTargetList(skill, activeChar, false, target);
    assertEquals(EMPTY_TARGET_LIST, actual);
  }

  @Test
  void test_null_target_should_return_empty_target_list() {
    when(skill.getAffectScope()).thenReturn(SINGLE);

    final var actual = enemy.getTargetList(skill, activeChar, false, null);
    assertEquals(EMPTY_TARGET_LIST, actual);
  }

  @Test
  void test_dead_target_should_return_empty_target_list_with_invalid_target_message() {
    when(skill.getAffectScope()).thenReturn(SINGLE);
    when(target.isDead()).thenReturn(true);
    activeChar.sendPacket(INCORRECT_TARGET);

    final var actual = enemy.getTargetList(skill, activeChar, false, target);
    assertEquals(EMPTY_TARGET_LIST, actual);
  }

  @Test
  void test_attackable_target_should_return_target() {
    when(skill.getAffectScope()).thenReturn(SINGLE);
    when(target.isDead()).thenReturn(false);
    when(target.isAttackable()).thenReturn(true);

    final var actual = enemy.getTargetList(skill, activeChar, false, target);
    assertEquals(target, actual.get(0));
  }

  @Test
  void test_null_player_should_return_empty_target_list() {
    when(skill.getAffectScope()).thenReturn(SINGLE);
    when(target.isDead()).thenReturn(false);
    when(target.isAttackable()).thenReturn(false);
    when(activeChar.getActingPlayer()).thenReturn(null);

    final var actual = enemy.getTargetList(skill, activeChar, false, target);
    assertEquals(EMPTY_TARGET_LIST, actual);
  }

  @Test
  void test_player_cannot_pvp_target_and_no_ctrl_should_return_empty_target_list() {
    when(skill.getAffectScope()).thenReturn(SINGLE);
    when(target.isDead()).thenReturn(false);
    when(target.isAttackable()).thenReturn(false);
    when(activeChar.getActingPlayer()).thenReturn(player);
    when(player.checkIfPvP(target)).thenReturn(false);
    when(player.getCurrentSkill()).thenReturn(skillUseHolder);
    when(skillUseHolder.isCtrlPressed()).thenReturn(false);
    player.sendPacket(INCORRECT_TARGET);

    final var actual = enemy.getTargetList(skill, activeChar, false, target);
    assertEquals(EMPTY_TARGET_LIST, actual);
  }

  @Test
  void test_player_cannot_pvp_target_and_ctrl_should_return_target() {
    when(skill.getAffectScope()).thenReturn(SINGLE);
    when(target.isDead()).thenReturn(false);
    when(target.isAttackable()).thenReturn(false);
    when(activeChar.getActingPlayer()).thenReturn(player);
    when(player.checkIfPvP(target)).thenReturn(false);
    when(player.getCurrentSkill()).thenReturn(skillUseHolder);
    when(skillUseHolder.isCtrlPressed()).thenReturn(true);

    final var actual = enemy.getTargetList(skill, activeChar, false, target);
    assertEquals(target, actual.get(0));
  }

  @Test
  void test_player_can_pvp_target_should_return_target() {
    when(skill.getAffectScope()).thenReturn(SINGLE);
    when(target.isDead()).thenReturn(false);
    when(target.isAttackable()).thenReturn(false);
    when(activeChar.getActingPlayer()).thenReturn(player);
    when(player.checkIfPvP(target)).thenReturn(true);

    final var actual = enemy.getTargetList(skill, activeChar, false, target);
    assertEquals(target, actual.get(0));
  }
}
