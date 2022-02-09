
package com.l2jserver.datapack.quests.Q00147_PathtoBecominganEliteMercenary;

import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.model.quest.State;

/**
 * Path to Becoming an Elite Mercenary (147)
 * @author Gnacik
 * @version 2010-09-30 Based on official server Franz
 */
public class Q00147_PathtoBecominganEliteMercenary extends Quest {
	// NPCs
	private static final int[] MERC = {
		36481,
		36482,
		36483,
		36484,
		36485,
		36486,
		36487,
		36488,
		36489
	};
	// Items
	private static final int ORDINARY_CERTIFICATE = 13766;
	private static final int ELITE_CERTIFICATE = 13767;
	
	public Q00147_PathtoBecominganEliteMercenary() {
		super(147, Q00147_PathtoBecominganEliteMercenary.class.getSimpleName(), "Path to Becoming an Elite Mercenary");
		addStartNpc(MERC);
		addTalkId(MERC);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
		String htmltext = event;
		final QuestState st = getQuestState(player, false);
		
		if (st == null) {
			return htmltext;
		}
		
		if (event.equalsIgnoreCase("elite-02.htm")) {
			if (st.hasQuestItems(ORDINARY_CERTIFICATE)) {
				return "elite-02a.htm";
			}
			st.giveItems(ORDINARY_CERTIFICATE, 1);
		} else if (event.equalsIgnoreCase("elite-04.htm")) {
			st.startQuest();
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player) {
		String htmltext = getNoQuestMsg(player);
		final QuestState st = getQuestState(player, true);
		switch (st.getState()) {
			case State.CREATED:
				if ((player.getClan() != null) && (player.getClan().getCastleId() > 0)) {
					htmltext = "castle.htm";
				} else {
					htmltext = "elite-01.htm";
				}
				break;
			case State.STARTED:
				if (st.getCond() < 4) {
					htmltext = "elite-05.htm";
				} else if (st.isCond(4)) {
					st.takeItems(ORDINARY_CERTIFICATE, -1);
					st.giveItems(ELITE_CERTIFICATE, 1);
					st.exitQuest(false);
					htmltext = "elite-06.htm";
				}
				break;
			case State.COMPLETED:
				htmltext = getAlreadyCompletedMsg(player);
				break;
		}
		return htmltext;
	}
}
