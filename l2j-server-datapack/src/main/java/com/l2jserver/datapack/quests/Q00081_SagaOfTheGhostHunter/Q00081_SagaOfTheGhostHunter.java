
package com.l2jserver.datapack.quests.Q00081_SagaOfTheGhostHunter;

import com.l2jserver.datapack.quests.AbstractSagaQuest;
import com.l2jserver.gameserver.model.Location;

/**
 * Saga of the Ghost Hunter (81)
 * @author Emperorc
 */
public class Q00081_SagaOfTheGhostHunter extends AbstractSagaQuest {
	public Q00081_SagaOfTheGhostHunter() {
		super(81, Q00081_SagaOfTheGhostHunter.class.getSimpleName(), "Saga of the Ghost Hunter");
		_npc = new int[] {
			31603,
			31624,
			31286,
			31615,
			31617,
			31646,
			31649,
			31653,
			31654,
			31655,
			31656,
			31616
		};
		_items = new int[] {
			7080,
			7518,
			7081,
			7496,
			7279,
			7310,
			7341,
			7372,
			7403,
			7434,
			7104,
			0
		};
		_mob = new int[] {
			27301,
			27230,
			27304
		};
		_classId = new int[] {
			108
		};
		_previousClass = new int[] {
			0x24
		};
		_npcSpawnLocations = new Location[] {
			new Location(164650, -74121, -2871),
			new Location(47391, -56929, -2370),
			new Location(47429, -56923, -2383)
		};
		_text = new String[] {
			"PLAYERNAME! Pursued to here! However, I jumped out of the Banshouren boundaries! You look at the giant as the sign of power!",
			"... Oh ... good! So it was ... let's begin!",
			"I do not have the patience ..! I have been a giant force ...! Cough chatter ah ah ah!",
			"Paying homage to those who disrupt the orderly will be PLAYERNAME's death!",
			"Now, my soul freed from the shackles of the millennium, Halixia, to the back side I come ...",
			"Why do you interfere others' battles?",
			"This is a waste of time.. Say goodbye...!",
			"...That is the enemy",
			"...Goodness! PLAYERNAME you are still looking?",
			"PLAYERNAME ... Not just to whom the victory. Only personnel involved in the fighting are eligible to share in the victory.",
			"Your sword is not an ornament. Don't you think, PLAYERNAME?",
			"Goodness! I no longer sense a battle there now.",
			"let...",
			"Only engaged in the battle to bar their choice. Perhaps you should regret.",
			"The human nation was foolish to try and fight a giant's strength.",
			"Must...Retreat... Too...Strong.",
			"PLAYERNAME. Defeat...by...retaining...and...Mo...Hacker",
			"....! Fight...Defeat...It...Fight...Defeat...It..."
		};
		registerNPCs();
	}
}
