
package com.l2jserver.datapack.custom.service.teleporter.model.entity;

import com.l2jserver.datapack.custom.service.teleporter.model.TeleporterConfig;

/**
 * Group teleport.
 * @author HorridoJoho
 * @version 2.6.2.0
 */
public class GroupTeleport extends SoloTeleport {
	private int minMembers;
	private int maxMembers;
	private int maxDistance;
	private boolean allowIncomplete;
	
	public GroupTeleport() {
	}
	
	@Override
	public void afterDeserialize(TeleporterConfig config) {
		super.afterDeserialize(config);
		
		getPlaceholder().addChild("min_members", String.valueOf(minMembers)).addChild("max_members", String.valueOf(maxMembers)).addChild("max_distance", String.valueOf(maxDistance));
	}
	
	public int getMinMembers() {
		return minMembers;
	}
	
	public int getMaxMembers() {
		return maxMembers;
	}
	
	public int getMaxDistance() {
		return maxDistance;
	}
	
	public boolean getAllowIncomplete() {
		return allowIncomplete;
	}
}
