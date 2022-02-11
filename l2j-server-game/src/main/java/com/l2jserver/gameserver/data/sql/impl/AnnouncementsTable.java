package com.l2jserver.gameserver.data.sql.impl;

import com.l2jserver.gameserver.Context;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.announce.Announcement;
import com.l2jserver.gameserver.model.announce.AnnouncementType;
import com.l2jserver.gameserver.model.announce.AutoAnnouncement;
import com.l2jserver.gameserver.model.announce.IAnnouncement;
import com.l2jserver.gameserver.network.clientpackets.Say2;
import com.l2jserver.gameserver.network.serverpackets.CreatureSay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

@Service
public class AnnouncementsTable {
	
	private static final Logger LOG = LoggerFactory.getLogger(AnnouncementsTable.class);
	
	private final Map<Integer, IAnnouncement> announcements = new ConcurrentSkipListMap<>();
	private final Context context;

	private AnnouncementsTable(Context context) {
		this.context = context;
		load();
	}
	
	public static AnnouncementsTable getInstance() {
		return SingletonHolder._instance;
	}
	
	private void load() {
		announcements.clear();
		try (var con = context.connectionFactory.getConnection();
			var st = con.createStatement();
			var rs = st.executeQuery("SELECT `id`, `type`, `initial`, `delay`, `repeat`, `author`, `content` FROM announcements")) {
			while (rs.next()) {
				final AnnouncementType type = AnnouncementType.findById(rs.getInt("type"));
				final var author = rs.getString("author");
				final var content = rs.getString("content");
				final Announcement announce;
				switch (type) {
					case NORMAL, CRITICAL -> announce = new Announcement(rs.getInt("id"), type, content, author);
					case AUTO_NORMAL, AUTO_CRITICAL -> announce = new AutoAnnouncement(type, content, author, rs.getLong("initial"), rs.getLong("delay"), rs.getInt("repeat"));
					default -> {
						continue;
					}
				}
				announcements.put(announce.getId(), announce);
			}
		} catch (Exception e) {
			LOG.warn("Failed loading announcements:", e);
		}
	}
	
	/**
	 * Sending all announcements to the player
	 * @param player
	 */
	public void showAnnouncements(L2PcInstance player) {
		sendAnnouncements(player, AnnouncementType.NORMAL);
		sendAnnouncements(player, AnnouncementType.CRITICAL);
		sendAnnouncements(player, AnnouncementType.EVENT);
	}
	
	/**
	 * Sends all announcements to the player by the specified type.
	 */
	public void sendAnnouncements(L2PcInstance player, AnnouncementType type) {
		for (IAnnouncement announce : announcements.values()) {
			if (announce.isValid() && (announce.getType() == type)) {
				player.sendPacket(new CreatureSay(0, //
					type == AnnouncementType.CRITICAL ? Say2.CRITICAL_ANNOUNCE : Say2.ANNOUNCEMENT, //
					player.getName(), announce.getContent()));
			}
		}
	}
	
	/**
	 * Adds announcement
	 */
	public void addAnnouncement(IAnnouncement announce) {
		if (announce.storeMe()) {
			announcements.put(announce.getId(), announce);
		}
	}
	
	/**
	 * Removes announcement by id
	 * @param id
	 * @return {@code true} if announcement exists and was deleted successfully, {@code false} otherwise.
	 */
	public boolean deleteAnnouncement(int id) {
		final IAnnouncement announce = announcements.remove(id);
		return (announce != null) && announce.deleteMe();
	}
	
	public IAnnouncement getAnnounce(int id) {
		return announcements.get(id);
	}
	
	/**
	 * @return {@link Collection} containing all announcements
	 */
	public Collection<IAnnouncement> getAllAnnouncements() {
		return announcements.values();
	}
	
	private static class SingletonHolder {
		protected static final AnnouncementsTable _instance = new AnnouncementsTable(null);
	}
}
