
package com.l2jserver.datapack.handlers.effecthandlers.pump;

import com.l2jserver.gameserver.data.xml.impl.TransformData;
import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.skills.BuffInfo;

/**
 * Transformation effect implementation.
 * @author nBd
 */
public final class Transformation extends AbstractEffect {
	private final int _id;
	
	public Transformation(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
		
		_id = params.getInt("id", 0);
	}
	
	@Override
	public boolean canStart(BuffInfo info) {
		return info.getEffected().isPlayer();
	}
	
	@Override
	public void onExit(BuffInfo info) {
		info.getEffected().stopTransformation(false);
	}
	
	@Override
	public void onStart(BuffInfo info) {
		TransformData.getInstance().transformPlayer(_id, info.getEffected().getActingPlayer());
	}
}
