package com.l2jserver.gameserver.model.entity;

import com.l2jserver.gameserver.data.xml.impl.SkillTreesData;
import com.l2jserver.gameserver.model.L2SkillLearn;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.events.ListenersContainer;
import com.l2jserver.gameserver.model.holders.SkillHolder;
import com.l2jserver.gameserver.model.zone.type.L2ResidenceZone;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractResidence extends ListenersContainer {
	private final int _residenceId;
  private String name;

	private L2ResidenceZone _zone = null;
	private final List<SkillHolder> _residentialSkills = new ArrayList<>();
	
	public AbstractResidence(int residenceId) {
		_residenceId = residenceId;
		initResidentialSkills();
	}
	
	protected abstract void load();
	
	protected abstract void initResidenceZone();
	
	protected void initResidentialSkills() {
		final List<L2SkillLearn> residentialSkills = SkillTreesData.getInstance().getAvailableResidentialSkills(getResidenceId());
		for (L2SkillLearn s : residentialSkills) {
			_residentialSkills.add(new SkillHolder(s.getSkillId(), s.getSkillLevel()));
		}
	}
	
	public final int getResidenceId() {
		return _residenceId;
	}
	
	public final String getName() {
    return name;
	}
	
	// TODO: Remove it later when both castles and forts are loaded from same table.
	public final void setName(String name) {
    this.name = name;
	}
	
	public L2ResidenceZone getResidenceZone() {
		return _zone;
	}
	
	protected void setResidenceZone(L2ResidenceZone zone) {
		_zone = zone;
	}
	
	public final List<SkillHolder> getResidentialSkills() {
		return _residentialSkills;
	}
	
	public void giveResidentialSkills(L2PcInstance player) {
		if (!_residentialSkills.isEmpty()) {
			for (SkillHolder sh : _residentialSkills) {
				player.addSkill(sh.getSkill(), false);
			}
		}
	}
	
	public void removeResidentialSkills(L2PcInstance player) {
		if (!_residentialSkills.isEmpty()) {
			for (SkillHolder sh : _residentialSkills) {
				player.removeSkill(sh.getSkill(), false);
			}
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		return (obj instanceof AbstractResidence) && (((AbstractResidence) obj).getResidenceId() == getResidenceId());
	}
	
	@Override
	public String toString() {
		return getName() + "(" + getResidenceId() + ")";
	}
}
