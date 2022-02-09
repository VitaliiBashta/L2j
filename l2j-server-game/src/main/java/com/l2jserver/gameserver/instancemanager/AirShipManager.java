package com.l2jserver.gameserver.instancemanager;

import java.util.HashMap;
import java.util.Map;

import com.l2jserver.gameserver.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.gameserver.model.AirShipTeleportList;
import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.VehiclePathPoint;
import com.l2jserver.gameserver.model.actor.instance.L2AirShipInstance;
import com.l2jserver.gameserver.model.actor.instance.L2ControllableAirShipInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.actor.templates.L2CharTemplate;
import com.l2jserver.gameserver.network.serverpackets.ExAirShipTeleportList;
import org.springframework.stereotype.Service;

@Service
public class AirShipManager {
	
	private static final Logger LOG = LoggerFactory.getLogger(AirShipManager.class);
	
	private static final String LOAD_DB = "SELECT * FROM airships";
	private static final String ADD_DB = "INSERT INTO airships (owner_id,fuel) VALUES (?,?)";
	private static final String UPDATE_DB = "UPDATE airships SET fuel=? WHERE owner_id=?";
	
	private final L2CharTemplate airShipTemplate;
	private final Map<Integer, StatsSet> airShipsInfo = new HashMap<>();
	private final Map<Integer, L2AirShipInstance> airShips = new HashMap<>();
	private final Map<Integer, AirShipTeleportList> teleports = new HashMap<>();
	private final Context context;

	protected AirShipManager(Context context) {
		this.context = context;
		StatsSet npcDat = new StatsSet();
		npcDat.set("npcId", 9);
		npcDat.set("level", 0);
		npcDat.set("jClass", "boat");
		
		npcDat.set("baseSTR", 0);
		npcDat.set("baseCON", 0);
		npcDat.set("baseDEX", 0);
		npcDat.set("baseINT", 0);
		npcDat.set("baseWIT", 0);
		npcDat.set("baseMEN", 0);
		
		npcDat.set("baseShldDef", 0);
		npcDat.set("baseShldRate", 0);
		npcDat.set("baseAccCombat", 38);
		npcDat.set("baseEvasRate", 38);
		npcDat.set("baseCritRate", 38);
		
		npcDat.set("collisionRadius", 0);
		npcDat.set("collisionHeight", 0);
		npcDat.set("sex", "male");
		npcDat.set("type", "");
		npcDat.set("baseAtkRange", 0);
		npcDat.set("baseMpMax", 0);
		npcDat.set("baseCpMax", 0);
		npcDat.set("rewardExp", 0);
		npcDat.set("rewardSp", 0);
		npcDat.set("basePAtk", 0);
		npcDat.set("baseMAtk", 0);
		npcDat.set("basePAtkSpd", 0);
		npcDat.set("aggroRange", 0);
		npcDat.set("baseMAtkSpd", 0);
		npcDat.set("rhand", 0);
		npcDat.set("lhand", 0);
		npcDat.set("armor", 0);
		npcDat.set("baseWalkSpd", 0);
		npcDat.set("baseRunSpd", 0);
		npcDat.set("name", "AirShip");
		npcDat.set("baseHpMax", 50000);
		npcDat.set("baseHpReg", 3.e-3f);
		npcDat.set("baseMpReg", 3.e-3f);
		npcDat.set("basePDef", 100);
		npcDat.set("baseMDef", 100);
		airShipTemplate = new L2CharTemplate(npcDat);
		
		load();
	}
	
	public L2AirShipInstance getNewAirShip(int x, int y, int z, int heading) {
		final L2AirShipInstance airShip = new L2AirShipInstance(airShipTemplate);
		
		airShip.setHeading(heading);
		airShip.setXYZInvisible(x, y, z);
		airShip.spawnMe();
		airShip.getStat().setMoveSpeed(280);
		airShip.getStat().setRotationSpeed(2000);
		return airShip;
	}
	
	public L2AirShipInstance getNewAirShip(int x, int y, int z, int heading, int ownerId) {
		final StatsSet info = airShipsInfo.get(ownerId);
		if (info == null) {
			return null;
		}
		
		final L2AirShipInstance airShip;
		if (airShips.containsKey(ownerId)) {
			airShip = airShips.get(ownerId);
			airShip.refreshID();
		} else {
			airShip = new L2ControllableAirShipInstance(airShipTemplate, ownerId);
			airShips.put(ownerId, airShip);
			
			airShip.setMaxFuel(600);
			airShip.setFuel(info.getInt("fuel"));
			airShip.getStat().setMoveSpeed(280);
			airShip.getStat().setRotationSpeed(2000);
		}
		
		airShip.setHeading(heading);
		airShip.setXYZInvisible(x, y, z);
		airShip.spawnMe();
		return airShip;
	}
	
	public void removeAirShip(L2AirShipInstance ship) {
		if (ship.getOwnerId() != 0) {
			storeInDb(ship.getOwnerId());
			final StatsSet info = airShipsInfo.get(ship.getOwnerId());
			if (info != null) {
				info.set("fuel", ship.getFuel());
			}
		}
	}
	
	public boolean hasAirShipLicense(int ownerId) {
		return airShipsInfo.containsKey(ownerId);
	}
	
	public void registerLicense(int ownerId) {
		if (!airShipsInfo.containsKey(ownerId)) {
			final StatsSet info = new StatsSet();
			info.set("fuel", 600);
			
			airShipsInfo.put(ownerId, info);
			
			try (var con = context.connectionFactory.getConnection();
				var ps = con.prepareStatement(ADD_DB)) {
				ps.setInt(1, ownerId);
				ps.setInt(2, info.getInt("fuel"));
				ps.executeUpdate();
			} catch (Exception ex) {
				LOG.warn("Could not add new airship license!", ex);
			}
		}
	}
	
	public boolean hasAirShip(int ownerId) {
		final L2AirShipInstance ship = airShips.get(ownerId);
		return (ship != null) && (ship.isVisible() || ship.isTeleporting());
	}
	
	public void registerAirShipTeleportList(int dockId, int locationId, VehiclePathPoint[][] tp, int[] fuelConsumption) {
		if (tp.length != fuelConsumption.length) {
			return;
		}
		
		teleports.put(dockId, new AirShipTeleportList(locationId, fuelConsumption, tp));
	}
	
	public void sendAirShipTeleportList(L2PcInstance player) {
		if ((player == null) || !player.isInAirShip()) {
			return;
		}
		
		final L2AirShipInstance ship = player.getAirShip();
		if (!ship.isCaptain(player) || !ship.isInDock() || ship.isMoving()) {
			return;
		}
		
		int dockId = ship.getDockId();
		if (!teleports.containsKey(dockId)) {
			return;
		}
		
		final AirShipTeleportList all = teleports.get(dockId);
		player.sendPacket(new ExAirShipTeleportList(all.getLocation(), all.getRoute(), all.getFuel()));
	}
	
	public VehiclePathPoint[] getTeleportDestination(int dockId, int index) {
		final AirShipTeleportList all = teleports.get(dockId);
		if (all == null) {
			return null;
		}
		
		if ((index < -1) || (index >= all.getRoute().length)) {
			return null;
		}
		
		return all.getRoute()[index + 1];
	}
	
	public int getFuelConsumption(int dockId, int index) {
		final AirShipTeleportList all = teleports.get(dockId);
		if (all == null) {
			return 0;
		}
		
		if ((index < -1) || (index >= all.getFuel().length)) {
			return 0;
		}
		
		return all.getFuel()[index + 1];
	}
	
	private void load() {
		try (var con = context.connectionFactory.getConnection();
			var s = con.createStatement();
			var rs = s.executeQuery(LOAD_DB)) {
			StatsSet info;
			while (rs.next()) {
				info = new StatsSet();
				info.set("fuel", rs.getInt("fuel"));
				airShipsInfo.put(rs.getInt("owner_id"), info);
			}
		} catch (Exception ex) {
			LOG.warn("Could not load airships table!", ex);
		}
		LOG.info("Loaded {} private airships.", airShipsInfo.size());
	}
	
	private void storeInDb(int ownerId) {
		StatsSet info = airShipsInfo.get(ownerId);
		if (info == null) {
			return;
		}
		
		try (var con = context.connectionFactory.getConnection();
			var ps = con.prepareStatement(UPDATE_DB)) {
			ps.setInt(1, info.getInt("fuel"));
			ps.setInt(2, ownerId);
			ps.executeUpdate();
		} catch (Exception ex) {
			LOG.warn("Could not update airships table!", ex);
		}
	}
	
	public static AirShipManager getInstance() {
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder {
		protected static final AirShipManager _instance = new AirShipManager(null);
	}
}