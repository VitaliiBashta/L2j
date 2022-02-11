package com.l2jserver.datapack.handlers.actionhandlers;

import com.l2jserver.gameserver.ai.CtrlIntention;
import com.l2jserver.gameserver.cache.HtmCache;
import com.l2jserver.gameserver.enums.InstanceType;
import com.l2jserver.gameserver.handler.IActionHandler;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.actor.instance.L2StaticObjectInstance;
import com.l2jserver.gameserver.network.serverpackets.NpcHtmlMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class L2StaticObjectInstanceAction implements IActionHandler {

  private static final Logger LOG = LogManager.getLogger(L2StaticObjectInstanceAction.class);

  @Override
  public boolean action(
      final L2PcInstance activeChar, final L2Object target, final boolean interact) {
    final L2StaticObjectInstance staticObject = (L2StaticObjectInstance) target;
    if (staticObject.getType() < 0) {
      LOG.info(
          "L2StaticObjectInstance: StaticObject with invalid type! StaticObjectId: "
              + staticObject.getId());
    }

    // Check if the L2PcInstance already target the L2NpcInstance
    if (activeChar.getTarget() != staticObject) {
      // Set the target of the L2PcInstance activeChar
      activeChar.setTarget(staticObject);
    } else if (interact) {
      // Calculate the distance between the L2PcInstance and the L2NpcInstance
      if (!activeChar.isInsideRadius(staticObject, L2Npc.INTERACTION_DISTANCE, false, false)) {
        // Notify the L2PcInstance AI with AI_INTENTION_INTERACT
        activeChar.getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, staticObject);
      } else {
        if (staticObject.getType() == 2) {
          final String filename =
              (staticObject.getId() == 24230101)
                  ? "data/html/signboards/tomb_of_crystalgolem.htm"
                  : "data/html/signboards/pvp_signboard.htm";
          final String content =
              HtmCache.getInstance().getHtm(activeChar.getHtmlPrefix(), filename);
          final NpcHtmlMessage html = new NpcHtmlMessage(staticObject.getObjectId());

          if (content == null) {
            html.setHtml("<html><body>Signboard is missing:<br>" + filename + "</body></html>");
          } else {
            html.setHtml(content);
          }

          activeChar.sendPacket(html);
        } else if (staticObject.getType() == 0) {
          activeChar.sendPacket(staticObject.getMap());
        }
      }
    }
    return true;
  }

  @Override
  public InstanceType getInstanceType() {
    return InstanceType.L2StaticObjectInstance;
  }
}
