
package com.l2jserver.datapack.handlers.effecthandlers.instant;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;

import com.l2jserver.commons.util.Rnd;
import com.l2jserver.gameserver.model.CharEffectList;
import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.effects.L2EffectType;
import com.l2jserver.gameserver.model.skills.AbnormalType;
import com.l2jserver.gameserver.model.skills.BuffInfo;

/**
 * Dispel By Slot Probability effect implementation.
 * @author Adry_85, Zoey76
 */
public final class DispelBySlotProbability extends AbstractEffect {
	private final Map<AbnormalType, Short> _dispelAbnormals;
	private final int _rate;
	
	public DispelBySlotProbability(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
		
		final var dispel = params.getString("dispel", null);
		_rate = params.getInt("rate", 0);
		if ((dispel != null) && !dispel.isEmpty()) {
			_dispelAbnormals = new EnumMap<>(AbnormalType.class);
			for (String ngtStack : dispel.split(";")) {
				String[] ngt = ngtStack.split(",");
				_dispelAbnormals.put(AbnormalType.valueOf(ngt[0]), (ngt.length > 1) ? Short.parseShort(ngt[1]) : Short.MAX_VALUE);
			}
		} else {
			_dispelAbnormals = Collections.<AbnormalType, Short> emptyMap();
		}
	}
	
	@Override
	public L2EffectType getEffectType() {
		return L2EffectType.DISPEL;
	}
	
	@Override
	public boolean isInstant() {
		return true;
	}
	
	@Override
	public void onStart(BuffInfo info) {
		if (_dispelAbnormals.isEmpty()) {
			return;
		}
		
		final L2Character effected = info.getEffected();
		final CharEffectList effectList = effected.getEffectList();
		// There is no need to iterate over all buffs,
		// Just iterate once over all slots to dispel and get the buff with that abnormal if exists,
		// Operation of O(n) for the amount of slots to dispel (which is usually small) and O(1) to get the buff.
		for (Entry<AbnormalType, Short> entry : _dispelAbnormals.entrySet()) {
			if ((Rnd.get(100) < _rate)) {
				// Dispel transformations (buff and by GM)
				if ((entry.getKey() == AbnormalType.TRANSFORM)) {
					if (effected.isTransformed() || (effected.isPlayer() || (entry.getValue() == effected.getActingPlayer().getTransformationId()) || (entry.getValue() < 0))) {
						info.getEffected().stopTransformation(true);
					}
				}
				
				final BuffInfo toDispel = effectList.getBuffInfoByAbnormalType(entry.getKey());
				if (toDispel == null) {
					continue;
				}
				
				if ((toDispel.getSkill().getAbnormalType() == entry.getKey()) && (entry.getValue() >= toDispel.getSkill().getAbnormalLvl())) {
					effectList.stopSkillEffects(true, entry.getKey());
				}
			}
		}
	}
}
