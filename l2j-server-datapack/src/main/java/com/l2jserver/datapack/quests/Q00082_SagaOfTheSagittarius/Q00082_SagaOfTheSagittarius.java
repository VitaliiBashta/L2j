
package com.l2jserver.datapack.quests.Q00082_SagaOfTheSagittarius;

import com.l2jserver.datapack.quests.AbstractSagaQuest;
import com.l2jserver.gameserver.model.Location;

/**
 * Saga of the Sagittarius (82)
 * @author Emperorc
 */
public class Q00082_SagaOfTheSagittarius extends AbstractSagaQuest {
	public Q00082_SagaOfTheSagittarius() {
		super(82, Q00082_SagaOfTheSagittarius.class.getSimpleName(), "Saga of the Sagittarius");
		_npc = new int[] {
			30702,
			31627,
			31604,
			31640,
			31633,
			31646,
			31647,
			31650,
			31654,
			31655,
			31657,
			31641
		};
		_items = new int[] {
			7080,
			7519,
			7081,
			7497,
			7280,
			7311,
			7342,
			7373,
			7404,
			7435,
			7105,
			0
		};
		_mob = new int[] {
			27296,
			27231,
			27305
		};
		_classId = new int[] {
			92
		};
		_previousClass = new int[] {
			0x09
		};
		_npcSpawnLocations = new Location[] {
			new Location(191046, -40640, -3042),
			new Location(46066, -36396, -1685),
			new Location(46066, -36396, -1685)
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
