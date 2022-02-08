
package com.l2jserver.datapack.custom.service.teleporter.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.l2jserver.datapack.custom.service.teleporter.TeleporterService;
import com.l2jserver.datapack.custom.service.teleporter.model.entity.AbstractTeleporter;
import com.l2jserver.datapack.custom.service.teleporter.model.entity.NpcTeleporter;
import com.l2jserver.datapack.custom.service.teleporter.model.entity.VoicedTeleporter;
import com.l2jserver.gameserver.config.Configuration;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

/**
 * Teleporter config.
 * @author HorridoJoho
 * @version 2.6.2.0
 */
public class TeleporterConfig {
	private final GlobalConfig global;
	private final VoicedTeleporter voiced;
	private final Map<Integer, NpcTeleporter> npcs;
	
	public TeleporterConfig() throws JsonSyntaxException, JsonIOException, IOException {
		Gson gson = new Gson();
		
		Path jsonPath = Paths.get(Configuration.server().getDatapackRoot().getAbsolutePath(), "data", TeleporterService.SCRIPT_PATH.toString(), "json");
		
		global = gson.fromJson(Files.newBufferedReader(jsonPath.resolve("global.json")), GlobalConfig.class);
		voiced = gson.fromJson(Files.newBufferedReader(jsonPath.resolve("voiced.json")), VoicedTeleporter.class);
		npcs = new HashMap<>();
		
		Path npcsDir = Paths.get(jsonPath.toString(), "npcs");
		try (var dirStream = Files.newDirectoryStream(npcsDir, "*.json")) {
			for (Path entry : dirStream) {
				if (!Files.isRegularFile(entry)) {
					continue;
				}
				
				NpcTeleporter npc = gson.fromJson(Files.newBufferedReader(entry), NpcTeleporter.class);
				npcs.put(npc.getId(), npc);
			}
		}
		
		global.afterDeserialize(this);
		voiced.afterDeserialize(this);
		for (NpcTeleporter npc : npcs.values()) {
			npc.afterDeserialize(this);
		}
	}
	
	public AbstractTeleporter determineTeleporter(L2Npc npc, L2PcInstance player) {
		if (npc == null) {
			if (!Configuration.teleporterService().getVoicedEnable() || ((Configuration.teleporterService().getVoicedRequiredItem() > 0) && (player.getInventory().getAllItemsByItemId(Configuration.teleporterService().getVoicedRequiredItem()) == null))) {
				return null;
			}
			return voiced;
		}
		return npcs.get(npc.getId());
	}
	
	public void registerNpcs(TeleporterService scriptInstance) {
		for (NpcTeleporter npc : npcs.values()) {
			if (npc.getDirectFirstTalk()) {
				scriptInstance.addFirstTalkId(npc.getId());
			}
			scriptInstance.addStartNpc(npc.getId());
			scriptInstance.addTalkId(npc.getId());
		}
	}
	
	public GlobalConfig getGlobal() {
		return global;
	}
	
	public VoicedTeleporter getVoiced() {
		return voiced;
	}
	
	public Map<Integer, NpcTeleporter> getNpcs() {
		return npcs;
	}
}
