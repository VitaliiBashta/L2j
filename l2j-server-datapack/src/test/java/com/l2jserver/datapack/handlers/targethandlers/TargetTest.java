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
import static com.l2jserver.gameserver.network.SystemMessageId.INCORRECT_TARGET;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TargetTest {

  @Mock private Skill skill;
  @Mock private L2Character activeChar;
  @Mock private L2Character target;
  @Mock private L2PcInstance player;
  @Mock private SkillUseHolder skillUse;

  private final Target handler = new Target();

  @Test
  void test_target_null() {
    assertEquals(EMPTY_TARGET_LIST, handler.getTargetList(skill, activeChar, false, null));
  }

  @Test
  void test_target_without_ctrl_target_autoattackable() {
    when(activeChar.getActingPlayer()).thenReturn(player);
    when(player.getCurrentSkill()).thenReturn(skillUse);
    when(skillUse.isCtrlPressed()).thenReturn(false);
    when(target.isAutoAttackable(activeChar)).thenReturn(true);
    activeChar.sendPacket(INCORRECT_TARGET);

    assertEquals(EMPTY_TARGET_LIST, handler.getTargetList(skill, activeChar, false, target));
  }
}
