package com.l2jserver.gameserver.model;

import com.l2jserver.commons.util.Rnd;
import com.l2jserver.gameserver.data.sql.impl.TerritoryTable;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2ControllableMobInstance;
import com.l2jserver.gameserver.model.actor.templates.L2NpcTemplate;

import java.util.logging.Level;

import static com.l2jserver.gameserver.config.Configuration.general;

public class L2GroupSpawn extends L2Spawn {
	private final L2NpcTemplate _template;
	
	public L2GroupSpawn(L2NpcTemplate mobTemplate) throws SecurityException, ClassNotFoundException, NoSuchMethodException {
		super(mobTemplate);
		_template = mobTemplate;
		
		setAmount(1);
	}
	
	public L2Npc doGroupSpawn() {
		try {
			if (_template.isType("L2Pet") || _template.isType("L2Minion")) {
				return null;
			}
			
			int newLocX = 0;
			int newLocY = 0;
			int newLocZ = 0;
			
			if ((getX() == 0) && (getY() == 0)) {
				if (getLocationId() == 0) {
					return null;
				}
				
				final Location location = TerritoryTable.getInstance().getRandomPoint(getLocationId());
				if (location != null) {
					newLocX = location.getX();
					newLocY = location.getY();
					newLocZ = location.getZ();
				}
			} else {
				newLocX = getX();
				newLocY = getY();
				newLocZ = getZ();
			}
			
			final L2Npc mob = new L2ControllableMobInstance(_template);
			mob.setCurrentHpMp(mob.getMaxHp(), mob.getMaxMp());
			
			if (getHeading() == -1) {
				mob.setHeading(Rnd.nextInt(61794));
			} else {
				mob.setHeading(getHeading());
			}
			
			mob.setSpawn(this);
			mob.spawnMe(newLocX, newLocY, newLocZ);
			mob.onSpawn();
			
			if (general().debug()) {
				_log.finest("Spawned Mob Id: " + _template.getId() + " ,at: X: " + mob.getX() + " Y: " + mob.getY() + " Z: " + mob.getZ());
			}
			return mob;
			
		} catch (Exception e) {
			_log.log(Level.WARNING, "NPC class not found: " + e.getMessage(), e);
			return null;
		}
	}
}