
package com.l2jserver.datapack.quests.Q10291_FireDragonDestroyer;

import com.l2jserver.gameserver.model.L2Party;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.model.quest.State;
import com.l2jserver.gameserver.util.Util;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class Q10291_FireDragonDestroyer extends Quest {
	// NPC
	private static final int KLEIN = 31540;
	// Monster
	private static final int VALAKAS = 29028;
	// Items
	private static final int FLOATING_STONE = 7267;
	private static final int POOR_NECKLACE = 15524;
	private static final int VALOR_NECKLACE = 15525;
	
	private static final int VALAKAS_SLAYER_CIRCLET = 8567;
	
	public Q10291_FireDragonDestroyer() {
		super(10291, Q10291_FireDragonDestroyer.class.getSimpleName(), "Fire Dragon Destroyer");
		addStartNpc(KLEIN);
		addTalkId(KLEIN);
		addKillId(VALAKAS);
		registerQuestItems(POOR_NECKLACE, VALOR_NECKLACE);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
		final QuestState st = getQuestState(player, false);
		if (st == null) {
			return getNoQuestMsg(player);
		}
		
		if (event.equals("31540-05.htm")) {
			st.startQuest();
			st.giveItems(POOR_NECKLACE, 1);
		}
		
		return event;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isSummon) {
		if (!player.isInParty()) {
			return super.onKill(npc, player, isSummon);
		}
		
		Function<L2PcInstance, Boolean> rewardCheck = p -> {
			if (Util.checkIfInRange(8000, npc, p, false)) {
				QuestState st = getQuestState(p, false);

				if ((st != null) && st.isCond(1) && st.hasQuestItems(POOR_NECKLACE)) {
					st.takeItems(POOR_NECKLACE, -1);
					st.giveItems(VALOR_NECKLACE, 1);
					st.setCond(2, true);
				}
			}
			return true;
		};

		// Rewards go only to command channel, not to a single party or player.
		L2Party party = player.getParty();
		if (party.isInCommandChannel()) {
			party.getCommandChannel().forEachMember(rewardCheck);
		} else {
			party.forEachMember(rewardCheck);
		}
		return super.onKill(npc, player, isSummon);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player) {
		String htmltext = getNoQuestMsg(player);
		final QuestState st = getQuestState(player, true);
		switch (st.getState()) {
			case State.CREATED: {
				if (player.getLevel() < 83) {
					htmltext = "31540-00.htm";
				} else {
					htmltext = st.hasQuestItems(FLOATING_STONE) ? "31540-02.htm" : "31540-01.htm";
				}
				break;
			}
			case State.STARTED: {
				if (st.isCond(1)) {
					if (st.hasQuestItems(POOR_NECKLACE)) {
						htmltext = "31540-06.html";
					} else {
						st.giveItems(POOR_NECKLACE, 1);
						htmltext = "31540-07.html";
					}
				} else if (st.isCond(2) && st.hasQuestItems(VALOR_NECKLACE)) {
					htmltext = "31540-08.html";
					st.giveAdena(126549, true);
					st.addExpAndSp(717291, 77397);
					st.giveItems(VALAKAS_SLAYER_CIRCLET, 1);
					st.exitQuest(false, true);
				}
				break;
			}
			case State.COMPLETED: {
				htmltext = "31540-09.html";
				break;
			}
		}
		
		return htmltext;
	}
}
