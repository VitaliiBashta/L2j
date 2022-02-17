package com.l2jserver.datapack.events.LoveYourGatekeeper;

import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.event.LongTimeEvent;
import com.l2jserver.gameserver.model.holders.SkillHolder;
import com.l2jserver.gameserver.model.itemcontainer.Inventory;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.SystemMessage;
import org.springframework.stereotype.Service;

@Service
public class LoveYourGatekeeper extends LongTimeEvent {
  // NPC
  private static final int GATEKEEPER = 32477;
  // Item
  private static final int GATEKEEPER_TRANSFORMATION_STICK = 12814;
  // Skills
  private static final SkillHolder TELEPORTER_TRANSFORM = new SkillHolder(5655);
  // Misc
  private static final int HOURS = 24;
  private static final int PRICE = 10000;
  private static final String REUSE = LoveYourGatekeeper.class.getSimpleName() + "_reuse";

  private LoveYourGatekeeper() {
    super(LoveYourGatekeeper.class.getSimpleName(), "events");
    addStartNpc(GATEKEEPER);
    addFirstTalkId(GATEKEEPER);
    addTalkId(GATEKEEPER);
  }

  @Override
  public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
    switch (event) {
      case "transform_stick":
        {
          if (player.getAdena() >= PRICE) {
            final long reuse = player.getVariables().getLong(REUSE, 0);
            if (reuse > System.currentTimeMillis()) {
              final long remainingTime = (reuse - System.currentTimeMillis()) / 1000;
              final int hours = (int) (remainingTime / 3600);
              final int minutes = (int) ((remainingTime % 3600) / 60);
              final SystemMessage sm =
                  SystemMessage.getSystemMessage(
                      SystemMessageId.AVAILABLE_AFTER_S1_S2_HOURS_S3_MINUTES);
              sm.addItemName(GATEKEEPER_TRANSFORMATION_STICK);
              sm.addInt(hours);
              sm.addInt(minutes);
              player.sendPacket(sm);
            } else {
              takeItems(player, Inventory.ADENA_ID, PRICE);
              giveItems(player, GATEKEEPER_TRANSFORMATION_STICK, 1);
              player.getVariables().set(REUSE, System.currentTimeMillis() + (HOURS * 3600000));
            }
          } else {
            return "32477-3.htm";
          }
          return null;
        }
      case "transform":
        {
          if (!player.isTransformed()) {
            player.doCast(TELEPORTER_TRANSFORM);
          }
          return null;
        }
    }
    return event;
  }

  @Override
  public String onFirstTalk(L2Npc npc, L2PcInstance player) {
    return "32477.htm";
  }
}
