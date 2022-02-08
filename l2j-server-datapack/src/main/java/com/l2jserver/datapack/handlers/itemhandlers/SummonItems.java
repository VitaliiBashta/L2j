
package com.l2jserver.datapack.handlers.itemhandlers;

import com.l2jserver.gameserver.data.xml.impl.PetDataTable;
import com.l2jserver.gameserver.model.L2PetData;
import com.l2jserver.gameserver.model.actor.L2Playable;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.entity.TvTEvent;
import com.l2jserver.gameserver.model.holders.PetItemHolder;
import com.l2jserver.gameserver.model.items.instance.L2ItemInstance;
import com.l2jserver.gameserver.network.SystemMessageId;

/**
 * @author HorridoJoho, UnAfraid
 */
public class SummonItems extends ItemSkillsTemplate {
	@Override
	public boolean useItem(L2Playable playable, L2ItemInstance item, boolean forceUse) {
		if (!playable.isPlayer()) {
			playable.sendPacket(SystemMessageId.ITEM_NOT_FOR_PETS);
			return false;
		}
		
		if (!TvTEvent.onItemSummon(playable.getObjectId())) {
			return false;
		}
		
		final L2PcInstance activeChar = playable.getActingPlayer();
		if (!activeChar.getFloodProtectors().getItemPetSummon().tryPerformAction("summon items") || (activeChar.getBlockCheckerArena() != -1) || activeChar.inObserverMode() || activeChar.isAllSkillsDisabled() || activeChar.isCastingNow()) {
			return false;
		}
		
		if (activeChar.isSitting()) {
			activeChar.sendPacket(SystemMessageId.CANT_MOVE_SITTING);
			return false;
		}
		
		if (activeChar.hasSummon() || activeChar.isMounted()) {
			activeChar.sendPacket(SystemMessageId.YOU_ALREADY_HAVE_A_PET);
			return false;
		}
		
		if (activeChar.isAttackingNow()) {
			activeChar.sendPacket(SystemMessageId.YOU_CANNOT_SUMMON_IN_COMBAT);
			return false;
		}
		
		final L2PetData petData = PetDataTable.getInstance().getPetDataByItemId(item.getId());
		if ((petData == null) || (petData.getNpcId() == -1)) {
			return false;
		}
		
		activeChar.addScript(new PetItemHolder(item));
		return super.useItem(playable, item, forceUse);
	}
}
