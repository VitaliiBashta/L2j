
package com.l2jserver.datapack.handlers.voicedcommandhandlers;

import static com.l2jserver.gameserver.config.Configuration.character;
import static com.l2jserver.gameserver.config.Configuration.customs;

import com.l2jserver.gameserver.handler.IVoicedCommandHandler;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

/**
 * Auto Loot Extension voiced command.
 * @author Maneco2
 * @version 2.6.2.0
 */
public class AutoLoot implements IVoicedCommandHandler {
	private static final String[] VOICED_COMMANDS = {
		"loot",
		"autoloot",
		"itemloot",
		"herbloot"
	};
	
	@Override
	public boolean useVoicedCommand(String command, L2PcInstance activeChar, String params) {
		if (!customs().autoLootVoiceCommand()) {
			return false;
		}
		if (command.equals("loot")) {
			activeChar.sendMessage("Using Voices Methods:\n.autoloot: Loot all item(s).\n.itemloot: Loot all better item(s).\n.herbloot: Loot recovery(s) herb(s).");
			if (activeChar.isAutoLoot()) {
				activeChar.sendMessage("Auto Loot: enabled.");
			}
			if (activeChar.isAutoLootItem()) {
				activeChar.sendMessage("Auto Loot Item: enabled.");
			}
			if (activeChar.isAutoLootHerb()) {
				activeChar.sendMessage("Auto Loot Herbs: enabled.");
			}
		} else if (command.equals("autoloot")) {
			if (!character().autoLoot()) {
				if (activeChar.isAutoLoot()) {
					activeChar.setAutoLoot(false);
					activeChar.sendMessage("Auto Loot: disabled.");
					if (customs().autoLootVoiceRestore()) {
						activeChar.getVariables().remove("AutoLoot");
					}
				} else {
					activeChar.setAutoLoot(true);
					activeChar.sendMessage("Auto Loot: enabled.");
					if (customs().autoLootVoiceRestore()) {
						activeChar.getVariables().set("AutoLoot", true);
					}
				}
			} else {
				activeChar.sendMessage("Auto Loot already enable.");
			}
		} else if (command.equals("itemloot")) {
			if (activeChar.isAutoLootItem()) {
				activeChar.setAutoLootItem(false);
				activeChar.sendMessage("Auto Loot Item: disabled.");
				if (customs().autoLootVoiceRestore()) {
					activeChar.getVariables().remove("AutoLootItems");
				}
			} else {
				activeChar.setAutoLootItem(true);
				activeChar.sendMessage("Auto Loot Item: enabled.");
				if (customs().autoLootVoiceRestore()) {
					activeChar.getVariables().set("AutoLootItems", true);
				}
				
				if (activeChar.isAutoLoot()) {
					activeChar.setAutoLoot(false);
					activeChar.sendMessage("Auto Loot Item is now priority.");
					if (customs().autoLootVoiceRestore()) {
						activeChar.getVariables().remove("AutoLoot");
					}
				}
			}
		} else if (command.equals("herbloot")) {
			if (activeChar.isAutoLootHerb()) {
				activeChar.setAutoLootHerbs(false);
				activeChar.sendMessage("Auto Loot Herbs: disabled.");
				if (customs().autoLootVoiceRestore()) {
					activeChar.getVariables().remove("AutoLootHerbs");
				}
			} else {
				activeChar.setAutoLootHerbs(true);
				activeChar.sendMessage("Auto Loot Herbs: enabled.");
				if (customs().autoLootVoiceRestore()) {
					activeChar.getVariables().set("AutoLootHerbs", true);
				}
			}
		}
		return true;
	}
	
	@Override
	public String[] getVoicedCommandList() {
		return VOICED_COMMANDS;
	}
}