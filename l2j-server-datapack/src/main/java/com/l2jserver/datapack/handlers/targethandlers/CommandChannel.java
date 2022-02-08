package com.l2jserver.datapack.handlers.targethandlers;

import com.l2jserver.gameserver.handler.ITargetTypeHandler;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.L2Party;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.skills.Skill;
import com.l2jserver.gameserver.model.skills.targets.TargetType;

import java.util.ArrayList;
import java.util.List;

public class CommandChannel implements ITargetTypeHandler {
	@Override
	public List<L2Object> getTargetList(Skill skill, L2Character activeChar, boolean onlyFirst, L2Character target) {
		List<L2Object> targetList = new ArrayList<>();
		final L2PcInstance player = activeChar.getActingPlayer();
		if (player == null) {
			return EMPTY_TARGET_LIST;
		}
		
		targetList.add(player);
		
		final int radius = skill.getAffectRange();
		final L2Party party = player.getParty();
		final boolean hasChannel = (party != null) && party.isInCommandChannel();
		
		if (Skill.addSummon(activeChar, player, radius, false)) {
			targetList.add(player.getSummon());
		}
		
		// if player in not in party
		if (party == null) {
			return targetList;
		}
		
		// Get all visible objects in a spherical area near the L2Character
		int maxTargets = skill.getAffectLimit();
		final List<L2PcInstance> members = hasChannel ? party.getCommandChannel().getMembers() : party.getMembers();
		
		for (L2PcInstance member : members) {
			if (activeChar == member) {
				continue;
			}
			
			if (Skill.addCharacter(activeChar, member, radius, false)) {
				targetList.add(member);
				if (targetList.size() >= maxTargets) {
					break;
				}
			}
		}
		
		return targetList;
	}
	
	@Override
	public Enum<TargetType> getTargetType() {
		return TargetType.COMMAND_CHANNEL;
	}
}
