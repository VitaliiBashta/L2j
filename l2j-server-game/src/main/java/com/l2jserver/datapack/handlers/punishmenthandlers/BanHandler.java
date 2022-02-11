
package com.l2jserver.datapack.handlers.punishmenthandlers;

import com.l2jserver.gameserver.LoginServerThread;
import com.l2jserver.gameserver.handler.IPunishmentHandler;
import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.punishment.PunishmentTask;
import com.l2jserver.gameserver.model.punishment.PunishmentType;
import com.l2jserver.gameserver.network.L2GameClient;
import org.springframework.stereotype.Service;

@Service
public class BanHandler implements IPunishmentHandler {
	@Override
	public void onStart(PunishmentTask task) {
		switch (task.getAffect()) {
			case CHARACTER: {
				int objectId = Integer.parseInt(String.valueOf(task.getKey()));
				final L2PcInstance player = L2World.getInstance().getPlayer(objectId);
				if (player != null) {
					applyToPlayer(player);
				}
				break;
			}
			case ACCOUNT: {
				String account = String.valueOf(task.getKey());
				final L2GameClient client = LoginServerThread.getInstance().getClient(account);
				if (client != null) {
					final L2PcInstance player = client.getActiveChar();
					if (player != null) {
						applyToPlayer(player);
					} else {
						client.closeNow();
					}
				}
				break;
			}
			case IP: {
				String ip = String.valueOf(task.getKey());
				for (L2PcInstance player : L2World.getInstance().getPlayers()) {
					if (player.getIPAddress().equals(ip)) {
						applyToPlayer(player);
					}
				}
				break;
			}
		}
	}
	
	@Override
	public void onEnd(PunishmentTask task) {
		
	}
	
	/**
	 * Applies all punishment effects from the player.
	 * @param player
	 */
	private static void applyToPlayer(L2PcInstance player) {
		player.logout();
	}
	
	@Override
	public PunishmentType getType() {
		return PunishmentType.BAN;
	}
}
