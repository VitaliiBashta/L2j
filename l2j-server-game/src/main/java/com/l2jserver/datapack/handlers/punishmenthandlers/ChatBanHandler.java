
package com.l2jserver.datapack.handlers.punishmenthandlers;

import com.l2jserver.gameserver.LoginServerThread;
import com.l2jserver.gameserver.handler.IPunishmentHandler;
import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.punishment.PunishmentTask;
import com.l2jserver.gameserver.model.punishment.PunishmentType;
import com.l2jserver.gameserver.network.L2GameClient;
import com.l2jserver.gameserver.network.serverpackets.EtcStatusUpdate;
import org.springframework.stereotype.Service;

@Service
public class ChatBanHandler implements IPunishmentHandler {
	@Override
	public void onStart(PunishmentTask task) {
		switch (task.getAffect()) {
			case CHARACTER: {
				int objectId = Integer.parseInt(String.valueOf(task.getKey()));
				final L2PcInstance player = L2World.getInstance().getPlayer(objectId);
				if (player != null) {
					applyToPlayer(task, player);
				}
				break;
			}
			case ACCOUNT: {
				String account = String.valueOf(task.getKey());
				final L2GameClient client = LoginServerThread.getInstance().getClient(account);
				if (client != null) {
					final L2PcInstance player = client.getActiveChar();
					if (player != null) {
						applyToPlayer(task, player);
					}
				}
				break;
			}
			case IP: {
				String ip = String.valueOf(task.getKey());
				for (L2PcInstance player : L2World.getInstance().getPlayers()) {
					if (player.getIPAddress().equals(ip)) {
						applyToPlayer(task, player);
					}
				}
				break;
			}
		}
	}
	
	@Override
	public void onEnd(PunishmentTask task) {
		switch (task.getAffect()) {
			case CHARACTER: {
				int objectId = Integer.parseInt(String.valueOf(task.getKey()));
				final L2PcInstance player = L2World.getInstance().getPlayer(objectId);
				if (player != null) {
					removeFromPlayer(player);
				}
				break;
			}
			case ACCOUNT: {
				String account = String.valueOf(task.getKey());
				final L2GameClient client = LoginServerThread.getInstance().getClient(account);
				if (client != null) {
					final L2PcInstance player = client.getActiveChar();
					if (player != null) {
						removeFromPlayer(player);
					}
				}
				break;
			}
			case IP: {
				String ip = String.valueOf(task.getKey());
				for (L2PcInstance player : L2World.getInstance().getPlayers()) {
					if (player.getIPAddress().equals(ip)) {
						removeFromPlayer(player);
					}
				}
				break;
			}
		}
	}
	
	/**
	 * Applies all punishment effects from the player.
	 * @param task
	 * @param player
	 */
	private static void applyToPlayer(PunishmentTask task, L2PcInstance player) {
		long delay = ((task.getExpirationTime() - System.currentTimeMillis()) / 1000);
		if (delay > 0) {
			player.sendMessage("You've been chat banned for " + (delay > 60 ? ((delay / 60) + " minutes.") : delay + " seconds."));
		} else {
			player.sendMessage("You've been chat banned forever.");
		}
		player.sendPacket(new EtcStatusUpdate(player));
	}
	
	/**
	 * Removes any punishment effects from the player.
	 * @param player
	 */
	private static void removeFromPlayer(L2PcInstance player) {
		player.sendMessage("Your Chat ban has been lifted");
		player.sendPacket(new EtcStatusUpdate(player));
	}
	
	@Override
	public PunishmentType getType() {
		return PunishmentType.CHAT_BAN;
	}
}
