
package com.l2jserver.datapack.handlers.itemhandlers;

import com.l2jserver.gameserver.enums.ShotType;
import com.l2jserver.gameserver.handler.IItemHandler;
import com.l2jserver.gameserver.model.actor.L2Playable;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.holders.SkillHolder;
import com.l2jserver.gameserver.model.items.L2Weapon;
import com.l2jserver.gameserver.model.items.instance.L2ItemInstance;
import com.l2jserver.gameserver.model.items.type.ActionType;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.MagicSkillUse;
import com.l2jserver.gameserver.network.serverpackets.SystemMessage;
import com.l2jserver.gameserver.util.Broadcast;
import org.springframework.stereotype.Service;

import java.util.logging.Level;

@Service
public class SpiritShot implements IItemHandler {
	@Override
	public boolean useItem(L2Playable playable, L2ItemInstance item, boolean forceUse) {
		if (!playable.isPlayer()) {
			playable.sendPacket(SystemMessageId.ITEM_NOT_FOR_PETS);
			return false;
		}
		
		final L2PcInstance activeChar = (L2PcInstance) playable;
		final L2ItemInstance weaponInst = activeChar.getActiveWeaponInstance();
		final L2Weapon weaponItem = activeChar.getActiveWeaponItem();
		final SkillHolder[] skills = item.getItem().getSkills();
		
		int itemId = item.getId();
		
		if (skills == null) {
			_log.log(Level.WARNING, getClass().getSimpleName() + ": is missing skills!");
			return false;
		}
		
		// Check if Spirit shot can be used
		if ((weaponInst == null) || (weaponItem.getSpiritShotCount() == 0)) {
			if (!activeChar.getAutoSoulShot().contains(itemId)) {
				activeChar.sendPacket(SystemMessageId.CANNOT_USE_SPIRITSHOTS);
			}
			return false;
		}
		
		// Check if Spirit shot is already active
		if (activeChar.isChargedShot(ShotType.SPIRITSHOTS)) {
			return false;
		}
		
		boolean gradeCheck = item.isEtcItem() && (item.getEtcItem().getDefaultAction() == ActionType.SPIRITSHOT) && (weaponInst.getItem().getItemGradeSPlus() == item.getItem().getItemGradeSPlus());
		
		if (!gradeCheck) {
			if (!activeChar.getAutoSoulShot().contains(itemId)) {
				activeChar.sendPacket(SystemMessageId.SPIRITSHOTS_GRADE_MISMATCH);
			}
			
			return false;
		}
		
		// Consume Spirit shot if player has enough of them
		if (!activeChar.destroyItemWithoutTrace("Consume", item.getObjectId(), weaponItem.getSpiritShotCount(), null, false)) {
			if (!activeChar.disableAutoShot(itemId)) {
				activeChar.sendPacket(SystemMessageId.NOT_ENOUGH_SPIRITSHOTS);
			}
			return false;
		}
		
		// Charge Spirit shot
		activeChar.setChargedShot(ShotType.SPIRITSHOTS, true);
		
		SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.USE_S1_);
		sm.addItemName(itemId);
		activeChar.sendPacket(sm);
		
		activeChar.sendPacket(SystemMessageId.ENABLED_SPIRITSHOT);
		Broadcast.toSelfAndKnownPlayersInRadius(activeChar, new MagicSkillUse(activeChar, activeChar, skills[0].getSkillId(), skills[0].getSkillLvl(), 0, 0), 600);
		return true;
	}
}
