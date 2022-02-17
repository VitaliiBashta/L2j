
package com.l2jserver.gameserver.model.actor.tasks.player;

import com.l2jserver.gameserver.data.xml.impl.AdminData;
import com.l2jserver.gameserver.enums.IllegalActionPunishmentType;
import com.l2jserver.gameserver.instancemanager.PunishmentManager;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.punishment.PunishmentAffect;
import com.l2jserver.gameserver.model.punishment.PunishmentTask;
import com.l2jserver.gameserver.model.punishment.PunishmentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.l2jserver.gameserver.config.Configuration.general;


public final class IllegalPlayerActionTask implements Runnable {
	
	private static final Logger LOG = LoggerFactory.getLogger("audit");
	
	private final String message;
	private final IllegalActionPunishmentType punishment;
	private final L2PcInstance actor;
	
	public IllegalPlayerActionTask(L2PcInstance actor, String message, IllegalActionPunishmentType punishment) {
		this.message = message;
		this.punishment = punishment;
		this.actor = actor;
		
		switch (punishment) {
			case KICK -> this.actor.sendMessage("You will be kicked for illegal action, GM informed.");
			case KICK_BAN -> {
				if (!this.actor.isGM()) {
					this.actor.setAccessLevel(-1);
					this.actor.setAccountAccesslevel(-1);
				}
				this.actor.sendMessage("You are banned for illegal action, GM informed.");
			}
			case JAIL -> {
				this.actor.sendMessage("Illegal action performed!");
				this.actor.sendMessage("You will be teleported to GM Consultation Service area and jailed.");
			}
		}
	}
	
	@Override
	public void run() {
		LOG.info("Illegal action [{}] by player {}, action taken {}!", message, actor, punishment);
		
		AdminData.getInstance().broadcastMessageToGMs(message);
		if (!actor.isGM()) {
			switch (punishment) {
				case BROADCAST -> {
				}
				case KICK -> actor.logout(false);
				case KICK_BAN -> PunishmentManager.getInstance().startPunishment(new PunishmentTask(actor.getObjectId(), PunishmentAffect.CHARACTER, PunishmentType.BAN, System.currentTimeMillis() + general().getDefaultPunishParam(), message, getClass().getSimpleName()));
				case JAIL -> PunishmentManager.getInstance().startPunishment(new PunishmentTask(actor.getObjectId(), PunishmentAffect.CHARACTER, PunishmentType.JAIL, System.currentTimeMillis() + general().getDefaultPunishParam(), message, getClass().getSimpleName()));
			}
		}
	}
}
