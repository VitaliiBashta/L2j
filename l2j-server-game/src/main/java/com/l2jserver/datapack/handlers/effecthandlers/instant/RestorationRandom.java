
package com.l2jserver.datapack.handlers.effecthandlers.instant;

import static com.l2jserver.gameserver.config.Configuration.rates;

import java.util.ArrayList;
import java.util.List;

import com.l2jserver.commons.util.Rnd;
import com.l2jserver.gameserver.model.L2ExtractableProductItem;
import com.l2jserver.gameserver.model.L2ExtractableSkill;
import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.holders.ItemHolder;
import com.l2jserver.gameserver.model.skills.BuffInfo;
import com.l2jserver.gameserver.network.SystemMessageId;

/**
 * Restoration Random effect implementation.<br>
 * This effect is present in item skills that "extract" new items upon usage.<br>
 * This effect has been unhardcoded in order to work on targets as well.
 * @author Zoey76
 */
public final class RestorationRandom extends AbstractEffect {
	public RestorationRandom(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
	}
	
	@Override
	public boolean isInstant() {
		return true;
	}
	
	@Override
	public void onStart(BuffInfo info) {
		if ((info.getEffector() == null) || (info.getEffected() == null) || !info.getEffector().isPlayer() || !info.getEffected().isPlayer()) {
			return;
		}
		
		final L2ExtractableSkill exSkill = info.getSkill().getExtractableSkill();
		if (exSkill == null) {
			return;
		}
		
		if (exSkill.getProductItems().isEmpty()) {
			_log.warning("Extractable Skill with no data, probably wrong/empty table in Skill Id: " + info.getSkill().getId());
			return;
		}
		
		final double rndNum = 100 * Rnd.nextDouble();
		double chance = 0;
		double chanceFrom = 0;
		final List<ItemHolder> creationList = new ArrayList<>();
		
		// Explanation for future changes:
		// You get one chance for the current skill, then you can fall into
		// one of the "areas" like in a roulette.
		// Example: for an item like Id1,A1,30;Id2,A2,50;Id3,A3,20;
		// #---#-----#--#
		// 0--30----80-100
		// If you get chance equal 45% you fall into the second zone 30-80.
		// Meaning you get the second production list.
		// Calculate extraction
		for (L2ExtractableProductItem expi : exSkill.getProductItems()) {
			chance = expi.getChance();
			if ((rndNum >= chanceFrom) && (rndNum <= (chance + chanceFrom))) {
				creationList.addAll(expi.getItems());
				break;
			}
			chanceFrom += chance;
		}
		
		final L2PcInstance player = info.getEffected().getActingPlayer();
		if (creationList.isEmpty()) {
			player.sendPacket(SystemMessageId.NOTHING_INSIDE_THAT);
			return;
		}
		
		for (ItemHolder item : creationList) {
			if ((item.getId() <= 0) || (item.getCount() <= 0)) {
				continue;
			}
			player.addItem("Extract", item.getId(), (long) (item.getCount() * rates().getRateExtractable()), info.getEffector(), true);
		}
	}
}
