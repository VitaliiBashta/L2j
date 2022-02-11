package com.l2jserver.datapack.handlers.bypasshandlers;

import com.l2jserver.gameserver.handler.IBypassHandler;
import com.l2jserver.gameserver.instancemanager.ItemAuctionManager;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.itemauction.ItemAuction;
import com.l2jserver.gameserver.model.itemauction.ItemAuctionInstance;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.ExItemAuctionInfoPacket;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.logging.Level;

import static com.l2jserver.gameserver.config.Configuration.general;

@Service
public class ItemAuctionLink implements IBypassHandler {
  private static final SimpleDateFormat fmt = new SimpleDateFormat("HH:mm:ss dd.MM.yyyy");

  private static final String[] COMMANDS = {"ItemAuction"};

  @Override
  public boolean useBypass(String command, L2PcInstance activeChar, L2Character target) {
    if (!target.isNpc()) {
      return false;
    }

    if (!general().itemAuctionEnabled()) {
      activeChar.sendPacket(SystemMessageId.NO_AUCTION_PERIOD);
      return true;
    }

    final ItemAuctionInstance au =
        ItemAuctionManager.getInstance().getManagerInstance(target.getId());
    if (au == null) {
      return false;
    }

    try {
      StringTokenizer st = new StringTokenizer(command);
      st.nextToken(); // bypass "ItemAuction"
      if (!st.hasMoreTokens()) {
        return false;
      }

      String cmd = st.nextToken();
      if ("show".equalsIgnoreCase(cmd)) {
        if (!activeChar
            .getFloodProtectors()
            .getItemAuction()
            .tryPerformAction("RequestInfoItemAuction")) {
          return false;
        }

        if (activeChar.isItemAuctionPolling()) {
          return false;
        }

        final ItemAuction currentAuction = au.getCurrentAuction();
        final ItemAuction nextAuction = au.getNextAuction();

        if (currentAuction == null) {
          activeChar.sendPacket(SystemMessageId.NO_AUCTION_PERIOD);

          if (nextAuction != null) {
            activeChar.sendMessage(
                "The next auction will begin on the "
                    + fmt.format(new Date(nextAuction.getStartingTime()))
                    + ".");
          }
          return true;
        }

        activeChar.sendPacket(new ExItemAuctionInfoPacket(false, currentAuction, nextAuction));
      } else if ("cancel".equalsIgnoreCase(cmd)) {
        final ItemAuction[] auctions = au.getAuctionsByBidder(activeChar.getObjectId());
        boolean returned = false;
        for (final ItemAuction auction : auctions) {
          if (auction.cancelBid(activeChar)) {
            returned = true;
          }
        }
        if (!returned) {
          activeChar.sendPacket(SystemMessageId.NO_OFFERINGS_OWN_OR_MADE_BID_FOR);
        }
      } else {
        return false;
      }
    } catch (Exception e) {
      _log.log(Level.WARNING, "Exception in " + getClass().getSimpleName(), e);
    }

    return true;
  }

  @Override
  public String[] getBypassList() {
    return COMMANDS;
  }
}
