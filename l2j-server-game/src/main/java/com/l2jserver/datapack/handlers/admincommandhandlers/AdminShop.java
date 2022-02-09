
package com.l2jserver.datapack.handlers.admincommandhandlers;

import java.util.logging.Logger;

import com.l2jserver.gameserver.data.xml.impl.BuyListData;
import com.l2jserver.gameserver.handler.IAdminCommandHandler;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.buylist.L2BuyList;
import com.l2jserver.gameserver.network.serverpackets.ActionFailed;
import com.l2jserver.gameserver.network.serverpackets.BuyList;
import com.l2jserver.gameserver.network.serverpackets.ExBuySellList;
import org.springframework.stereotype.Service;

/**
 * This class handles following admin commands:
 * <ul>
 * <li>gmshop = shows menu</li>
 * <li>buy id = shows shop with respective id</li>
 * </ul>
 */
@Service
public class AdminShop implements IAdminCommandHandler {
	private static final Logger _log = Logger.getLogger(AdminShop.class.getName());
	
	private static final String[] ADMIN_COMMANDS = {
		"admin_buy",
		"admin_gmshop"
	};
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar) {
		if (command.startsWith("admin_buy")) {
			try {
				handleBuyRequest(activeChar, command.substring(10));
			} catch (IndexOutOfBoundsException e) {
				activeChar.sendMessage("Please specify buylist.");
			}
		} else if (command.equals("admin_gmshop")) {
			AdminHtml.showAdminHtml(activeChar, "gmshops.htm");
		}
		return true;
	}
	
	@Override
	public String[] getAdminCommandList() {
		return ADMIN_COMMANDS;
	}
	
	private void handleBuyRequest(L2PcInstance activeChar, String command) {
		int val = -1;
		try {
			val = Integer.parseInt(command);
		} catch (Exception e) {
			_log.warning("admin buylist failed:" + command);
		}
		
		L2BuyList buyList = BuyListData.getInstance().getBuyList(val);
		
		if (buyList != null) {
			activeChar.sendPacket(new BuyList(buyList, activeChar.getAdena(), 0));
			activeChar.sendPacket(new ExBuySellList(activeChar, false));
		} else {
			_log.warning("no buylist with id:" + val);
		}
		activeChar.sendPacket(ActionFailed.STATIC_PACKET);
	}
}
