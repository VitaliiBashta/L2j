
package com.l2jserver.datapack.gracia.ai.npc.Nemo;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.datapack.gracia.ai.Maguen;
import com.l2jserver.gameserver.ai.CtrlIntention;
import com.l2jserver.gameserver.instancemanager.QuestManager;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.network.NpcStringId;
import org.springframework.stereotype.Service;

@Service
public class Nemo extends AbstractNpcAI {
	// NPC
	private static final int NEMO = 32735; // Nemo
	private static final int MAGUEN = 18839; // Wild Maguen
	// Items
	private static final int COLLECTOR = 15487; // Maguen Plasma Collector
	// Misc
	private static final int MAXIMUM_MAGUEN = 18; // Maximum maguens in one time
	
	public Nemo() {
		super(Nemo.class.getSimpleName(), "gracia/AI/NPC");
		addStartNpc(NEMO);
		addFirstTalkId(NEMO);
		addTalkId(NEMO);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
		String htmltext = null;
		switch (event) {
			case "32735-01.html": {
				htmltext = event;
				break;
			}
			case "giveCollector": {
				if (hasQuestItems(player, COLLECTOR)) {
					htmltext = "32735-03.html";
				} else if (!player.isInventoryUnder90(false)) {
					htmltext = "32735-04.html";
				} else {
					htmltext = "32735-02.html";
					giveItems(player, COLLECTOR, 1);
				}
				break;
			}
			case "summonMaguen": {
				if ((player.getVariables().getInt("TEST_MAGUEN", 0) == 0) && (npc.getScriptValue() < MAXIMUM_MAGUEN)) {
					final L2Npc maguen = addSpawn(MAGUEN, npc.getLocation(), true, 60000, true);
					maguen.getVariables().set("SUMMON_PLAYER", player);
					maguen.getVariables().set("SPAWNED_NPC", npc);
					maguen.getVariables().set("TEST_MAGUEN", 1);
					player.getVariables().set("TEST_MAGUEN", 1);
					maguen.setTitle(player.getName());
					maguen.setIsRunning(true);
					maguen.getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, player);
					maguen.broadcastStatusUpdate();
					showOnScreenMsg(player, NpcStringId.MAGUEN_APPEARANCE, 2, 4000);
					maguenAi().startQuestTimer("DIST_CHECK_TIMER", 1000, maguen, player);
					npc.setScriptValue(npc.getScriptValue() + 1);
					htmltext = "32735-05.html";
				} else {
					htmltext = "32735-06.html";
				}
				break;
			}
			case "DECREASE_COUNT": {
				final L2Npc spawnedNpc = npc.getVariables().getObject("SPAWNED_NPC", L2Npc.class);
				if ((spawnedNpc != null) && (spawnedNpc.getScriptValue() > 0)) {
					player.getVariables().remove("TEST_MAGUEN");
					spawnedNpc.setScriptValue(spawnedNpc.getScriptValue() - 1);
				}
			}
		}
		return htmltext;
	}
	
	private Quest maguenAi() {
		return QuestManager.getInstance().getQuest(Maguen.class.getSimpleName());
	}
}