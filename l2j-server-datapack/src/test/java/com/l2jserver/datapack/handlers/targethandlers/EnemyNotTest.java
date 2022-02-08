package com.l2jserver.datapack.handlers.targethandlers;

import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.skills.Skill;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.l2jserver.gameserver.handler.ITargetTypeHandler.EMPTY_TARGET_LIST;
import static com.l2jserver.gameserver.network.SystemMessageId.INCORRECT_TARGET;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EnemyNotTest {

  @Mock private Skill skill;
  @Mock private L2Character activeChar;
  @Mock private L2Character target;

  private final EnemyNot handler = new EnemyNot();

  @Test
  void test_target_null() {
    assertEquals(EMPTY_TARGET_LIST, handler.getTargetList(skill, activeChar, false, null));
  }

  @Test
  void test_target_is_dead() {
    when(target.isDead()).thenReturn(true);
    activeChar.sendPacket(INCORRECT_TARGET);

    assertEquals(EMPTY_TARGET_LIST, handler.getTargetList(skill, activeChar, false, target));
  }

  @Test
  void test_target_is_autoattackable() {
    when(target.isDead()).thenReturn(false);
    when(target.isAutoAttackable(activeChar)).thenReturn(true);
    activeChar.sendPacket(INCORRECT_TARGET);

    assertEquals(EMPTY_TARGET_LIST, handler.getTargetList(skill, activeChar, false, target));
  }
}
