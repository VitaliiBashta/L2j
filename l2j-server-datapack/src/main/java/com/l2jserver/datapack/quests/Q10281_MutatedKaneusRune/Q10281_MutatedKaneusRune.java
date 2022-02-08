
package com.l2jserver.datapack.quests.Q10281_MutatedKaneusRune;

import java.util.ArrayList;
import java.util.List;

import com.l2jserver.gameserver.enums.audio.Sound;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.model.quest.State;

/**
 * Mutated Kaneus - Rune (10281)<br>
 * Original Jython script by Gnacik on 2010-06-29.
 * @author nonom
 */
public class Q10281_MutatedKaneusRune extends Quest {
	// NPCs
	private static final int MATHIAS = 31340;
	private static final int KAYAN = 31335;
	private static final int WHITE_ALLOSCE = 18577;
	// Item
	private static final int TISSUE_WA = 13840;
	
	public Q10281_MutatedKaneusRune() {
		super(10281, Q10281_MutatedKaneusRune.class.getSimpleName(), "Mutated Kaneus - Rune");
		addStartNpc(MATHIAS);
		addTalkId(MATHIAS, KAYAN);
		addKillId(WHITE_ALLOSCE);
		registerQuestItems(TISSUE_WA);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
		final QuestState st = getQuestState(player, false);
		if (st == null) {
			return getNoQuestMsg(player);
		}
		
		switch (event) {
			case "31340-03.htm":
				st.startQuest();
				break;
			case "31335-03.htm":
				st.giveAdena(360000, true);
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
				if ((st != null) && st.isStarted() && !st.hasQuestItems(TISSUE_WA)) {
					PartyMembers.add(st);
				}
			}
			
			if (!PartyMembers.isEmpty()) {
				rewardItem(npcId, PartyMembers.get(getRandom(PartyMembers.size())));
			}
		} else if (st.isStarted() && !st.hasQuestItems(TISSUE_WA)) {
			rewardItem(npcId, st);
		}
		return null;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player) {
		String htmltext = getNoQuestMsg(player);
		final QuestState st = getQuestState(player, true);
		switch (npc.getId()) {
			case MATHIAS:
				switch (st.getState()) {
					case State.CREATED:
						htmltext = (player.getLevel() > 67) ? "31340-01.htm" : "31340-00.htm";
						break;
					case State.STARTED:
						htmltext = st.hasQuestItems(TISSUE_WA) ? "31340-05.htm" : "31340-04.htm";
						break;
					case State.COMPLETED:
						htmltext = "31340-06.htm";
						break;
				}
				break;
			case KAYAN:
				switch (st.getState()) {
					case State.STARTED:
						htmltext = st.hasQuestItems(TISSUE_WA) ? "31335-02.htm" : "31335-01.htm";
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
		st.giveItems(TISSUE_WA, 1);
		st.playSound(Sound.ITEMSOUND_QUEST_ITEMGET);
	}
}
