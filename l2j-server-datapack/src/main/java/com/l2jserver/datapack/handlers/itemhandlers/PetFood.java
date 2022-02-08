
package com.l2jserver.datapack.handlers.itemhandlers;

import java.util.List;

import com.l2jserver.gameserver.data.xml.impl.PetDataTable;
import com.l2jserver.gameserver.datatables.SkillData;
import com.l2jserver.gameserver.handler.IItemHandler;
import com.l2jserver.gameserver.model.actor.L2Playable;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PetInstance;
import com.l2jserver.gameserver.model.holders.SkillHolder;
import com.l2jserver.gameserver.model.items.instance.L2ItemInstance;
import com.l2jserver.gameserver.model.skills.Skill;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.MagicSkillUse;
import com.l2jserver.gameserver.network.serverpackets.SystemMessage;

/**
 * @author Kerberos, Zoey76
 */
public class PetFood implements IItemHandler {
	@Override
	public boolean useItem(L2Playable playable, L2ItemInstance item, boolean forceUse) {
		if (playable.isPet() && !((L2PetInstance) playable).canEatFoodId(item.getId())) {
			playable.sendPacket(SystemMessageId.PET_CANNOT_USE_ITEM);
			return false;
		}
		
		final SkillHolder[] skills = item.getItem().getSkills();
		if (skills != null) {
			for (SkillHolder sk : skills) {
				useFood(playable, sk.getSkillId(), sk.getSkillLvl(), item);
			}
		}
		return true;
	}
	
	public boolean useFood(L2Playable activeChar, int skillId, int skillLevel, L2ItemInstance item) {
		final Skill skill = SkillData.getInstance().getSkill(skillId, skillLevel);
		if (skill != null) {
			if (activeChar.isPet()) {
				final L2PetInstance pet = (L2PetInstance) activeChar;
				if (pet.destroyItem("Consume", item.getObjectId(), 1, null, false)) {
					pet.broadcastPacket(new MagicSkillUse(pet, pet, skillId, skillLevel, 0, 0));
					skill.applyEffects(pet, pet);
					pet.broadcastStatusUpdate();
					if (pet.isHungry()) {
						pet.sendPacket(SystemMessageId.YOUR_PET_ATE_A_LITTLE_BUT_IS_STILL_HUNGRY);
					}
					return true;
				}
			} else if (activeChar.isPlayer()) {
				final L2PcInstance player = activeChar.getActingPlayer();
				if (player.isMounted()) {
					final List<Integer> foodIds = PetDataTable.getInstance().getPetData(player.getMountNpcId()).getFood();
					if (foodIds.contains(Integer.valueOf(item.getId()))) {
						if (player.destroyItem("Consume", item.getObjectId(), 1, null, false)) {
							player.broadcastPacket(new MagicSkillUse(player, player, skillId, skillLevel, 0, 0));
							skill.applyEffects(player, player);
							return true;
						}
					}
				}
				
				player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_CANNOT_BE_USED).addItemName(item));
			}
		}
		return false;
	}
}