/*
 * Copyright © 2004-2020 L2J Server
 * 
 * This file is part of L2J Server.
 * 
 * L2J Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.l2jserver.commons.dao;

import java.util.HashMap;
import java.util.Map;

/**
 * Server Name DAO.
 * @author Zoey76
 */
public class ServerNameDAO {
	
	private static final Map<Integer, String> SERVERS = new HashMap<>();
	
	static {
		SERVERS.put(1, "Bartz");
		SERVERS.put(2, "Sieghardt");
		SERVERS.put(3, "Kain");
		SERVERS.put(4, "Lionna");
		SERVERS.put(5, "Erica");
		SERVERS.put(6, "Gustin");
		SERVERS.put(7, "Devianne");
		SERVERS.put(8, "Hindemith");
		SERVERS.put(9, "Teon (EURO)");
		SERVERS.put(10, "Franz (EURO)");
		SERVERS.put(11, "Luna (EURO)");
		SERVERS.put(12, "Sayha");
		SERVERS.put(13, "Aria");
		SERVERS.put(14, "Phoenix");
		SERVERS.put(15, "Chronos");
		SERVERS.put(16, "Naia (EURO)");
		SERVERS.put(17, "Elhwynna");
		SERVERS.put(18, "Ellikia");
		SERVERS.put(19, "Shikken");
		SERVERS.put(20, "Scryde");
		SERVERS.put(21, "Frikios");
		SERVERS.put(22, "Ophylia");
		SERVERS.put(23, "Shakdun");
		SERVERS.put(24, "Tarziph");
		SERVERS.put(25, "Aria");
		SERVERS.put(26, "Esenn");
		SERVERS.put(27, "Elcardia");
		SERVERS.put(28, "Yiana");
		SERVERS.put(29, "Seresin");
		SERVERS.put(30, "Tarkai");
		SERVERS.put(31, "Khadia");
		SERVERS.put(32, "Roien");
		SERVERS.put(33, "Kallint (Non-PvP)");
		SERVERS.put(34, "Baium");
		SERVERS.put(35, "Kamael");
		SERVERS.put(36, "Beleth");
		SERVERS.put(37, "Anakim");
		SERVERS.put(38, "Lilith");
		SERVERS.put(39, "Thifiel");
		SERVERS.put(40, "Lithra");
		SERVERS.put(41, "Lockirin");
		SERVERS.put(42, "Kakai");
		SERVERS.put(43, "Cadmus");
		SERVERS.put(44, "Athebaldt");
		SERVERS.put(45, "Blackbird");
		SERVERS.put(46, "Ramsheart");
		SERVERS.put(47, "Esthus");
		SERVERS.put(48, "Vasper");
		SERVERS.put(49, "Lancer");
		SERVERS.put(50, "Ashton");
		SERVERS.put(51, "Waytrel");
		SERVERS.put(52, "Waltner");
		SERVERS.put(53, "Tahnford");
		SERVERS.put(54, "Hunter");
		SERVERS.put(55, "Dewell");
		SERVERS.put(56, "Rodemaye");
		SERVERS.put(57, "Ken Rauhel");
		SERVERS.put(58, "Ken Abigail");
		SERVERS.put(59, "Ken Orwen");
		SERVERS.put(60, "Van Holter");
		SERVERS.put(61, "Desperion");
		SERVERS.put(62, "Einhovant");
		SERVERS.put(63, "Shunaiman");
		SERVERS.put(64, "Faris");
		SERVERS.put(65, "Tor");
		SERVERS.put(66, "Carneiar");
		SERVERS.put(67, "Dwyllios");
		SERVERS.put(68, "Baium");
		SERVERS.put(69, "Hallate");
		SERVERS.put(70, "Zaken");
		SERVERS.put(71, "Core");
	}
	
	public static String getServer(int id) {
		return SERVERS.getOrDefault(id, "Undefined");
	}
	
	public static Map<Integer, String> getServers() {
		return SERVERS;
	}
}
