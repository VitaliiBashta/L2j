package com.l2jserver.gameserver.model.punishment;

public enum PunishmentAffect {
	ACCOUNT,
	CHARACTER,
	IP;
	// HWID; Not implemented yet.
	
	public static PunishmentAffect getByName(String name) {
		for (PunishmentAffect type : values()) {
			if (type.name().equalsIgnoreCase(name)) {
				return type;
			}
		}
		return null;
	}
}
