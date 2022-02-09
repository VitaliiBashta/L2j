
package com.l2jserver.datapack.custom.service.teleporter;

import com.l2jserver.gameserver.handler.IBypassHandler;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

/**
 * Teleporter service bypass handler.
 * @author HorridoJoho
 * @version 2.6.2.0
 */
public class TeleporterServiceBypassHandler implements IBypassHandler {
	
	public static final String BYPASS = "TeleporterService";
	private static final String[] BYPASS_LIST = new String[] {
		BYPASS
	};
	
	private TeleporterServiceBypassHandler() {
	}
	
	@Override
	public boolean useBypass(String command, L2PcInstance activeChar, L2Character target) {
		if ((target == null) || !target.isNpc()) {
			return false;
		}
		
		TeleporterService.getService().executeCommand(activeChar, (L2Npc) target, command.substring(BYPASS.length()).trim());
		return true;
	}
	
	@Override
	public String[] getBypassList() {
		return BYPASS_LIST;
	}
	
	static TeleporterServiceBypassHandler getInstance() {
		return SingletonHolder.INSTANCE;
	}
	
	private static final class SingletonHolder {
		protected static final TeleporterServiceBypassHandler INSTANCE = new TeleporterServiceBypassHandler();
	}
}
