
package com.l2jserver.datapack.hellbound.instancess.RankuFloor;

import com.l2jserver.datapack.instances.AbstractInstance;
import com.l2jserver.gameserver.instancemanager.InstanceManager;
import com.l2jserver.gameserver.model.L2Party;
import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.PcCondOverride;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.entity.Instance;
import com.l2jserver.gameserver.model.instancezone.InstanceWorld;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.SystemMessage;
import com.l2jserver.gameserver.util.Util;
import org.springframework.stereotype.Service;

@Service
public class RankuFloor extends AbstractInstance {
	protected class RFWorld extends InstanceWorld {
		
	}
	
	// NPCs
	private static final int GK_9 = 32752;
	private static final int CUBE = 32374;
	private static final int RANKU = 25542;
	// Item
	private static final int SEAL_BREAKER_10 = 15516;
	// Locations
	private static final Location ENTRY_POINT = new Location(-19008, 277024, -15000);
	private static final Location EXIT_POINT = new Location(-19008, 277122, -13376);
	// Misc
	private static final int TEMPLATE_ID = 143;
	private static final int MIN_LV = 78;
	
	public RankuFloor() {
		super(RankuFloor.class.getSimpleName(), "hellbound/Instances");
		addStartNpc(GK_9, CUBE);
		addTalkId(GK_9, CUBE);
		addKillId(RANKU);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player) {
		String htmltext = null;
		
		if (npc.getId() == GK_9) {
			if (!player.canOverrideCond(PcCondOverride.INSTANCE_CONDITIONS)) {
				if (player.getParty() == null) {
					htmltext = "gk-noparty.htm";
				} else if (!player.getParty().isLeader(player)) {
					htmltext = "gk-noleader.htm";
				}
			}
			
			if (htmltext == null) {
				enterInstance(player, new RFWorld(), "Ranku.xml", TEMPLATE_ID);
			}
		} else if (npc.getId() == CUBE) {
			final InstanceWorld world = InstanceManager.getInstance().getWorld(npc.getInstanceId());
			if (world instanceof RFWorld) {
				teleportPlayer(player, EXIT_POINT, 0);
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon) {
		final int instanceId = npc.getInstanceId();
		if (instanceId > 0) {
			final Instance inst = InstanceManager.getInstance().getInstance(instanceId);
			final InstanceWorld world = InstanceManager.getInstance().getWorld(npc.getInstanceId());
			inst.setExitLoc(EXIT_POINT);
			finishInstance(world);
			addSpawn(CUBE, -19056, 278732, -15000, 0, false, 0, false, instanceId);
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	protected boolean checkConditions(L2PcInstance player) {
		if (player.canOverrideCond(PcCondOverride.INSTANCE_CONDITIONS)) {
			return true;
		}
		
		final L2Party party = player.getParty();
		
		if ((party == null) || !party.isLeader(player)) {
			player.sendPacket(SystemMessageId.ONLY_PARTY_LEADER_CAN_ENTER);
			return false;
		}
		
		for (L2PcInstance partyMember : party.getMembers()) {
			if (partyMember.getLevel() < MIN_LV) {
				party.broadcastPacket(SystemMessage.getSystemMessage(SystemMessageId.C1_S_LEVEL_REQUIREMENT_IS_NOT_SUFFICIENT_AND_CANNOT_BE_ENTERED).addPcName(partyMember));
				return false;
			}
			
			if (!Util.checkIfInRange(500, player, partyMember, true)) {
				party.broadcastPacket(SystemMessage.getSystemMessage(SystemMessageId.C1_IS_IN_A_LOCATION_WHICH_CANNOT_BE_ENTERED_THEREFORE_IT_CANNOT_BE_PROCESSED).addPcName(partyMember));
				return false;
			}
			
			if (InstanceManager.getInstance().getPlayerWorld(player) != null) {
				party.broadcastPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_ENTERED_ANOTHER_INSTANT_ZONE_THEREFORE_YOU_CANNOT_ENTER_CORRESPONDING_DUNGEON).addPcName(partyMember));
				return false;
			}
			
			final Long reenterTime = InstanceManager.getInstance().getInstanceTime(partyMember.getObjectId(), TEMPLATE_ID);
			if (System.currentTimeMillis() < reenterTime) {
				party.broadcastPacket(SystemMessage.getSystemMessage(SystemMessageId.C1_MAY_NOT_RE_ENTER_YET).addPcName(partyMember));
				return false;
			}
			
			if (partyMember.getInventory().getInventoryItemCount(SEAL_BREAKER_10, -1, false) < 1) {
				party.broadcastPacket(SystemMessage.getSystemMessage(SystemMessageId.C1_S_QUEST_REQUIREMENT_IS_NOT_SUFFICIENT_AND_CANNOT_BE_ENTERED).addPcName(partyMember));
				return false;
			}
		}
		return true;
	}
	
	@Override
	public void onEnterInstance(L2PcInstance player, InstanceWorld world, boolean firstEntrance) {
		if (firstEntrance) {
			if (player.getParty() == null) {
				teleportPlayer(player, ENTRY_POINT, world.getInstanceId());
				player.destroyItemByItemId("Quest", SEAL_BREAKER_10, 1, null, true);
				world.addAllowed(player.getObjectId());
			} else {
				for (L2PcInstance partyMember : player.getParty().getMembers()) {
					teleportPlayer(partyMember, ENTRY_POINT, world.getInstanceId());
					partyMember.destroyItemByItemId("Quest", SEAL_BREAKER_10, 1, null, true);
					world.addAllowed(partyMember.getObjectId());
				}
			}
		} else {
			teleportPlayer(player, ENTRY_POINT, world.getInstanceId());
		}
	}
}
