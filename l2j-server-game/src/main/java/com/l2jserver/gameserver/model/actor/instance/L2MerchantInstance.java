package com.l2jserver.gameserver.model.actor.instance;

import com.l2jserver.gameserver.data.xml.impl.BuyListData;
import com.l2jserver.gameserver.datatables.MerchantPriceConfigTable;
import com.l2jserver.gameserver.datatables.MerchantPriceConfigTable.MerchantPriceConfig;
import com.l2jserver.gameserver.enums.InstanceType;
import com.l2jserver.gameserver.model.actor.templates.L2NpcTemplate;
import com.l2jserver.gameserver.model.buylist.L2BuyList;
import com.l2jserver.gameserver.network.serverpackets.ActionFailed;
import com.l2jserver.gameserver.network.serverpackets.BuyList;
import com.l2jserver.gameserver.network.serverpackets.ExBuySellList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class L2MerchantInstance extends L2NpcInstance {
	private static final Logger LOG = LoggerFactory.getLogger(L2MerchantInstance.class);
	private MerchantPriceConfig _mpc;
	
	public L2MerchantInstance(L2NpcTemplate template) {
		super(template);
		setInstanceType(InstanceType.L2MerchantInstance);
	}
	
	@Override
	public void onSpawn() {
		super.onSpawn();
		_mpc = MerchantPriceConfigTable.getInstance().getMerchantPriceConfig(this);
	}
	
	@Override
	public String getHtmlPath(int npcId, int val) {
		String pom;
		if (val == 0) {
			pom = "" + npcId;
		} else {
			pom = npcId + "-" + val;
		}
		return "data/html/merchant/" + pom + ".htm";
	}
	
	public MerchantPriceConfig getMpc() {
		return _mpc;
	}
	
	public final void showBuyWindow(L2PcInstance player, int val) {
		showBuyWindow(player, val, true);
	}
	
	public final void showBuyWindow(L2PcInstance player, int val, boolean applyTax) {
		final L2BuyList buyList = BuyListData.getInstance().getBuyList(val);
		if (buyList == null) {
			LOG.warn("BuyList not found! BuyListId: {}", val);
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (!buyList.isNpcAllowed(getId())) {
			LOG.warn("Npc not allowed in BuyList! BuyListId: {} NpcId: {}", val, getId());
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		final double taxRate = (applyTax) ? getMpc().getTotalTaxRate() : 0;
		
		player.setInventoryBlockingStatus(true);
		if (player.isGM()) {
			player.sendMessage("Buy List [" + buyList.getListId() + "]");
		}
		player.sendPacket(new BuyList(buyList, player.getAdena(), taxRate));
		player.sendPacket(new ExBuySellList(player, false));
		player.sendPacket(ActionFailed.STATIC_PACKET);
	}
}
