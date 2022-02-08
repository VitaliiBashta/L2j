
package com.l2jserver.datapack.quests.Q00510_AClansPrestige;

import com.l2jserver.gameserver.enums.audio.Sound;
import com.l2jserver.gameserver.model.L2Clan;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.model.quest.State;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.PledgeShowInfoUpdate;
import com.l2jserver.gameserver.network.serverpackets.SystemMessage;

/**
 * A Clan's Prestige (510)
 * @author Adry_85
 */
public class Q00510_AClansPrestige extends Quest {
	// NPC
	private static final int VALDIS = 31331;
	// Quest Item
	private static final int TYRANNOSAURUS_CLAW = 8767;
	
	private static final int[] MOBS = {
		22215,
		22216,
		22217
	};
	
	public Q00510_AClansPrestige() {
		super(510, Q00510_AClansPrestige.class.getSimpleName(), "A Clan's Prestige");
		addStartNpc(VALDIS);
		addTalkId(VALDIS);
		addKillId(MOBS);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
		QuestState st = getQuestState(player, false);
		if (st == null) {
			return getNoQuestMsg(player);
		}
		
		switch (event) {
			case "31331-3.html":
				st.startQuest();
				break;
			case "31331-6.html":
				st.exitQuest(true, true);
				break;
		}
		return event;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isSummon) {
		if (player.getClan() == null) {
			return null;
		}
		
		QuestState st = null;
		if (player.isClanLeader()) {
			st = getQuestState(player, false);
		} else {
			L2PcInstance pleader = player.getClan().getLeader().getPlayerInstance();
			if ((pleader != null) && player.isInsideRadius(pleader, 1500, true, false)) {
				st = getQuestState(pleader, false);
			}
		}
		
		if ((st != null) && st.isStarted()) {
			st.rewardItems(TYRANNOSAURUS_CLAW, 1);
			st.playSound(Sound.ITEMSOUND_QUEST_ITEMGET);
		}
		return null;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player) {
		String htmltext = getNoQuestMsg(player);
		final QuestState st = getQuestState(player, true);
		L2Clan clan = player.getClan();
		switch (st.getState()) {
			case State.CREATED:
				htmltext = ((clan == null) || !player.isClanLeader() || (clan.getLevel() < 5)) ? "31331-0.htm" : "31331-1.htm";
				break;
			case State.STARTED:
				if ((clan == null) || !player.isClanLeader()) {
					st.exitQuest(true);
					return "31331-8.html";
				}
				
				if (!st.hasQuestItems(TYRANNOSAURUS_CLAW)) {
					htmltext = "31331-4.html";
				} else {
					int count = (int) st.getQuestItemsCount(TYRANNOSAURUS_CLAW);
					int reward = (count < 10) ? (30 * count) : (59 + (30 * count));
					st.playSound(Sound.ITEMSOUND_QUEST_FANFARE_1);
					st.takeItems(TYRANNOSAURUS_CLAW, -1);
					clan.addReputationScore(reward, true);
					player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.CLAN_QUEST_COMPLETED_AND_S1_POINTS_GAINED).addInt(reward));
					clan.broadcastToOnlineMembers(new PledgeShowInfoUpdate(clan));
					htmltext = "31331-7.html";
				}
				break;
			default:
				break;
		}
		return htmltext;
	}
}
