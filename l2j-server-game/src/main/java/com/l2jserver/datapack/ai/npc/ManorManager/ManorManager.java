package com.l2jserver.datapack.ai.npc.ManorManager;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.instancemanager.CastleManorManager;
import com.l2jserver.gameserver.model.PcCondOverride;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2MerchantInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.events.EventType;
import com.l2jserver.gameserver.model.events.ListenerRegisterType;
import com.l2jserver.gameserver.model.events.annotations.Id;
import com.l2jserver.gameserver.model.events.annotations.RegisterEvent;
import com.l2jserver.gameserver.model.events.annotations.RegisterType;
import com.l2jserver.gameserver.model.events.impl.character.npc.OnNpcManorBypass;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.BuyListSeed;
import com.l2jserver.gameserver.network.serverpackets.ExShowCropInfo;
import com.l2jserver.gameserver.network.serverpackets.ExShowManorDefaultInfo;
import com.l2jserver.gameserver.network.serverpackets.ExShowProcureCropDetail;
import com.l2jserver.gameserver.network.serverpackets.ExShowSeedInfo;
import com.l2jserver.gameserver.network.serverpackets.ExShowSellCropList;
import com.l2jserver.gameserver.network.serverpackets.SystemMessage;
import org.springframework.stereotype.Service;

import static com.l2jserver.gameserver.config.Configuration.general;

@Service
public class ManorManager extends AbstractNpcAI {
  private static final int[] NPC = {
    35644, 35645, 35319, 35366, 36456, 35512, 35558, 35229, 35230, 35231, 35277, 35103, 35145, 35187
  };

  public ManorManager() {
    super(ManorManager.class.getSimpleName(), "ai/npc");
    addStartNpc(NPC);
    addFirstTalkId(NPC);
    addTalkId(NPC);
  }

  @Override
  public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
      return switch (event) {
          case "manager-help-01.htm", "manager-help-02.htm", "manager-help-03.htm" -> event;
          default -> null;
      };
  }

  @Override
  public String onFirstTalk(L2Npc npc, L2PcInstance player) {
    if (general().allowManor()) {
      final int castleId = npc.getTemplate().getParameters().getInt("manor_id", -1);
      if (!player.canOverrideCond(PcCondOverride.CASTLE_CONDITIONS)
          && player.isClanLeader()
          && (castleId == player.getClan().getCastleId())) {
        return "manager-lord.htm";
      }
      return "manager.htm";
    }
    return getHtm(player.getHtmlPrefix(), "data/html/npcdefault.htm");
  }

  @RegisterEvent(EventType.ON_NPC_MANOR_BYPASS)
  @RegisterType(ListenerRegisterType.NPC)
  @Id({
    35644, 35645, 35319, 35366, 36456, 35512, 35558, 35229, 35230, 35231, 35277, 35103, 35145, 35187
  })
  public void onNpcManorBypass(OnNpcManorBypass evt) {
    final L2PcInstance player = evt.getActiveChar();
    if (CastleManorManager.getInstance().isUnderMaintenance()) {
      player.sendPacket(SystemMessageId.THE_MANOR_SYSTEM_IS_CURRENTLY_UNDER_MAINTENANCE);
      return;
    }

    final L2Npc npc = evt.getTarget();
    final int templateId = npc.getTemplate().getParameters().getInt("manor_id", -1);
    final int castleId = (evt.getManorId() == -1) ? templateId : evt.getManorId();
	  switch (evt.getRequest()) {
		  case 1 -> // Seed purchase
				  {
					  if (templateId != castleId) {
						  player.sendPacket(
								  SystemMessage.getSystemMessage(
												  SystemMessageId.HERE_YOU_CAN_BUY_ONLY_SEEDS_OF_S1_MANOR)
										  .addCastleId(templateId));
						  return;
					  }
					  player.sendPacket(new BuyListSeed(player.getAdena(), castleId));
				  }
		  case 2 -> // Crop sales
				  player.sendPacket(new ExShowSellCropList(player.getInventory(), castleId));
		  case 3 -> // Seed info
				  player.sendPacket(new ExShowSeedInfo(castleId, evt.isNextPeriod(), false));
		  case 4 -> // Crop info
				  player.sendPacket(new ExShowCropInfo(castleId, evt.isNextPeriod(), false));
		  case 5 -> // Basic info
				  player.sendPacket(new ExShowManorDefaultInfo(false));
		  case 6 -> // Buy harvester
				  ((L2MerchantInstance) npc).showBuyWindow(player, 300000 + npc.getId());
		  case 9 -> // Edit sales (Crop sales)
				  player.sendPacket(new ExShowProcureCropDetail(evt.getManorId()));
		  default -> LOG.warn(
				  getClass().getSimpleName()
						  + ": Player "
						  + player.getName()
						  + " ("
						  + player.getObjectId()
						  + ") send unknown request id "
						  + evt.getRequest()
						  + "!");
	  }
  }
}
