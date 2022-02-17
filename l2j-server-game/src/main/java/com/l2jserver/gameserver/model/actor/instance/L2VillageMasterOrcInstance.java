package com.l2jserver.gameserver.model.actor.instance;

import com.l2jserver.gameserver.enums.Race;
import com.l2jserver.gameserver.model.actor.templates.L2NpcTemplate;
import com.l2jserver.gameserver.model.base.PlayerClass;

public final class L2VillageMasterOrcInstance extends L2VillageMasterInstance {
	
	public L2VillageMasterOrcInstance(L2NpcTemplate template) {
		super(template);
	}
	
	@Override
	protected boolean checkVillageMasterRace(PlayerClass pclass) {
		if (pclass == null) {
			return false;
		}
		
		return pclass.isOfRace(Race.ORC);
	}
}