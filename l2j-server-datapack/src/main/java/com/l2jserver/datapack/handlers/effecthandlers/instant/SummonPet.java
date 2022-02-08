
package com.l2jserver.datapack.handlers.effecthandlers.instant;

import java.util.logging.Level;

import com.l2jserver.gameserver.data.xml.impl.NpcData;
import com.l2jserver.gameserver.data.xml.impl.PetDataTable;
import com.l2jserver.gameserver.model.L2PetData;
import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PetInstance;
import com.l2jserver.gameserver.model.actor.templates.L2NpcTemplate;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.holders.PetItemHolder;
import com.l2jserver.gameserver.model.items.instance.L2ItemInstance;
import com.l2jserver.gameserver.model.skills.BuffInfo;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.PetItemList;

/**
 * Summon Pet effect implementation.
 * @author UnAfraid
 */
public final class SummonPet extends AbstractEffect {
	public SummonPet(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
	}
	
	@Override
	public boolean isInstant() {
		return true;
	}
	
	@Override
	public void onStart(BuffInfo info) {
		if ((info.getEffector() == null) || (info.getEffected() == null) || !info.getEffector().isPlayer() || !info.getEffected().isPlayer() || info.getEffected().isAlikeDead()) {
			return;
		}
		
		final L2PcInstance player = info.getEffector().getActingPlayer();
		
		if ((player.hasSummon() || player.isMounted())) {
			player.sendPacket(SystemMessageId.YOU_ALREADY_HAVE_A_PET);
			return;
		}
		
		final PetItemHolder holder = player.removeScript(PetItemHolder.class);
		if (holder == null) {
			_log.log(Level.WARNING, "Summoning pet without attaching PetItemHandler!", new Throwable());
			return;
		}
		
		final L2ItemInstance item = holder.getItem();
		if (player.getInventory().getItemByObjectId(item.getObjectId()) != item) {
			_log.log(Level.WARNING, "Player: " + player + " is trying to summon pet from item that he doesn't owns.");
			return;
		}
		
		final L2PetData petData = PetDataTable.getInstance().getPetDataByItemId(item.getId());
		if ((petData == null) || (petData.getNpcId() == -1)) {
			return;
		}
		
		final L2NpcTemplate npcTemplate = NpcData.getInstance().getTemplate(petData.getNpcId());
		final L2PetInstance pet = L2PetInstance.spawnPet(npcTemplate, player, item);
		
		pet.setShowSummonAnimation(true);
		if (!pet.isRespawned()) {
			pet.setCurrentHp(pet.getMaxHp());
			pet.setCurrentMp(pet.getMaxMp());
			pet.getStat().setExp(pet.getExpForThisLevel());
			pet.setCurrentFed(pet.getMaxFed());
		}
		
		pet.setRunning();
		
		if (!pet.isRespawned()) {
			pet.storeMe();
		}
		
		item.setEnchantLevel(pet.getLevel());
		player.setPet(pet);
		pet.spawnMe(player.getX() + 50, player.getY() + 100, player.getZ());
		pet.startFeed();
		pet.setFollowStatus(true);
		pet.getOwner().sendPacket(new PetItemList(pet.getInventory().getItems()));
		pet.broadcastStatusUpdate();
	}
}
