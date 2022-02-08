
package com.l2jserver.datapack.handlers.effecthandlers.instant;

import com.l2jserver.commons.util.Rnd;
import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.actor.instance.L2MonsterInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.holders.ItemHolder;
import com.l2jserver.gameserver.model.skills.BuffInfo;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.SystemMessage;

/**
 * Harvesting effect implementation.
 * @author l3x, Zoey76
 */
public final class Harvesting extends AbstractEffect {
	public Harvesting(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
	}
	
	@Override
	public boolean isInstant() {
		return true;
	}
	
	@Override
	public void onStart(BuffInfo info) {
		if ((info.getEffector() == null) || (info.getEffected() == null) || !info.getEffector().isPlayer() || !info.getEffected().isMonster() || !info.getEffected().isDead()) {
			return;
		}
		
		final L2PcInstance player = info.getEffector().getActingPlayer();
		final L2MonsterInstance monster = (L2MonsterInstance) info.getEffected();
		if (player.getObjectId() != monster.getSeederId()) {
			player.sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_HARVEST);
		} else if (monster.isSeeded()) {
			if (calcSuccess(player, monster)) {
				final ItemHolder item = monster.takeHarvest();
				if (item != null) {
					// Add item
					player.getInventory().addItem("Harvesting", item.getId(), item.getCount(), player, monster);
					
					// Send system msg
					SystemMessage sm = null;
					if (item.getCount() == 1) {
						sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_PICKED_UP_S1);
						sm.addItemName(item.getId());
					} else {
						sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_PICKED_UP_S1_S2);
						sm.addItemName(item.getId());
						sm.addLong(item.getCount());
					}
					player.sendPacket(sm);
					
					// Send msg to party
					if (player.isInParty()) {
						if (item.getCount() == 1) {
							sm = SystemMessage.getSystemMessage(SystemMessageId.C1_HARVESTED_S2S);
							sm.addString(player.getName());
							sm.addItemName(item.getId());
						} else {
							sm = SystemMessage.getSystemMessage(SystemMessageId.C1_HARVESTED_S3_S2S);
							sm.addString(player.getName());
							sm.addLong(item.getCount());
							sm.addItemName(item.getId());
						}
						player.getParty().broadcastToPartyMembers(player, sm);
					}
				}
			} else {
				player.sendPacket(SystemMessageId.THE_HARVEST_HAS_FAILED);
			}
		} else {
			player.sendPacket(SystemMessageId.THE_HARVEST_FAILED_BECAUSE_THE_SEED_WAS_NOT_SOWN);
		}
	}
	
	private static boolean calcSuccess(L2PcInstance activeChar, L2MonsterInstance target) {
		final int levelPlayer = activeChar.getLevel();
		final int levelTarget = target.getLevel();
		
		int diff = (levelPlayer - levelTarget);
		if (diff < 0) {
			diff = -diff;
		}
		
		// apply penalty, target <=> player levels
		// 5% penalty for each level
		int basicSuccess = 100;
		if (diff > 5) {
			basicSuccess -= (diff - 5) * 5;
		}
		
		// success rate can't be less than 1%
		if (basicSuccess < 1) {
			basicSuccess = 1;
		}
		return Rnd.nextInt(99) < basicSuccess;
	}
}
