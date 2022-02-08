package com.l2jserver.gameserver.model.events.impl.character.npc;

import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.events.EventType;
import com.l2jserver.gameserver.model.events.impl.IBaseEvent;
import com.l2jserver.gameserver.model.skills.Skill;

import java.util.List;

public class OnNpcSkillSee implements IBaseEvent {
	private final L2Npc _npc;
	private final L2PcInstance _caster;
	private final Skill _skill;
	private final List<L2Object> _targets;
	private final boolean _isSummon;
	
	public OnNpcSkillSee(L2Npc npc, L2PcInstance caster, Skill skill, List<L2Object> targets, boolean isSummon) {
		_npc = npc;
		_caster = caster;
		_skill = skill;
		_targets = targets;
		_isSummon = isSummon;
	}
	
	public L2Npc getTarget() {
		return _npc;
	}
	
	public L2PcInstance getCaster() {
		return _caster;
	}
	
	public Skill getSkill() {
		return _skill;
	}
	
	public List<L2Object> getTargets() {
		return _targets;
	}
	
	public boolean isSummon() {
		return _isSummon;
	}
	
	@Override
	public EventType getType() {
		return EventType.ON_NPC_SKILL_SEE;
	}
}
