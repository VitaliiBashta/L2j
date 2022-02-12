package com.l2jserver.gameserver.instancemanager;

import com.l2jserver.commons.database.ConnectionFactory;
import com.l2jserver.gameserver.model.itemauction.ItemAuctionInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static com.l2jserver.gameserver.config.Configuration.server;

@Service
public class ItemAuctionManager {

  private static final Logger LOG = LoggerFactory.getLogger(ItemAuctionManager.class);

  private final Map<Integer, ItemAuctionInstance> managerInstances = new HashMap<>();
  private final AtomicInteger auctionIds;

  private final ConnectionFactory connectionFactory;

  protected ItemAuctionManager(ConnectionFactory connectionFactory) {
    this.connectionFactory = connectionFactory;
    auctionIds = new AtomicInteger(loadAuctions(connectionFactory));

    final File file = new File(server().getDatapackRoot(), "data/ItemAuctions.xml");
    if (!file.exists()) {
      LOG.warn("Missing ItemAuctions.xml!");
      return;
    }

    // TODO(Zoey76): Use IXmlReader.
    final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setValidating(false);
    factory.setIgnoringComments(true);

    try {
      final Document doc = factory.newDocumentBuilder().parse(file);
      for (Node na = doc.getFirstChild(); na != null; na = na.getNextSibling()) {
        if ("list".equalsIgnoreCase(na.getNodeName())) {
          for (Node nb = na.getFirstChild(); nb != null; nb = nb.getNextSibling()) {
            if ("instance".equalsIgnoreCase(nb.getNodeName())) {
              final NamedNodeMap nab = nb.getAttributes();
              final int instanceId = Integer.parseInt(nab.getNamedItem("id").getNodeValue());

              if (managerInstances.containsKey(instanceId)) {
                throw new Exception("Dublicated instanceId " + instanceId);
              }

              final ItemAuctionInstance instance =
                  new ItemAuctionInstance(this, instanceId, auctionIds, nb);
              managerInstances.put(instanceId, instance);
            }
          }
        }
      }
      LOG.info("Loaded " + managerInstances.size() + " auction manager instance(s).");
    } catch (Exception e) {
      LOG.error("Failed loading auctions from xml!", e);
    }
  }

  private int loadAuctions(ConnectionFactory connectionFactory) {
    var auctionIds = 0;
    try (var con = connectionFactory.getConnection();
        var s = con.createStatement();
        var rs =
            s.executeQuery(
                "SELECT auctionId FROM item_auction ORDER BY auctionId DESC LIMIT 0, 1")) {
      if (rs.next()) {
        auctionIds += rs.getInt(1) + 1;
      }
    } catch (Exception e) {
      LOG.error("Failed loading auctions!", e);
    }
    return auctionIds;
  }

  /** Gets the single instance of {@code ItemAuctionManager}. */
  public static ItemAuctionManager getInstance() {
    return SingletonHolder.INSTANCE;
  }

  public void deleteAuction(final int auctionId) {
    try (var con = connectionFactory.getConnection()) {
      try (var ps = con.prepareStatement("DELETE FROM item_auction WHERE auctionId=?")) {
        ps.setInt(1, auctionId);
        ps.execute();
      }

      try (var ps = con.prepareStatement("DELETE FROM item_auction_bid WHERE auctionId=?")) {
        ps.setInt(1, auctionId);
        ps.execute();
      }
    } catch (Exception e) {
      LOG.error("Failed deleting auction ID {}!", auctionId, e);
    }
  }

  public void shutdown() {
    for (ItemAuctionInstance instance : managerInstances.values()) {
      instance.shutdown();
    }
  }

  public ItemAuctionInstance getManagerInstance(final int instanceId) {
    return managerInstances.get(instanceId);
  }

  public int getNextAuctionId() {
    return auctionIds.getAndIncrement();
  }

  private static class SingletonHolder {
    protected static final ItemAuctionManager INSTANCE = new ItemAuctionManager(null);
  }
}
