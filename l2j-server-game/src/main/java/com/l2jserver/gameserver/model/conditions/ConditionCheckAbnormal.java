/*
 * Copyright © 2004-2021 L2J Server
 * 
 * This file is part of L2J Server.
 * 
 * L2J Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.l2jserver.gameserver.model.conditions;

import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.items.L2Item;
import com.l2jserver.gameserver.model.skills.AbnormalType;
import com.l2jserver.gameserver.model.skills.BuffInfo;
import com.l2jserver.gameserver.model.skills.Skill;

/**
 * Check AbnormalType and level.
 * @author Zoey76
 * @version 2.6.2.0
 */
public class ConditionCheckAbnormal extends Condition {
	private final AbnormalType type;
	private final int level;
	private final boolean mustHave;
	
	public ConditionCheckAbnormal(AbnormalType type, int level, boolean mustHave) {
		this.type = type;
		this.level = level;
		this.mustHave = mustHave;
	}
	
	@Override
	public boolean testImpl(L2Character effector, L2Character effected, Skill skill, L2Item item) {
		final L2PcInstance player = effector.getActingPlayer();
		final BuffInfo info = player.getEffectList().getBuffInfoByAbnormalType(type);
		if (info == null) {
			return !mustHave;
		}
		return (mustHave) || ((level == -1) && (level >= info.getSkill().getAbnormalLvl()));
	}
}
