
package com.l2jserver.datapack.gracia.ai.npc.Nottingale;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.datapack.quests.Q10273_GoodDayToFly.Q10273_GoodDayToFly;
import com.l2jserver.gameserver.instancemanager.AirShipManager;
import com.l2jserver.gameserver.model.ClanPrivilege;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.network.serverpackets.RadarControl;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class Nottingale extends AbstractNpcAI {
	// NPC
	private static final int NOTTINGALE = 32627;
	// Misc
	private static final Map<Integer, RadarControl> RADARS = new HashMap<>();
	static {
		RADARS.put(2, new RadarControl(0, -184545, 243120, 1581, 2));
		RADARS.put(5, new RadarControl(0, -192361, 254528, 3598, 1));
		RADARS.put(6, new RadarControl(0, -174600, 219711, 4424, 1));
		RADARS.put(7, new RadarControl(0, -181989, 208968, 4424, 1));
		RADARS.put(8, new RadarControl(0, -252898, 235845, 5343, 1));
		RADARS.put(9, new RadarControl(0, -212819, 209813, 4288, 1));
		RADARS.put(10, new RadarControl(0, -246899, 251918, 4352, 1));
	}
	
	public Nottingale() {
		super(Nottingale.class.getSimpleName(), "gracia/AI/NPC");
		addStartNpc(NOTTINGALE);
		addTalkId(NOTTINGALE);
		addFirstTalkId(NOTTINGALE);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
		String htmltext = null;
		switch (event) {
			case "32627-02.html":
			case "32627-03.html":
			case "32627-04.html": {
				if (player.getClan() != null) {
					if (player.hasClanPrivilege(ClanPrivilege.CL_SUMMON_AIRSHIP) && AirShipManager.getInstance().hasAirShipLicense(player.getClanId()) && !AirShipManager.getInstance().hasAirShip(player.getClanId())) {
						htmltext = event;
					} else {
						final QuestState st = player.getQuestState(Q10273_GoodDayToFly.class.getSimpleName());
						if ((st != null) && st.isCompleted()) {
							htmltext = event;
						} else {
							player.sendPacket(RADARS.get(2));
							htmltext = "32627-01.html";
						}
					}
				} else {
					final QuestState st = player.getQuestState(Q10273_GoodDayToFly.class.getSimpleName());
					if ((st != null) && st.isCompleted()) {
						htmltext = event;
					} else {
						player.sendPacket(RADARS.get(2));
						htmltext = "32627-01.html";
					}
				}
				break;
			}
			case "32627-05.html":
			case "32627-06.html":
			case "32627-07.html":
			case "32627-08.html":
			case "32627-09.html":
			case "32627-10.html": {
				player.sendPacket(RADARS.get(Integer.valueOf(event.substring(6, 8))));
				htmltext = event;
				break;
			}
		}
		return htmltext;
	}
}
