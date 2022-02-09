package com.l2jserver.datapack.handlers.effecthandlers.instant;

import com.l2jserver.gameserver.instancemanager.MapRegionManager;
import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.TeleportWhereType;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.effects.L2EffectType;
import com.l2jserver.gameserver.model.skills.BuffInfo;

public final class Escape extends AbstractEffect {
	private final TeleportWhereType _escapeType;
	
	public Escape(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
		
		_escapeType = params.getEnum("escapeType", TeleportWhereType.class, null);
	}
	
	@Override
	public L2EffectType getEffectType() {
		return L2EffectType.TELEPORT;
	}
	
	@Override
	public boolean isInstant() {
		return true;
	}
	
	@Override
	public void onStart(BuffInfo info) {
		if (_escapeType == null) {
			return;
		}
		
		info.getEffected().teleToLocation(MapRegionManager.getInstance().getTeleToLocation(info.getEffected(), _escapeType), true);
		info.getEffected().getActingPlayer().setIsIn7sDungeon(false);
		info.getEffected().setInstanceId(0);
	}
}