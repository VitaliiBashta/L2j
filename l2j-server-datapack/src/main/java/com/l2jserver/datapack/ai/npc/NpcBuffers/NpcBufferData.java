
package com.l2jserver.datapack.ai.npc.NpcBuffers;

import java.util.ArrayList;
import java.util.List;

/**
 * @author UnAfraid
 */
public class NpcBufferData {
	private final int _id;
	private final List<NpcBufferSkillData> _skills = new ArrayList<>();
	
	public NpcBufferData(int id) {
		_id = id;
	}
	
	public int getId() {
		return _id;
	}
	
	public void addSkill(NpcBufferSkillData skill) {
		_skills.add(skill);
	}
	
	public List<NpcBufferSkillData> getSkills() {
		return _skills;
	}
}
