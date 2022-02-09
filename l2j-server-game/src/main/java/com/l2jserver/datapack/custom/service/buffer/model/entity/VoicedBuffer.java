
package com.l2jserver.datapack.custom.service.buffer.model.entity;

import com.l2jserver.datapack.custom.service.buffer.model.BufferConfig;
import com.l2jserver.gameserver.config.Configuration;
import com.l2jserver.gameserver.datatables.ItemTable;
import com.l2jserver.gameserver.model.items.L2Item;

/**
 * Voiced buffer.
 * @author HorridoJoho
 * @version 2.6.2.0
 */
public final class VoicedBuffer extends AbstractBuffer {
	public VoicedBuffer() {
		super("voice ." + Configuration.bufferService().getVoicedCommand());
	}
	
	@Override
	public void afterDeserialize(BufferConfig config) {
		super.afterDeserialize(config);
	}
	
	public L2Item getRequiredItem() {
		return ItemTable.getInstance().getTemplate(Configuration.bufferService().getVoicedRequiredItem());
	}
	
	@Override
	public String getName() {
		return Configuration.bufferService().getVoicedName();
	}
}
