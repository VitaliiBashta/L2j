package com.l2jserver.gameserver.dao.impl.mysql;

import com.l2jserver.commons.database.ConnectionFactory;
import com.l2jserver.gameserver.dao.ClanDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class ClanDAOMySQLImpl implements ClanDAO {
	
	private static final Logger LOG = LoggerFactory.getLogger(ClanDAOMySQLImpl.class);
	
	private static final String SELECT_CLAN_PRIVILEGES = "SELECT `privs`, `rank`, `party` FROM `clan_privs` WHERE clan_id=?";
	
	private static final String INSERT_CLAN_PRIVILEGES = "INSERT INTO `clan_privs` (`clan_id`, `rank`, `party`, `privs`) VALUES (?,?,?,?) ON DUPLICATE KEY UPDATE `privs`=?";
	
	@Override
	public Map<Integer, Integer> getPrivileges(int clanId) {
		final var result = new HashMap<Integer, Integer>();
		try (var con = ConnectionFactory.getInstance().getConnection();
			var ps = con.prepareStatement(SELECT_CLAN_PRIVILEGES)) {
			ps.setInt(1, clanId);
			try (var rs = ps.executeQuery()) {
				while (rs.next()) {
					final int rank = rs.getInt("rank");
					if (rank == -1) {
						continue;
					}
					result.put(rank, rs.getInt("privs"));
				}
			}
		} catch (Exception ex) {
			LOG.error("Unable to restore clan privileges for clan Id {}!", clanId, ex);
		}
		return result;
	}
	
	@Override
	public void storePrivileges(int clanId, int rank, int privileges) {
		try (var con = ConnectionFactory.getInstance().getConnection();
			var ps = con.prepareStatement(INSERT_CLAN_PRIVILEGES)) {
			ps.setInt(1, clanId);
			ps.setInt(2, rank);
			ps.setInt(3, 0);
			ps.setInt(4, privileges);
			ps.setInt(5, privileges);
			ps.execute();
		} catch (Exception ex) {
			LOG.error("Unable to store clan privileges for clan Id {}!", clanId, ex);
		}
	}
}
