
package com.l2jserver.datapack.hellbound.ai.zones.BaseTower;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.data.xml.impl.DoorData;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.base.ClassId;
import com.l2jserver.gameserver.model.holders.SkillHolder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class BaseTower extends AbstractNpcAI {
	// NPCs
	private static final int GUZEN = 22362;
	private static final int KENDAL = 32301;
	private static final int BODY_DESTROYER = 22363;
  // Skills
  private static final SkillHolder DEATH_WORD = new SkillHolder(5256);
	// Misc
	private static final Map<Integer, L2PcInstance> BODY_DESTROYER_TARGET_LIST = new ConcurrentHashMap<>();
	
	public BaseTower() {
		super(BaseTower.class.getSimpleName(), "hellbound/AI/Zones");
		addKillId(GUZEN);
		addKillId(BODY_DESTROYER);
		addFirstTalkId(KENDAL);
		addAggroRangeEnterId(BODY_DESTROYER);
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player) {
		final ClassId classId = player.getClassId();
		if (classId.equalsOrChildOf(ClassId.hellKnight) || classId.equalsOrChildOf(ClassId.soultaker)) {
			return "32301-02.htm";
		}
		return "32301-01.htm";
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
		if (event.equalsIgnoreCase("CLOSE")) {
			DoorData.getInstance().getDoor(20260004).closeMe();
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onAggroRangeEnter(L2Npc npc, L2PcInstance player, boolean isSummon) {
		if (!BODY_DESTROYER_TARGET_LIST.containsKey(npc.getObjectId())) {
			BODY_DESTROYER_TARGET_LIST.put(npc.getObjectId(), player);
			npc.setTarget(player);
			npc.doSimultaneousCast(DEATH_WORD);
		}
		return super.onAggroRangeEnter(npc, player, isSummon);
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon) {
		switch (npc.getId()) {
			case GUZEN: {
				// Should Kendal be despawned before Guzen's spawn? Or it will be crowd of Kendal's
				addSpawn(KENDAL, npc.getSpawn().getLocation(), false, npc.getSpawn().getRespawnDelay(), false);
				DoorData.getInstance().getDoor(20260003).openMe();
				DoorData.getInstance().getDoor(20260004).openMe();
				startQuestTimer("CLOSE", 60000, npc, null, false);
				break;
			}
			case BODY_DESTROYER: {
				if (BODY_DESTROYER_TARGET_LIST.containsKey(npc.getObjectId())) {
					final L2PcInstance pl = BODY_DESTROYER_TARGET_LIST.get(npc.getObjectId());
					if ((pl != null) && pl.isOnline() && !pl.isDead()) {
						pl.stopSkillEffects(true, DEATH_WORD.getSkillId());
					}
					BODY_DESTROYER_TARGET_LIST.remove(npc.getObjectId());
				}
				break;
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
}