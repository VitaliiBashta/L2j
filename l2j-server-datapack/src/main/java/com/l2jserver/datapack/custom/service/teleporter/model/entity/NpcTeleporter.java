
package com.l2jserver.datapack.custom.service.teleporter.model.entity;

import com.l2jserver.datapack.custom.service.base.model.entity.IRefable;
import com.l2jserver.datapack.custom.service.teleporter.TeleporterServiceBypassHandler;
import com.l2jserver.datapack.custom.service.teleporter.model.TeleporterConfig;
import com.l2jserver.gameserver.data.xml.impl.NpcData;
import com.l2jserver.gameserver.model.actor.templates.L2NpcTemplate;

/**
 * Npc teleporter.
 * @author HorridoJoho
 * @version 2.6.2.0
 */
public final class NpcTeleporter extends AbstractTeleporter implements IRefable<Integer> {
	private int npcId;
	private boolean directFirstTalk;
	
	public NpcTeleporter() {
		super(TeleporterServiceBypassHandler.BYPASS);
	}
	
	@Override
	public void afterDeserialize(TeleporterConfig config) {
		super.afterDeserialize(config);
		
		getPlaceholder().addChild("ident", String.valueOf(npcId));
	}
	
	public L2NpcTemplate getNpc() {
		return NpcData.getInstance().getTemplate(npcId);
	}
	
	public int getNpcId() {
		return npcId;
	}
	
	public boolean getDirectFirstTalk() {
		return directFirstTalk;
	}
	
	@Override
	public final String getName() {
		return getNpc().getName();
	}
	
	@Override
	public final Integer getId() {
		return npcId;
	}
}
