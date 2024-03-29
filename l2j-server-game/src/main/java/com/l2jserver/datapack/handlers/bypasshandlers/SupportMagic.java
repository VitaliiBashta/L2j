package com.l2jserver.datapack.handlers.bypasshandlers;

import com.l2jserver.gameserver.enums.CategoryType;
import com.l2jserver.gameserver.handler.IBypassHandler;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.holders.SkillHolder;
import org.springframework.stereotype.Service;

@Service
public class SupportMagic implements IBypassHandler {
  private static final String[] COMMANDS = {"supportmagicservitor", "supportmagic"};

  // Buffs
  private static final SkillHolder HASTE_1 = new SkillHolder(4327);
  private static final SkillHolder HASTE_2 = new SkillHolder(5632);
  private static final SkillHolder CUBIC = new SkillHolder(4338);
  private static final SkillHolder[] FIGHTER_BUFFS = {
    new SkillHolder(4322), // Wind Walk
    new SkillHolder(4323), // Shield
    new SkillHolder(5637), // Magic Barrier
    new SkillHolder(4324), // Bless the Body
    new SkillHolder(4325), // Vampiric Rage
    new SkillHolder(4326), // Regeneration
  };
  private static final SkillHolder[] MAGE_BUFFS = {
    new SkillHolder(4322), // Wind Walk
    new SkillHolder(4323), // Shield
    new SkillHolder(5637), // Magic Barrier
    new SkillHolder(4328), // Bless the Soul
    new SkillHolder(4329), // Acumen
    new SkillHolder(4330), // Concentration
    new SkillHolder(4331), // Empower
  };
  private static final SkillHolder[] SUMMON_BUFFS = {
    new SkillHolder(4322), // Wind Walk
    new SkillHolder(4323), // Shield
    new SkillHolder(5637), // Magic Barrier
    new SkillHolder(4324), // Bless the Body
    new SkillHolder(4325), // Vampiric Rage
    new SkillHolder(4326), // Regeneration
    new SkillHolder(4328), // Bless the Soul
    new SkillHolder(4329), // Acumen
    new SkillHolder(4330), // Concentration
    new SkillHolder(4331), // Empower
  };

  // Levels
  private static final int LOWEST_LEVEL = 6;
  private static final int HIGHEST_LEVEL = 75;
  private static final int CUBIC_LOWEST = 16;
  private static final int CUBIC_HIGHEST = 34;
  private static final int HASTE_LEVEL_2 = 40;

  @Override
  public boolean useBypass(String command, L2PcInstance activeChar, L2Character target) {
    if (!target.isNpc() || activeChar.isCursedWeaponEquipped()) {
      return false;
    }

    if (command.equalsIgnoreCase(COMMANDS[0])) {
      makeSupportMagic(activeChar, (L2Npc) target, true);
    } else if (command.equalsIgnoreCase(COMMANDS[1])) {
      makeSupportMagic(activeChar, (L2Npc) target, false);
    }
    return true;
  }

  private static void makeSupportMagic(L2PcInstance player, L2Npc npc, boolean isSummon) {
    final int level = player.getLevel();
    if (isSummon && !player.hasServitor()) {
      npc.showChatWindow(player, "data/html/default/SupportMagicNoSummon.htm");
      return;
    } else if (level > HIGHEST_LEVEL) {
      npc.showChatWindow(player, "data/html/default/SupportMagicHighLevel.htm");
      return;
    } else if (level < LOWEST_LEVEL) {
      npc.showChatWindow(player, "data/html/default/SupportMagicLowLevel.htm");
      return;
    } else if (player.getClassId().level() == 3) {
      player.sendMessage(
          "Only adventurers who have not completed their 3rd class transfer may receive these buffs."); // Custom message
      return;
    }

    if (isSummon) {
      npc.setTarget(player.getSummon());
      for (SkillHolder skill : SUMMON_BUFFS) {
        npc.doCast(skill.getSkill());
      }

      if (level >= HASTE_LEVEL_2) {
        npc.doCast(HASTE_2.getSkill());
      } else {
        npc.doCast(HASTE_1.getSkill());
      }
    } else {
      npc.setTarget(player);
      if (player.isInCategory(CategoryType.BEGINNER_MAGE)) {
        for (SkillHolder skill : MAGE_BUFFS) {
          npc.doCast(skill.getSkill());
        }
      } else {
        for (SkillHolder skill : FIGHTER_BUFFS) {
          npc.doCast(skill.getSkill());
        }

        if (level >= HASTE_LEVEL_2) {
          npc.doCast(HASTE_2.getSkill());
        } else {
          npc.doCast(HASTE_1.getSkill());
        }
      }

      if ((level >= CUBIC_LOWEST) && (level <= CUBIC_HIGHEST)) {
        player.doSimultaneousCast(CUBIC.getSkill());
      }
    }
  }

  @Override
  public String[] getBypassList() {
    return COMMANDS;
  }
}
