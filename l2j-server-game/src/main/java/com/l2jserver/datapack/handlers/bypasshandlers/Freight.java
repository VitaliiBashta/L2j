
package com.l2jserver.datapack.handlers.bypasshandlers;

import com.l2jserver.gameserver.handler.IBypassHandler;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.itemcontainer.PcFreight;
import com.l2jserver.gameserver.model.items.instance.L2ItemInstance;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.PackageToList;
import com.l2jserver.gameserver.network.serverpackets.WareHouseWithdrawalList;

/**
 * @author UnAfraid
 */
public class Freight implements IBypassHandler {
	private static final String[] COMMANDS = {
		"package_withdraw",
		"package_deposit"
	};
	
	@Override
	public boolean useBypass(String command, L2PcInstance activeChar, L2Character target) {
		if (!target.isNpc()) {
			return false;
		}
		
		if (command.equalsIgnoreCase(COMMANDS[0])) {
			PcFreight freight = activeChar.getFreight();
			if (freight != null) {
				if (freight.getSize() > 0) {
					activeChar.setActiveWarehouse(freight);
					for (L2ItemInstance i : activeChar.getActiveWarehouse().getItems()) {
						if (i.isTimeLimitedItem() && (i.getRemainingTime() <= 0)) {
							activeChar.getActiveWarehouse().destroyItem("L2ItemInstance", i, activeChar, null);
						}
					}
					activeChar.sendPacket(new WareHouseWithdrawalList(activeChar, WareHouseWithdrawalList.FREIGHT));
				} else {
					activeChar.sendPacket(SystemMessageId.NO_ITEM_DEPOSITED_IN_WH);
				}
			}
		} else if (command.equalsIgnoreCase(COMMANDS[1])) {
			if (activeChar.getAccountChars().size() < 1) {
				activeChar.sendPacket(SystemMessageId.CHARACTER_DOES_NOT_EXIST);
			} else {
				activeChar.sendPacket(new PackageToList(activeChar.getAccountChars()));
			}
		}
		return false;
	}
	
	@Override
	public String[] getBypassList() {
		return COMMANDS;
	}
}
