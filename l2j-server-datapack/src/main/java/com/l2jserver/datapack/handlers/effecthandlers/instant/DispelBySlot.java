
package com.l2jserver.datapack.handlers.effecthandlers.instant;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;

import com.l2jserver.gameserver.model.CharEffectList;
import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.effects.L2EffectType;
import com.l2jserver.gameserver.model.skills.AbnormalType;
import com.l2jserver.gameserver.model.skills.BuffInfo;

/**
 * Dispel By Slot effect implementation.
 * @author Gnacik, Zoey76, Adry_85
 */
public final class DispelBySlot extends AbstractEffect {
	private final String _dispel;
	private final Map<AbnormalType, Short> _dispelAbnormals;
	
	public DispelBySlot(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
		
		_dispel = params.getString("dispel", null);
		if ((_dispel != null) && !_dispel.isEmpty()) {
			_dispelAbnormals = new EnumMap<>(AbnormalType.class);
			for (String ngtStack : _dispel.split(";")) {
				String[] ngt = ngtStack.split(",");
				_dispelAbnormals.put(AbnormalType.valueOf(ngt[0]), Short.parseShort(ngt[1]));
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
			// Dispel transformations (buff and by GM)
			if ((entry.getKey() == AbnormalType.TRANSFORM)) {
				if (effected.isTransformed() || (effected.isPlayer() || (entry.getValue() == effected.getActingPlayer().getTransformationId()) || (entry.getValue() < 0))) {
					info.getEffected().stopTransformation(true);
					continue;
				}
			}
			
			final BuffInfo toDispel = effectList.getBuffInfoByAbnormalType(entry.getKey());
			if (toDispel == null) {
				continue;
			}
			
			if ((entry.getKey() == toDispel.getSkill().getAbnormalType()) && ((entry.getValue() < 0) || (entry.getValue() >= toDispel.getSkill().getAbnormalLvl()))) {
				effectList.stopSkillEffects(true, entry.getKey());
			}
		}
	}
}
