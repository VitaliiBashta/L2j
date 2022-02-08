
package com.l2jserver.datapack.custom.service.teleporter.model;

import java.util.Map;

import com.l2jserver.datapack.custom.service.teleporter.model.entity.GroupTeleport;
import com.l2jserver.datapack.custom.service.teleporter.model.entity.GroupTeleportCategory;
import com.l2jserver.datapack.custom.service.teleporter.model.entity.SoloTeleport;
import com.l2jserver.datapack.custom.service.teleporter.model.entity.SoloTeleportCategory;

/**
 * Global configuration.
 * @author HorridoJoho
 * @version 2.6.2.0
 */
public class GlobalConfig {
	private Map<String, SoloTeleport> soloTeleports;
	private Map<String, GroupTeleport> groupTeleports;
	
	private Map<String, SoloTeleportCategory> soloTeleportCategories;
	private Map<String, GroupTeleportCategory> groupTeleportCategories;
	
	public void afterDeserialize(TeleporterConfig config) {
		for (SoloTeleport teleport : soloTeleports.values()) {
			teleport.afterDeserialize(config);
		}
		
		for (GroupTeleport teleport : groupTeleports.values()) {
			teleport.afterDeserialize(config);
		}
		
		for (SoloTeleportCategory soloCat : soloTeleportCategories.values()) {
			soloCat.afterDeserialize(config);
		}
		
		for (GroupTeleportCategory groupCat : groupTeleportCategories.values()) {
			groupCat.afterDeserialize(config);
		}
	}
	
	public Map<String, SoloTeleport> getSoloTeleports() {
		return soloTeleports;
	}
	
	public Map<String, GroupTeleport> getGroupTeleports() {
		return groupTeleports;
	}
	
	public Map<String, SoloTeleportCategory> getSoloTeleportCategories() {
		return soloTeleportCategories;
	}
	
	public Map<String, GroupTeleportCategory> getGroupTeleportCategories() {
		return groupTeleportCategories;
	}
}
