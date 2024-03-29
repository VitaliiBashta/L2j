
package com.l2jserver.datapack.handlers.punishmenthandlers;

import com.l2jserver.gameserver.LoginServerThread;
import com.l2jserver.gameserver.ThreadPoolManager;
import com.l2jserver.gameserver.cache.HtmCache;
import com.l2jserver.gameserver.handler.IPunishmentHandler;
import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.actor.tasks.player.TeleportTask;
import com.l2jserver.gameserver.model.entity.TvTEvent;
import com.l2jserver.gameserver.model.events.Containers;
import com.l2jserver.gameserver.model.events.EventType;
import com.l2jserver.gameserver.model.events.impl.character.player.OnPlayerLogin;
import com.l2jserver.gameserver.model.events.listeners.ConsumerEventListener;
import com.l2jserver.gameserver.model.olympiad.OlympiadManager;
import com.l2jserver.gameserver.model.punishment.PunishmentTask;
import com.l2jserver.gameserver.model.punishment.PunishmentType;
import com.l2jserver.gameserver.model.zone.ZoneId;
import com.l2jserver.gameserver.model.zone.type.L2JailZone;
import com.l2jserver.gameserver.network.L2GameClient;
import com.l2jserver.gameserver.network.serverpackets.NpcHtmlMessage;
import org.springframework.stereotype.Service;

@Service
public class JailHandler implements IPunishmentHandler {
	public JailHandler() {
		// Register global listener
		Containers.Global().addListener(new ConsumerEventListener(Containers.Global(), EventType.ON_PLAYER_LOGIN, (OnPlayerLogin event) -> onPlayerLogin(event), this));
	}
	
	public void onPlayerLogin(OnPlayerLogin event) {
		final L2PcInstance activeChar = event.getActiveChar();
		if (activeChar.isJailed() && !activeChar.isInsideZone(ZoneId.JAIL)) {
			applyToPlayer(null, activeChar);
		} else if (!activeChar.isJailed() && activeChar.isInsideZone(ZoneId.JAIL) && !activeChar.isGM()) {
			removeFromPlayer(activeChar);
		}
	}
	
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
		player.setInstanceId(0);
		player.setIsIn7sDungeon(false);
		
		if (!TvTEvent.isInactive() && TvTEvent.isPlayerParticipant(player.getObjectId())) {
			TvTEvent.removeParticipant(player.getObjectId());
		}
		
		if (OlympiadManager.getInstance().isRegisteredInComp(player)) {
			OlympiadManager.getInstance().removeDisconnectedCompetitor(player);
		}
		
		ThreadPoolManager.getInstance().scheduleGeneral(new TeleportTask(player, L2JailZone.getLocationIn()), 2000);
		
		// Open a Html message to inform the player
		final NpcHtmlMessage msg = new NpcHtmlMessage();
		String content = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), "data/html/jail_in.htm");
		if (content != null) {
			content = content.replaceAll("%reason%", task != null ? task.getReason() : "");
			content = content.replaceAll("%punishedBy%", task != null ? task.getPunishedBy() : "");
			msg.setHtml(content);
		} else {
			msg.setHtml("<html><body>You have been put in jail by an admin.</body></html>");
		}
		player.sendPacket(msg);
		if (task != null) {
			long delay = ((task.getExpirationTime() - System.currentTimeMillis()) / 1000);
			if (delay > 0) {
				player.sendMessage("You've been jailed for " + (delay > 60 ? ((delay / 60) + " minutes.") : delay + " seconds."));
			} else {
				player.sendMessage("You've been jailed forever.");
			}
		}
	}
	
	/**
	 * Removes any punishment effects from the player.
	 * @param player
	 */
	private static void removeFromPlayer(L2PcInstance player) {
		ThreadPoolManager.getInstance().scheduleGeneral(new TeleportTask(player, L2JailZone.getLocationOut()), 2000);
		
		// Open a Html message to inform the player
		final NpcHtmlMessage msg = new NpcHtmlMessage();
		String content = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), "data/html/jail_out.htm");
		if (content != null) {
			msg.setHtml(content);
		} else {
			msg.setHtml("<html><body>You are free for now, respect server rules!</body></html>");
		}
		player.sendPacket(msg);
	}
	
	@Override
	public PunishmentType getType() {
		return PunishmentType.JAIL;
	}
}
