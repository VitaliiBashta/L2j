
package com.l2jserver.datapack.ai.npc.FameManager;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.UserInfo;

/**
 * Fame Manager AI.
 * @author St3eT
 */
public final class FameManager extends AbstractNpcAI {
	// Npc
	private static final int[] FAME_MANAGER = {
		36479, // Rapidus
		36480, // Scipio
	};
	// Misc
	private static final int MIN_LVL = 40;
	private static final int DECREASE_COST = 5000;
	private static final int REPUTATION_COST = 1000;
	private static final int MIN_CLAN_LVL = 5;
	private static final int CLASS_LVL = 2;
	
	public FameManager() {
		super(FameManager.class.getSimpleName(), "ai/npc");
		addStartNpc(FAME_MANAGER);
		addTalkId(FAME_MANAGER);
		addFirstTalkId(FAME_MANAGER);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
		String htmltext = null;
		switch (event) {
			case "36479.html":
			case "36479-02.html":
			case "36479-07.html":
			case "36480.html":
			case "36480-02.html":
			case "36480-07.html": {
				htmltext = event;
				break;
			}
			case "decreasePk": {
				if (player.getPkKills() > 0) {
					if ((player.getFame() >= DECREASE_COST) && (player.getLevel() >= MIN_LVL) && (player.getClassId().level() >= CLASS_LVL)) {
						player.setFame(player.getFame() - DECREASE_COST);
						player.setPkKills(player.getPkKills() - 1);
						player.sendPacket(new UserInfo(player));
						htmltext = npc.getId() + "-06.html";
					} else {
						htmltext = npc.getId() + "-01.html";
					}
				} else {
					htmltext = npc.getId() + "-05.html";
				}
				break;
			}
			case "clanRep": {
				if ((player.getClan() != null) && (player.getClan().getLevel() >= MIN_CLAN_LVL)) {
					if ((player.getFame() >= REPUTATION_COST) && (player.getLevel() >= MIN_LVL) && (player.getClassId().level() >= CLASS_LVL)) {
						player.setFame(player.getFame() - REPUTATION_COST);
						player.getClan().addReputationScore(50, true);
						player.sendPacket(new UserInfo(player));
						player.sendPacket(SystemMessageId.ACQUIRED_50_CLAN_FAME_POINTS);
						htmltext = npc.getId() + "-04.html";
					} else {
						htmltext = npc.getId() + "-01.html";
					}
				} else {
					htmltext = npc.getId() + "-03.html";
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player) {
		return ((player.getFame() > 0) && (player.getLevel() >= MIN_LVL) && (player.getClassId().level() >= CLASS_LVL)) ? npc.getId() + ".html" : npc.getId() + "-01.html";
	}
}