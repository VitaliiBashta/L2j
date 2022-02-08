
package com.l2jserver.datapack.handlers.custom;

import static com.l2jserver.gameserver.config.Configuration.customs;

import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.events.Containers;
import com.l2jserver.gameserver.model.events.EventType;
import com.l2jserver.gameserver.model.events.impl.character.player.OnPlayerPvPKill;
import com.l2jserver.gameserver.model.events.listeners.ConsumerEventListener;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.SystemMessage;
import com.l2jserver.gameserver.util.Broadcast;

/**
 * @author Zealar
 */
public class CustomAnnouncePkPvP {
	
	public CustomAnnouncePkPvP() {
		if (customs().announcePkPvP()) {
			Containers.Players().addListener(new ConsumerEventListener(Containers.Players(), EventType.ON_PLAYER_PVP_KILL, (OnPlayerPvPKill event) -> OnPlayerPvPKill(event), this));
		}
	}
	
	private Object OnPlayerPvPKill(OnPlayerPvPKill event) {
		L2PcInstance pk = event.getActiveChar();
		if (pk.isGM()) {
			return null;
		}
		L2PcInstance player = event.getTarget();
		
		String msg = customs().getAnnouncePvpMsg();
		if (player.getPvpFlag() == 0) {
			msg = customs().getAnnouncePkMsg();
		}
		msg = msg.replace("$killer", pk.getName()).replace("$target", player.getName());
		if (customs().announcePkPvPNormalMessage()) {
			SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1);
			sm.addString(msg);
			Broadcast.toAllOnlinePlayers(sm);
		} else {
			Broadcast.toAllOnlinePlayers(msg, false);
		}
		return null;
	}
}
