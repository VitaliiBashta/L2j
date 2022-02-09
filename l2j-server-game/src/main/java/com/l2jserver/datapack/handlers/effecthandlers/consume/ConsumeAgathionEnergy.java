package com.l2jserver.datapack.handlers.effecthandlers.consume;

import static com.l2jserver.gameserver.model.itemcontainer.Inventory.PAPERDOLL_LBRACELET;

import java.util.List;

import com.l2jserver.gameserver.agathion.repository.AgathionRepository;
import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.skills.BuffInfo;
import com.l2jserver.gameserver.network.serverpackets.ExBR_AgathionEnergyInfo;

public final class ConsumeAgathionEnergy extends AbstractEffect {
	
	private final int energy;
	
	public ConsumeAgathionEnergy(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
		energy = params.getInt("energy", 0);
		setTicks(params.getInt("ticks"));
	}
	
	@Override
	public boolean onActionTime(BuffInfo info) {
		if (info.getEffected().isDead()) {
			return false;
		}
		
		if (!info.getEffected().isPlayer()) {
			return false;
		}
		
		final var target = info.getEffected().getActingPlayer();
		final var agathionInfo = AgathionRepository.getInstance().getByNpcId(target.getAgathionId());
		if ((agathionInfo == null) || (agathionInfo.getMaxEnergy() <= 0)) {
			return false;
		}
		
		final var agathionItem = target.getInventory().getPaperdollItem(PAPERDOLL_LBRACELET);
		if ((agathionItem == null) || (agathionInfo.getItemId() != agathionItem.getId())) {
			return false;
		}
		
		final var consumed = (int) (energy * getTicksMultiplier());
		if ((consumed < 0) && ((agathionItem.getAgathionRemainingEnergy() + consumed) <= 0)) {
			return false;
		}
		agathionItem.setAgathionRemainingEnergy(agathionItem.getAgathionRemainingEnergy() + consumed);
		
		// If item is agathion with energy, then send info to client.
		info.getEffected().sendPacket(new ExBR_AgathionEnergyInfo(List.of(agathionItem)));
		
		return true;
	}
}
