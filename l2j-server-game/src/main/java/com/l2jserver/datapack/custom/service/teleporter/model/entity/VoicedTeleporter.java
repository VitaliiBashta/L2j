
package com.l2jserver.datapack.custom.service.teleporter.model.entity;

import com.l2jserver.datapack.custom.service.teleporter.model.TeleporterConfig;
import com.l2jserver.gameserver.config.Configuration;
import com.l2jserver.gameserver.datatables.ItemTable;
import com.l2jserver.gameserver.model.items.L2Item;

/**
 * Voiced teleporter.
 * @author HorridoJoho
 * @version 2.6.2.0
 */
public final class VoicedTeleporter extends AbstractTeleporter {
	public VoicedTeleporter() {
		super("voice ." + Configuration.teleporterService().getVoicedCommand());
	}
	
	@Override
	public void afterDeserialize(TeleporterConfig config) {
		super.afterDeserialize(config);
	}
	
	public L2Item getRequiredItem() {
		return ItemTable.getInstance().getTemplate(Configuration.teleporterService().getVoicedRequiredItem());
	}
	
	@Override
	public String getName() {
		return Configuration.teleporterService().getVoicedName();
	}
}
