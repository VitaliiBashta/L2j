package com.l2jserver.gameserver.model.actor.tasks.character;

import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.skills.Skill;

import java.util.List;

public final class MagicUseTask implements Runnable {
	private final L2Character _character;
	private List<L2Object> _targets;
	private final Skill _skill;
	private int _count;
	private int _skillTime;
	private int _phase;
	private final boolean _simultaneously;
	
	public MagicUseTask(L2Character character, List<L2Object> targets, Skill s, int hit, boolean simultaneous) {
		_character = character;
		_targets = targets;
		_skill = s;
		_count = 0;
		_phase = 1;
		_skillTime = hit;
		_simultaneously = simultaneous;
	}
	
	@Override
	public void run() {
		if (_character == null) {
			return;
		}
		switch (_phase) {
			case 1 -> _character.onMagicLaunchedTimer(this);
			case 2 -> _character.onMagicHitTimer(this);
			case 3 -> _character.onMagicFinalizer(this);
		}
	}
	
	public int getCount() {
		return _count;
	}
	
	public int getPhase() {
		return _phase;
	}
	
	public Skill getSkill() {
		return _skill;
	}
	
	public int getSkillTime() {
		return _skillTime;
	}
	
	public List<L2Object> getTargets() {
		return _targets;
	}
	
	public boolean isSimultaneous() {
		return _simultaneously;
	}
	
	public void setCount(int count) {
		_count = count;
	}
	
	public void setPhase(int phase) {
		_phase = phase;
	}
	
	public void setSkillTime(int skillTime) {
		_skillTime = skillTime;
	}
	
	public void setTargets(List<L2Object> targets) {
		_targets = targets;
	}
}