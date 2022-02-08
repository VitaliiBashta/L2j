
package com.l2jserver.datapack.custom.service.teleporter;

import com.l2jserver.gameserver.handler.IItemHandler;
import com.l2jserver.gameserver.model.actor.L2Playable;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.items.instance.L2ItemInstance;

/**
 * Teleporter service item handler.
 * @author HorridoJoho
 * @version 2.6.2.0
 */
public final class TeleporterServiceItemHandler implements IItemHandler {
	
	private TeleporterServiceItemHandler() {
	}
	
	@Override
	public boolean useItem(L2Playable playable, L2ItemInstance item, boolean forceUse) {
		if (!playable.isPlayer()) {
			return false;
		}
		
		TeleporterService.getService().executeCommand((L2PcInstance) playable, null, null);
		return true;
	}
	
	static TeleporterServiceItemHandler getInstance() {
		return SingletonHolder.INSTANCE;
	}
	
	private static final class SingletonHolder {
		protected static final TeleporterServiceItemHandler INSTANCE = new TeleporterServiceItemHandler();
	}
}
