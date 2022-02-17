
package com.l2jserver.datapack.quests.Q10278_MutatedKaneusHeine;

import com.l2jserver.gameserver.enums.audio.Sound;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.model.quest.State;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class Q10278_MutatedKaneusHeine extends Quest {
	// NPCs
	private static final int GOSTA = 30916;
	private static final int MINEVIA = 30907;
	private static final int BLADE_OTIS = 18562;
	private static final int WEIRD_BUNEI = 18564;
	// Items
	private static final int TISSUE_BO = 13834;
	private static final int TISSUE_WB = 13835;
	
	public Q10278_MutatedKaneusHeine() {
		super(10278, Q10278_MutatedKaneusHeine.class.getSimpleName(), "Mutated Kaneus - Heine");
		addStartNpc(GOSTA);
		addTalkId(GOSTA, MINEVIA);
		addKillId(BLADE_OTIS, WEIRD_BUNEI);
		registerQuestItems(TISSUE_BO, TISSUE_WB);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
		final QuestState st = getQuestState(player, false);
		if (st == null) {
			return getNoQuestMsg(player);
		}
		
		switch (event) {
			case "30916-03.htm":
				st.startQuest();
				break;
			case "30907-03.htm":
				st.giveAdena(50000, true);
				st.exitQuest(false, true);
				break;
		}
		return event;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon) {
		QuestState st = getQuestState(killer, false);
		if (st == null) {
			return null;
		}
		
		final int npcId = npc.getId();
		if (killer.getParty() != null) {
			final List<QuestState> PartyMembers = new ArrayList<>();
			for (L2PcInstance member : killer.getParty().getMembers()) {
				st = getQuestState(member, false);
				if ((st != null) && st.isStarted() && (((npcId == BLADE_OTIS) && !st.hasQuestItems(TISSUE_BO)) || ((npcId == WEIRD_BUNEI) && !st.hasQuestItems(TISSUE_WB)))) {
					PartyMembers.add(st);
				}
			}
			
			if (!PartyMembers.isEmpty()) {
				rewardItem(npcId, PartyMembers.get(getRandom(PartyMembers.size())));
			}
		} else if (st.isStarted()) {
			rewardItem(npcId, st);
		}
		return null;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player) {
		String htmltext = getNoQuestMsg(player);
		final QuestState st = getQuestState(player, true);
		switch (npc.getId()) {
			case GOSTA:
				switch (st.getState()) {
					case State.CREATED:
						htmltext = (player.getLevel() > 37) ? "30916-01.htm" : "30916-00.htm";
						break;
					case State.STARTED:
						htmltext = (st.hasQuestItems(TISSUE_BO) && st.hasQuestItems(TISSUE_WB)) ? "30916-05.htm" : "30916-04.htm";
						break;
					case State.COMPLETED:
						htmltext = "30916-06.htm";
						break;
				}
				break;
			case MINEVIA:
				switch (st.getState()) {
					case State.STARTED:
						htmltext = (st.hasQuestItems(TISSUE_BO) && st.hasQuestItems(TISSUE_WB)) ? "30907-02.htm" : "30907-01.htm";
						break;
					case State.COMPLETED:
						htmltext = getAlreadyCompletedMsg(player);
						break;
					default:
						break;
				}
				break;
		}
		return htmltext;
	}
	
	/**
	 * @param npcId the ID of the killed monster
	 * @param st the quest state of the killer or party member
	 */
	private final void rewardItem(int npcId, QuestState st) {
		if ((npcId == BLADE_OTIS) && !st.hasQuestItems(TISSUE_BO)) {
			st.giveItems(TISSUE_BO, 1);
			st.playSound(Sound.ITEMSOUND_QUEST_ITEMGET);
		} else if ((npcId == WEIRD_BUNEI) && !st.hasQuestItems(TISSUE_WB)) {
			st.giveItems(TISSUE_WB, 1);
			st.playSound(Sound.ITEMSOUND_QUEST_ITEMGET);
		}
	}
}
