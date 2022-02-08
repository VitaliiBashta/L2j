
package com.l2jserver.datapack.handlers.bypasshandlers;

import com.l2jserver.gameserver.handler.IBypassHandler;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.events.EventDispatcher;
import com.l2jserver.gameserver.model.events.EventType;
import com.l2jserver.gameserver.model.events.impl.character.npc.OnNpcFirstTalk;

public class ChatLink implements IBypassHandler {
	private static final String[] COMMANDS = {
		"Chat"
	};
	
	@Override
	public boolean useBypass(String command, L2PcInstance activeChar, L2Character target) {
		if (!target.isNpc()) {
			return false;
		}
		
		int val = 0;
		try {
			val = Integer.parseInt(command.substring(5));
		} catch (Exception ioobe) {
			
		}
		
		final L2Npc npc = (L2Npc) target;
		if ((val == 0) && npc.hasListener(EventType.ON_NPC_FIRST_TALK)) {
			EventDispatcher.getInstance().notifyEventAsync(new OnNpcFirstTalk(npc, activeChar), npc);
		} else {
			npc.showChatWindow(activeChar, val);
		}
		return false;
	}
	
	@Override
	public String[] getBypassList() {
		return COMMANDS;
	}
}
