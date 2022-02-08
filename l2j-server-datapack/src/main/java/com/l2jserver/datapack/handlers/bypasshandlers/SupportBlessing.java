
package com.l2jserver.datapack.handlers.bypasshandlers;

import com.l2jserver.gameserver.handler.IBypassHandler;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.skills.CommonSkill;

public class SupportBlessing implements IBypassHandler {
	private static final String[] COMMANDS = {
		"GiveBlessing"
	};
	
	@Override
	public boolean useBypass(String command, L2PcInstance activeChar, L2Character target) {
		if (!target.isNpc()) {
			return false;
		}
		
		final L2Npc npc = (L2Npc) target;
		
		// If the player is too high level, display a message and return
		if ((activeChar.getLevel() > 39) || (activeChar.getClassId().level() >= 2)) {
			npc.showChatWindow(activeChar, "data/html/default/SupportBlessingHighLevel.htm");
			return true;
		}
		npc.setTarget(activeChar);
		npc.doCast(CommonSkill.BLESSING_OF_PROTECTION.getSkill());
		return false;
	}
	
	@Override
	public String[] getBypassList() {
		return COMMANDS;
	}
}