
package com.l2jserver.datapack.ai.npc.Summons.Pets;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.L2Summon;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PetInstance;
import com.l2jserver.gameserver.model.events.EventType;
import com.l2jserver.gameserver.model.events.ListenerRegisterType;
import com.l2jserver.gameserver.model.events.annotations.RegisterEvent;
import com.l2jserver.gameserver.model.events.annotations.RegisterType;
import com.l2jserver.gameserver.model.events.impl.character.player.OnPlayerLogout;
import com.l2jserver.gameserver.model.holders.SkillHolder;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.SystemMessage;
import com.l2jserver.gameserver.util.Util;
import org.springframework.stereotype.Service;

@Service
public class BabyPets extends AbstractNpcAI {
	// NPCs
	private static final int[] BABY_PETS = {
		12780, // Baby Buffalo
		12781, // Baby Kookaburra
		12782, // Baby Cougar
	};
	
	// Skills
	private static final int HEAL_TRICK = 4717;
	private static final int GREATER_HEAL_TRICK = 4718;
	
	public BabyPets() {
		super(BabyPets.class.getSimpleName(), "ai/npc/Summons/Pets");
		addSummonSpawnId(BABY_PETS);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
		if (event.equals("CAST_HEAL") && (player != null)) {
			final L2PetInstance pet = (L2PetInstance) player.getSummon();
			
			if (pet != null) {
				if (getRandom(100) <= 25) {
					castHealSkill(pet, new SkillHolder(HEAL_TRICK, getHealLevel(pet)), 80);
				}
				
				if (getRandom(100) <= 75) {
					castHealSkill(pet, new SkillHolder(GREATER_HEAL_TRICK, getHealLevel(pet)), 15);
				}
			} else {
				cancelQuestTimer("CAST_HEAL", null, player);
			}
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@RegisterEvent(EventType.ON_PLAYER_LOGOUT)
	@RegisterType(ListenerRegisterType.GLOBAL)
	public void OnPlayerLogout(OnPlayerLogout event) {
		cancelQuestTimer("CAST_HEAL", null, event.getActiveChar());
	}
	
	@Override
	public void onSummonSpawn(L2Summon summon) {
		startQuestTimer("CAST_HEAL", 1000, null, summon.getOwner(), true);
	}
	
	private void castHealSkill(L2Summon summon, SkillHolder skill, int maxHpPer) {
		final L2PcInstance owner = summon.getOwner();
		if (!owner.isDead() && !summon.isHungry() && (((owner.getCurrentHp() / owner.getMaxHp()) * 100) < maxHpPer) && summon.checkDoCastConditions(skill.getSkill())) {
			final boolean previousFollowStatus = summon.getFollowStatus();
			
			if (!previousFollowStatus && !summon.isInsideRadius(owner, skill.getSkill().getCastRange(), true, true)) {
				return;
			}
			
			summon.setTarget(owner);
			summon.doCast(skill.getSkill());
			summon.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.PET_USES_S1).addSkillName(skill.getSkill()));
			
			if (previousFollowStatus != summon.getFollowStatus()) {
				summon.setFollowStatus(previousFollowStatus);
			}
		}
	}
	
	private int getHealLevel(L2Summon summon) {
		final int summonLevel = summon.getLevel();
		return Util.constrain(summonLevel < 70 ? (summonLevel / 10) : (7 + ((summonLevel - 70) / 5)), 1, 12);
	}
}