
package com.l2jserver.datapack.custom.service.buffer.model.entity;

import com.l2jserver.datapack.custom.service.base.model.entity.IRefable;
import com.l2jserver.datapack.custom.service.buffer.BufferServiceBypassHandler;
import com.l2jserver.datapack.custom.service.buffer.model.BufferConfig;
import com.l2jserver.gameserver.data.xml.impl.NpcData;
import com.l2jserver.gameserver.model.actor.templates.L2NpcTemplate;

/**
 * NPC buffer.
 * @author HorridoJoho
 * @version 2.6.2.0
 */
public final class NpcBuffer extends AbstractBuffer implements IRefable<Integer> {
	private Integer npcId;
	private boolean directFirstTalk;
	
	public NpcBuffer() {
		super(BufferServiceBypassHandler.BYPASS);
	}
	
	@Override
	public void afterDeserialize(BufferConfig config) {
		super.afterDeserialize(config);
		
		getPlaceholder().addChild("ident", String.valueOf(npcId));
	}
	
	public L2NpcTemplate getNpc() {
		return NpcData.getInstance().getTemplate(npcId);
	}
	
	@Override
	public String getName() {
		return getNpc().getName();
	}
	
	@Override
	public Integer getId() {
		return npcId;
	}
	
	public boolean getDirectFirstTalk() {
		return directFirstTalk;
	}
}
