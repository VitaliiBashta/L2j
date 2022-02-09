package com.l2jserver.datapack.handlers.admincommandhandlers;

import com.l2jserver.gameserver.ThreadPoolManager;
import com.l2jserver.gameserver.datatables.SkillData;
import com.l2jserver.gameserver.handler.IAdminCommandHandler;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.skills.Skill;
import com.l2jserver.gameserver.network.serverpackets.MagicSkillUse;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import static com.l2jserver.gameserver.config.Configuration.general;

@Service
public class AdminTest implements IAdminCommandHandler {
  private static final String[] ADMIN_COMMANDS = {"admin_stats", "admin_skill_test", "admin_known"};

  @Override
  public boolean useAdminCommand(String command, L2PcInstance activeChar) {
    if (command.equals("admin_stats")) {
      for (String line : ThreadPoolManager.getInstance().getStats()) {
        activeChar.sendMessage(line);
      }
    } else if (command.startsWith("admin_skill_test")) {
      try {
        StringTokenizer st = new StringTokenizer(command);
        st.nextToken();
        int id = Integer.parseInt(st.nextToken());
        if (command.startsWith("admin_skill_test")) {
          adminTestSkill(activeChar, id, true);
        } else {
          adminTestSkill(activeChar, id, false);
        }
      } catch (NumberFormatException e) {
        activeChar.sendMessage("Command format is //skill_test <ID>");
      } catch (NoSuchElementException nsee) {
        activeChar.sendMessage("Command format is //skill_test <ID>");
      }
    } else if (command.equals("admin_known on")) {
      general().setProperty("CheckKnownList", "true");
    } else if (command.equals("admin_known off")) {
      general().setProperty("CheckKnownList", "false");
    }
    return true;
  }

  private void adminTestSkill(L2PcInstance activeChar, int id, boolean msu) {
    L2Character caster;
    L2Object target = activeChar.getTarget();
    if (!(target instanceof L2Character)) {
      caster = activeChar;
    } else {
      caster = (L2Character) target;
    }

    Skill _skill = SkillData.getInstance().getSkill(id, 1);
    if (_skill != null) {
      caster.setTarget(activeChar);
      if (msu) {
        caster.broadcastPacket(
            new MagicSkillUse(
                caster, activeChar, id, 1, _skill.getHitTime(), _skill.getReuseDelay()));
      } else {
        caster.doCast(_skill);
      }
    }
  }

  @Override
  public String[] getAdminCommandList() {
    return ADMIN_COMMANDS;
  }
}
