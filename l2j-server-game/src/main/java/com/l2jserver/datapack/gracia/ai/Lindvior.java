
package com.l2jserver.datapack.gracia.ai;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.datatables.SpawnTable;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.NpcStringId;
import com.l2jserver.gameserver.network.clientpackets.Say2;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.GregorianCalendar;

@Service
public class Lindvior extends AbstractNpcAI {
	private static final int LINDVIOR_CAMERA = 18669;
	private static final int TOMARIS = 32552;
	private static final int ARTIUS = 32559;
	
	private static int LINDVIOR_SCENE_ID = 1;
	
	private static final int RESET_HOUR = 18;
	private static final int RESET_MIN = 58;
	private static final int RESET_DAY_1 = Calendar.TUESDAY;
	private static final int RESET_DAY_2 = Calendar.FRIDAY;
	
	private static boolean ALT_MODE = false;
	private static int ALT_MODE_MIN = 60; // schedule delay in minutes if ALT_MODE enabled
	
	private L2Npc _lindviorCamera = null;
	private L2Npc _tomaris = null;
	private L2Npc _artius = null;
	
	public Lindvior() {
		super(Lindvior.class.getSimpleName(), "gracia/AI");
		scheduleNextLindviorVisit();
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
		switch (event) {
			case "tomaris_shout1":
				broadcastNpcSay(npc, Say2.NPC_SHOUT, NpcStringId.HUH_THE_SKY_LOOKS_FUNNY_WHATS_THAT);
				break;
			case "artius_shout":
				broadcastNpcSay(npc, Say2.NPC_SHOUT, NpcStringId.A_POWERFUL_SUBORDINATE_IS_BEING_HELD_BY_THE_BARRIER_ORB_THIS_REACTION_MEANS);
				break;
			case "tomaris_shout2":
				broadcastNpcSay(npc, Say2.NPC_SHOUT, NpcStringId.BE_CAREFUL_SOMETHINGS_COMING);
				break;
			case "lindvior_scene":
				if (npc != null) {
					for (L2PcInstance pl : npc.getKnownList().getKnownPlayersInRadius(4000)) {
						if ((pl.getZ() >= 1100) && (pl.getZ() <= 3100)) {
							pl.showQuestMovie(LINDVIOR_SCENE_ID);
						}
					}
				}
				break;
			case "start":
				_lindviorCamera = SpawnTable.getInstance().findAny(LINDVIOR_CAMERA).getLastSpawn();
				_tomaris = SpawnTable.getInstance().findAny(TOMARIS).getLastSpawn();
				_artius = SpawnTable.getInstance().findAny(ARTIUS).getLastSpawn();
				
				startQuestTimer("tomaris_shout1", 1000, _tomaris, null);
				startQuestTimer("artius_shout", 60000, _artius, null);
				startQuestTimer("tomaris_shout2", 90000, _tomaris, null);
				startQuestTimer("lindvior_scene", 120000, _lindviorCamera, null);
				scheduleNextLindviorVisit();
				break;
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	public void scheduleNextLindviorVisit() {
		long delay = (ALT_MODE) ? ALT_MODE_MIN * 60000 : scheduleNextLindviorDate();
		startQuestTimer("start", delay, null, null);
	}
	
	protected long scheduleNextLindviorDate() {
		GregorianCalendar date = new GregorianCalendar();
		date.set(Calendar.MINUTE, RESET_MIN);
		date.set(Calendar.HOUR_OF_DAY, RESET_HOUR);
		if (System.currentTimeMillis() >= date.getTimeInMillis()) {
			date.add(Calendar.DAY_OF_WEEK, 1);
		}
		
		int dayOfWeek = date.get(Calendar.DAY_OF_WEEK);
		if (dayOfWeek <= RESET_DAY_1) {
			date.add(Calendar.DAY_OF_WEEK, RESET_DAY_1 - dayOfWeek);
		} else if (dayOfWeek <= RESET_DAY_2) {
			date.add(Calendar.DAY_OF_WEEK, RESET_DAY_2 - dayOfWeek);
		} else {
			date.add(Calendar.DAY_OF_WEEK, 1 + RESET_DAY_1);
		}
		return date.getTimeInMillis() - System.currentTimeMillis();
	}
}