
package com.l2jserver.datapack.handlers.effecthandlers.instant;

import static com.l2jserver.gameserver.enums.EffectCalculationType.DIFF;
import static com.l2jserver.gameserver.model.itemcontainer.Inventory.PAPERDOLL_LBRACELET;

import java.util.List;

import com.l2jserver.gameserver.agathion.repository.AgathionRepository;
import com.l2jserver.gameserver.enums.EffectCalculationType;
import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.skills.BuffInfo;
import com.l2jserver.gameserver.network.serverpackets.ExBR_AgathionEnergyInfo;

/**
 * Agathion Energy instant effect implementation.
 * @author Zoey76
 * @version 2.6.2.0
 */
public final class InstantAgathionEnergy extends AbstractEffect {
	
	private final double energy;
	private final EffectCalculationType mode;
	
	public InstantAgathionEnergy(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
		energy = params.getDouble("energy", 0);
		mode = params.getEnum("mode", EffectCalculationType.class, DIFF);
	}
	
	@Override
	public boolean isInstant() {
		return true;
	}
	
	@Override
	public void onStart(BuffInfo info) {
		if (info.getEffected().isDead()) {
			return;
		}
		
		if (!info.getEffected().isPlayer()) {
			return;
		}
		
		final var target = info.getEffected().getActingPlayer();
		final var agathionInfo = AgathionRepository.getInstance().getByNpcId(target.getAgathionId());
		if ((agathionInfo == null) || (agathionInfo.getMaxEnergy() <= 0)) {
			return;
		}
		
		final var agathionItem = target.getInventory().getPaperdollItem(PAPERDOLL_LBRACELET);
		if ((agathionItem == null) || (agathionInfo.getItemId() != agathionItem.getId())) {
			return;
		}
		
		int agathionEnergy = 0;
		switch (mode) {
			case DIFF: {
				agathionEnergy = (int) Math.max(0, agathionItem.getAgathionRemainingEnergy() + energy);
				break;
			}
			case PER: {
				agathionEnergy = (int) ((agathionItem.getAgathionRemainingEnergy() * energy) / 100.0);
				break;
			}
		}
		agathionItem.setAgathionRemainingEnergy(agathionEnergy);
		
		// If item is agathion with energy, then send info to client.
		target.sendPacket(new ExBR_AgathionEnergyInfo(List.of(agathionItem)));
	}
}
