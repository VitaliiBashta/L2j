
package com.l2jserver.datapack.custom.service.buffer;

import com.l2jserver.gameserver.handler.IItemHandler;
import com.l2jserver.gameserver.model.actor.L2Playable;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.items.instance.L2ItemInstance;

/**
 * Buffer Service item handler.
 * @author HorridoJoho
 * @version 2.6.2.0
 */
public final class BufferServiceItemHandler implements IItemHandler {
	
	private BufferServiceItemHandler() {
		// Do nothing.
	}
	
	@Override
	public boolean useItem(L2Playable playable, L2ItemInstance item, boolean forceUse) {
		if (!playable.isPlayer()) {
			return false;
		}
		
		BufferService.getInstance().executeCommand((L2PcInstance) playable, null, null);
		return true;
	}
	
	public static BufferServiceItemHandler getInstance() {
		return SingletonHolder.INSTANCE;
	}
	
	private static final class SingletonHolder {
		protected static final BufferServiceItemHandler INSTANCE = new BufferServiceItemHandler();
	}
}
