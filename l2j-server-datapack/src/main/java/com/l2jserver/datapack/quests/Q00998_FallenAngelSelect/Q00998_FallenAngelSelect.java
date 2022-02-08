
package com.l2jserver.datapack.quests.Q00998_FallenAngelSelect;

import com.l2jserver.datapack.quests.Q00141_ShadowFoxPart3.Q00141_ShadowFoxPart3;
import com.l2jserver.datapack.quests.Q00142_FallenAngelRequestOfDawn.Q00142_FallenAngelRequestOfDawn;
import com.l2jserver.datapack.quests.Q00143_FallenAngelRequestOfDusk.Q00143_FallenAngelRequestOfDusk;
import com.l2jserver.gameserver.instancemanager.QuestManager;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.model.quest.State;

/**
 * Fallen Angel Select (998 - Custom)<br>
 * NOTE: This quest is used for start quest 142 or 143
 * @author Nono
 */
public class Q00998_FallenAngelSelect extends Quest {
	// NPCs
	private static final int NATOOLS = 30894;
	// Misc
	private static final int MIN_LEVEL = 38;
	
	public Q00998_FallenAngelSelect() {
		super(998, Q00998_FallenAngelSelect.class.getSimpleName(), "Fallen Angel - Select");
		setIsCustom(true);
		addStartNpc(NATOOLS);
		addTalkId(NATOOLS);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
		final QuestState st = getQuestState(player, false);
		if (st == null) {
			return null;
		}
		
		switch (event) {
			case "30894-01.html":
			case "30894-02.html":
			case "30894-03.html":
				return event;
			case "dawn":
				startQuest(Q00142_FallenAngelRequestOfDawn.class.getSimpleName(), player);
				break;
			case "dusk":
				startQuest(Q00143_FallenAngelRequestOfDusk.class.getSimpleName(), player);
				break;
		}
		return null;
	}
	
	private void startQuest(String name, L2PcInstance player) {
		final Quest q = QuestManager.getInstance().getQuest(name);
		if (q != null) {
			q.newQuestState(player);
			q.notifyEvent("30894-01.html", null, player);
			player.getQuestState(getName()).setState(State.COMPLETED);
		}
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player) {
		final QuestState st = getQuestState(player, true);
		final QuestState qs = player.getQuestState(Q00141_ShadowFoxPart3.class.getSimpleName());
		if ((st == null) || !st.isStarted()) {
			return getNoQuestMsg(player);
		}
		return ((player.getLevel() >= MIN_LEVEL) && (qs != null) && qs.isCompleted()) ? "30894-01.html" : "30894-00.html";
	}
}