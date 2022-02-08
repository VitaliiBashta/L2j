
package com.l2jserver.datapack.events.HeavyMedal;

import com.l2jserver.gameserver.enums.audio.Sound;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.event.LongTimeEvent;

/**
 * Heavy Medals event AI.
 * @author Gnacik
 */
public final class HeavyMedal extends LongTimeEvent {
	private final static int CAT_ROY = 31228;
	private final static int CAT_WINNIE = 31229;
	private final static int GLITTERING_MEDAL = 6393;
	
	private final static int WIN_CHANCE = 50;
	
	private final static int[] MEDALS = {
		5,
		10,
		20,
		40
	};
	private final static int[] BADGES = {
		6399,
		6400,
		6401,
		6402
	};
	
	private HeavyMedal() {
		super(HeavyMedal.class.getSimpleName(), "events");
		addStartNpc(CAT_ROY);
		addStartNpc(CAT_WINNIE);
		addTalkId(CAT_ROY);
		addTalkId(CAT_WINNIE);
		addFirstTalkId(CAT_ROY);
		addFirstTalkId(CAT_WINNIE);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
		String htmltext = event;
		int level = checkLevel(player);
		
		if (event.equalsIgnoreCase("game")) {
			htmltext = getQuestItemsCount(player, GLITTERING_MEDAL) < MEDALS[level] ? "31229-no.htm" : "31229-game.htm";
		} else if (event.equalsIgnoreCase("heads") || event.equalsIgnoreCase("tails")) {
			if (getQuestItemsCount(player, GLITTERING_MEDAL) < MEDALS[level]) {
				htmltext = "31229-" + event.toLowerCase() + "-10.htm";
			} else {
				takeItems(player, GLITTERING_MEDAL, MEDALS[level]);
				
				if (getRandom(100) > WIN_CHANCE) {
					level = 0;
				} else {
					if (level > 0) {
						takeItems(player, BADGES[level - 1], -1);
					}
					giveItems(player, BADGES[level], 1);
					playSound(player, Sound.ITEMSOUND_QUEST_ITEMGET);
					level++;
				}
				htmltext = "31229-" + event.toLowerCase() + "-" + String.valueOf(level) + ".htm";
			}
		} else if (event.equalsIgnoreCase("talk")) {
			htmltext = String.valueOf(npc.getId()) + "-lvl-" + String.valueOf(level) + ".htm";
		}
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player) {
		if (player.getQuestState(getName()) == null) {
			newQuestState(player);
		}
		return npc.getId() + ".htm";
	}
	
	public int checkLevel(L2PcInstance player) {
		int _lev = 0;
		if (hasQuestItems(player, 6402)) {
			_lev = 4;
		} else if (hasQuestItems(player, 6401)) {
			_lev = 3;
		} else if (hasQuestItems(player, 6400)) {
			_lev = 2;
		} else if (hasQuestItems(player, 6399)) {
			_lev = 1;
		}
		return _lev;
	}
	
	public static void main(String[] args) {
		new HeavyMedal();
	}
}
