
package com.l2jserver.datapack.handlers.effecthandlers.instant;

import java.util.Collection;

import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.actor.L2Attackable;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.holders.ItemHolder;
import com.l2jserver.gameserver.model.skills.BuffInfo;

/**
 * Sweeper effect implementation.
 * @author Zoey76
 */
public final class Sweeper extends AbstractEffect {
	public Sweeper(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
	}
	
	@Override
	public boolean isInstant() {
		return true;
	}
	
	@Override
	public void onStart(BuffInfo info) {
		if ((info.getEffector() == null) || (info.getEffected() == null) || !info.getEffector().isPlayer() || !info.getEffected().isAttackable()) {
			return;
		}
		
		final L2PcInstance player = info.getEffector().getActingPlayer();
		final L2Attackable monster = (L2Attackable) info.getEffected();
		if (!monster.checkSpoilOwner(player, false)) {
			return;
		}
		
		if (!player.getInventory().checkInventorySlotsAndWeight(monster.getSpoilLootItems(), false, false)) {
			return;
		}
		
		final Collection<ItemHolder> items = monster.takeSweep();
		if (items != null) {
			for (ItemHolder item : items) {
				if (player.isInParty()) {
					player.getParty().distributeItem(player, item, true, monster);
				} else {
					player.addItem("Sweeper", item, info.getEffected(), true);
				}
			}
		}
	}
}
