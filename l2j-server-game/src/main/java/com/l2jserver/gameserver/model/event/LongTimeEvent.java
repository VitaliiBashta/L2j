package com.l2jserver.gameserver.model.event;

import com.l2jserver.gameserver.ThreadPoolManager;
import com.l2jserver.gameserver.data.sql.impl.AnnouncementsTable;
import com.l2jserver.gameserver.data.xml.impl.NpcData;
import com.l2jserver.gameserver.datatables.EventDroplist;
import com.l2jserver.gameserver.datatables.ItemTable;
import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.announce.EventAnnouncement;
import com.l2jserver.gameserver.model.drops.DropListScope;
import com.l2jserver.gameserver.model.drops.GeneralDropItem;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.script.DateRange;
import com.l2jserver.gameserver.util.Broadcast;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.l2jserver.gameserver.config.Configuration.server;

/**
 * Parent class for long time events.<br>
 * Maintains config reading, spawn of NPC's, adding of event's drop.
 *
 * @author GKR
 */
public class LongTimeEvent extends Quest {
  // NPC's to spawm and their spawn points
  private final List<NpcSpawn> spawnList = new ArrayList<>();
  // Drop data for event
  private final List<GeneralDropItem> dropList = new ArrayList<>();
  protected String endMsg = "Event ends!";
  private String eventName;
  // Messages
  private String onEnterMsg = "Event is in process";
  private DateRange eventPeriod;
  private DateRange dropPeriod;

  public LongTimeEvent(String name, String descr) {
    super(-1, name, descr);

    loadConfig();

    LocalDateTime now = LocalDateTime.now();
    if (eventPeriod != null) {
      if (eventPeriod.isWithinRange(now)) {
        startEvent();
        LOG.info("Event {} active till {}", eventName, eventPeriod.getEndDate());
      } else if (eventPeriod.getStartDate().isAfter(now)) {
        long delay = Duration.between(now, eventPeriod.getStartDate()).toMillis();
        ThreadPoolManager.getInstance().scheduleGeneral(new ScheduleStart(), delay);
        LOG.info("Event {} will be started at {}", eventName, eventPeriod.getStartDate());
      } else {
        LOG.info("Event " + eventName + " has passed... Ignored ");
      }
    }
  }

  /** Load event configuration file */
  private void loadConfig() {
    File configFile = new File(server().getDatapackRoot(), "data/events/" + getName() + ".xml");
    try {
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      DocumentBuilder db = dbf.newDocumentBuilder();
      Document doc = db.parse(configFile);
      if (!doc.getDocumentElement().getNodeName().equalsIgnoreCase("event")) {
        throw new NullPointerException("WARNING!!! " + getName() + " event: bad config file!");
      }
      eventName = doc.getDocumentElement().getAttributes().getNamedItem("name").getNodeValue();
      String period =
          doc.getDocumentElement().getAttributes().getNamedItem("active").getNodeValue();
      eventPeriod = DateRange.parse(period);

      if (doc.getDocumentElement().getAttributes().getNamedItem("dropPeriod") != null) {
        String dropPeriod =
            doc.getDocumentElement().getAttributes().getNamedItem("dropPeriod").getNodeValue();
        this.dropPeriod = DateRange.parse(dropPeriod);
        // Check if drop period is within range of event period
        if (!eventPeriod.isWithinRange(this.dropPeriod.getStartDate())
            || !eventPeriod.isWithinRange(this.dropPeriod.getEndDate())) {
          this.dropPeriod = eventPeriod;
        }
      } else {
        dropPeriod = eventPeriod; // Drop period, if not specified, assumes all event period.
      }

      if (eventPeriod == null) {
        throw new NullPointerException("WARNING!!! " + getName() + " event: illegal event period");
      }

      var today = LocalDateTime.now();

      if (eventPeriod.getStartDate().isAfter(today) || eventPeriod.isWithinRange(today)) {
        Node first = doc.getDocumentElement().getFirstChild();
        for (Node n = first; n != null; n = n.getNextSibling()) {
          // Loading droplist
          if (n.getNodeName().equalsIgnoreCase("droplist")) {
            for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
              if (d.getNodeName().equalsIgnoreCase("add")) {
                try {
                  int itemId =
                      Integer.parseInt(d.getAttributes().getNamedItem("item").getNodeValue());
                  int minCount =
                      Integer.parseInt(d.getAttributes().getNamedItem("min").getNodeValue());
                  int maxCount =
                      Integer.parseInt(d.getAttributes().getNamedItem("max").getNodeValue());
                  String chance = d.getAttributes().getNamedItem("chance").getNodeValue();
                  int finalChance = 0;

                  if (!chance.isEmpty() && chance.endsWith("%")) {
                    finalChance =
                        Integer.parseInt(chance.substring(0, chance.length() - 1)) * 10000;
                  }

                  if (ItemTable.getInstance().getTemplate(itemId) == null) {
                    LOG.warn(
                        getName()
                            + " event: "
                            + itemId
                            + " is wrong item id, item was not added in droplist");
                    continue;
                  }

                  if (minCount > maxCount) {
                    LOG.warn(
                        getName()
                            + " event: item "
                            + itemId
                            + " - min greater than max, item was not added in droplist");
                    continue;
                  }

                  if ((finalChance < 10000) || (finalChance > 1000000)) {
                    LOG.warn(
                        getName()
                            + " event: item "
                            + itemId
                            + " - incorrect drop chance, item was not added in droplist");
                    continue;
                  }

                  dropList.add(
                      (GeneralDropItem)
                          DropListScope.STATIC.newDropItem(
                              itemId, minCount, maxCount, finalChance));
                } catch (NumberFormatException nfe) {
                  LOG.warn(
                      "Wrong number format in config.xml droplist block for "
                          + getName()
                          + " event");
                }
              }
            }
          } else if (n.getNodeName().equalsIgnoreCase("spawnlist")) {
            // Loading spawnlist
            for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
              if (d.getNodeName().equalsIgnoreCase("add")) {
                try {
                  int npcId =
                      Integer.parseInt(d.getAttributes().getNamedItem("npc").getNodeValue());
                  int xPos = Integer.parseInt(d.getAttributes().getNamedItem("x").getNodeValue());
                  int yPos = Integer.parseInt(d.getAttributes().getNamedItem("y").getNodeValue());
                  int zPos = Integer.parseInt(d.getAttributes().getNamedItem("z").getNodeValue());
                  int heading =
                      d.getAttributes().getNamedItem("heading").getNodeValue() != null
                          ? Integer.parseInt(
                              d.getAttributes().getNamedItem("heading").getNodeValue())
                          : 0;

                  if (NpcData.getInstance().getTemplate(npcId) == null) {
                    LOG.warn(
                        getName()
                            + " event: "
                            + npcId
                            + " is wrong NPC id, NPC was not added in spawnlist");
                    continue;
                  }

                  spawnList.add(new NpcSpawn(npcId, new Location(xPos, yPos, zPos, heading)));
                } catch (NumberFormatException nfe) {
                  LOG.warn(
                      "Wrong number format in config.xml spawnlist block for "
                          + getName()
                          + " event");
                }
              }
            }
          } else if (n.getNodeName().equalsIgnoreCase("messages")) {
            // Loading Messages
            for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
              if (d.getNodeName().equalsIgnoreCase("add")) {
                String msgType = d.getAttributes().getNamedItem("type").getNodeValue();
                String msgText = d.getAttributes().getNamedItem("text").getNodeValue();
                if ((msgType != null) && (msgText != null)) {
                  if (msgType.equalsIgnoreCase("onEnd")) {
                    endMsg = msgText;
                  } else if (msgType.equalsIgnoreCase("onEnter")) {
                    onEnterMsg = msgText;
                  }
                }
              }
            }
          }
        }
      }
    } catch (Exception e) {
      LOG.warn(
          getName()
              + " event: error reading "
              + configFile.getAbsolutePath()
              + " ! "
              + e.getMessage(),
          e);
    }
  }

  /** Maintenance event start - adds global drop, spawns event NPC's, shows start announcement. */
  protected void startEvent() {
    var currentTime = LocalDateTime.now();
    // Add drop
    if (dropList != null) {
      if (currentTime.isBefore(dropPeriod.getEndDate())) {
        for (GeneralDropItem drop : dropList) {
          EventDroplist.getInstance()
              .addGlobalDrop(
                  drop.getItemId(),
                  drop.getMin(),
                  drop.getMax(),
                  (int) drop.getChance(),
                  dropPeriod);
        }
      }
    }

    // Add spawns
    long millisToEventEnd = Duration.between(currentTime, eventPeriod.getEndDate()).toMillis();
    if (spawnList != null) {
      for (NpcSpawn spawn : spawnList) {
        addSpawn(
            spawn.npcId,
            spawn.loc.getX(),
            spawn.loc.getY(),
            spawn.loc.getZ(),
            spawn.loc.getHeading(),
            false,
            millisToEventEnd,
            false);
      }
    }

    // Send message on begin
    Broadcast.toAllOnlinePlayers(onEnterMsg);

    // Add announce for entering players
    AnnouncementsTable.getInstance()
        .addAnnouncement(new EventAnnouncement(eventPeriod, onEnterMsg));

    // Schedule event end (now only for message sending)
    ThreadPoolManager.getInstance().scheduleGeneral(new ScheduleEnd(), millisToEventEnd);
  }

  /** @return event period */
  public DateRange getEventPeriod() {
    return eventPeriod;
  }

  /** @return {@code true} if now is event period */
  public boolean isEventPeriod() {
    return eventPeriod.isWithinRange(LocalDateTime.now());
  }

  /** @return {@code true} if now is drop period */
  public boolean isDropPeriod() {
    return dropPeriod.isWithinRange(LocalDateTime.now());
  }

  private static class NpcSpawn {
    protected final Location loc;
    protected final int npcId;

    protected NpcSpawn(int pNpcId, Location spawnLoc) {
      loc = spawnLoc;
      npcId = pNpcId;
    }
  }

  protected class ScheduleStart implements Runnable {
    @Override
    public void run() {
      startEvent();
    }
  }

  protected class ScheduleEnd implements Runnable {
    @Override
    public void run() {
      // Send message on end
      Broadcast.toAllOnlinePlayers(endMsg);
    }
  }
}
