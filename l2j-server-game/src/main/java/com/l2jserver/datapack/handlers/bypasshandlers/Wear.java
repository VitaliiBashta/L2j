package com.l2jserver.datapack.handlers.bypasshandlers;

import com.l2jserver.gameserver.data.xml.impl.BuyListData;
import com.l2jserver.gameserver.handler.IBypassHandler;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.buylist.L2BuyList;
import com.l2jserver.gameserver.network.serverpackets.ActionFailed;
import com.l2jserver.gameserver.network.serverpackets.ShopPreviewList;
import org.springframework.stereotype.Service;

import java.util.StringTokenizer;
import java.util.logging.Level;

import static com.l2jserver.gameserver.config.Configuration.general;

@Service
public class Wear implements IBypassHandler {
  @Override
  public boolean useBypass(String command, L2PcInstance activeChar, L2Character target) {
    if (!target.isNpc()) {
      return false;
    }

    if (!general().allowWear()) {
      return false;
    }

    try {
      StringTokenizer st = new StringTokenizer(command, " ");
      st.nextToken();

      if (st.countTokens() < 1) {
        return false;
      }

      showWearWindow(activeChar, Integer.parseInt(st.nextToken()));
      return true;
    } catch (Exception e) {
      _log.log(Level.WARNING, "Exception in " + getClass().getSimpleName(), e);
    }
    return false;
  }

  private void showWearWindow(L2PcInstance player, int val) {
    final L2BuyList buyList = BuyListData.getInstance().getBuyList(val);
    if (buyList == null) {
      _log.warning("BuyList not found! BuyListId:" + val);
      player.sendPacket(ActionFailed.STATIC_PACKET);
      return;
    }

    player.setInventoryBlockingStatus(true);

    player.sendPacket(new ShopPreviewList(buyList, player.getAdena(), player.getExpertiseLevel()));
  }

  @Override
  public String[] getBypassList() {
    return new String[] {"Wear"};
  }
}
