
package com.l2jserver.datapack.handlers.usercommandhandlers;

import com.l2jserver.gameserver.GameTimeController;
import com.l2jserver.gameserver.handler.IUserCommandHandler;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.SystemMessage;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.l2jserver.gameserver.config.Configuration.customs;

@Service
public class Time implements IUserCommandHandler {
	private static final int[] COMMAND_IDS = {
		77
	};
	
	private static final SimpleDateFormat fmt = new SimpleDateFormat("H:mm.");
	
	@Override
	public boolean useUserCommand(int id, L2PcInstance activeChar) {
		if (COMMAND_IDS[0] != id) {
			return false;
		}
		
		int t = GameTimeController.getInstance().getGameTime();
		String h = "" + ((t / 60) % 24);
		String m;
		if ((t % 60) < 10) {
			m = "0" + (t % 60);
		} else {
			m = "" + (t % 60);
		}
		
		SystemMessage sm;
		if (GameTimeController.getInstance().isNight()) {
			sm = SystemMessage.getSystemMessage(SystemMessageId.TIME_S1_S2_IN_THE_NIGHT);
			sm.addString(h);
			sm.addString(m);
		} else {
			sm = SystemMessage.getSystemMessage(SystemMessageId.TIME_S1_S2_IN_THE_DAY);
			sm.addString(h);
			sm.addString(m);
		}
		activeChar.sendPacket(sm);
		if (customs().displayServerTime()) {
			activeChar.sendMessage("Server time is " + fmt.format(new Date(System.currentTimeMillis())));
		}
		return true;
	}
	
	@Override
	public int[] getUserCommandList() {
		return COMMAND_IDS;
	}
}
