
package com.l2jserver.datapack.quests.Q00095_SagaOfTheHellKnight;

import com.l2jserver.datapack.quests.AbstractSagaQuest;
import com.l2jserver.gameserver.model.Location;

/**
 * Saga of the Hell Knight (95)
 * @author Emperorc
 */
public class Q00095_SagaOfTheHellKnight extends AbstractSagaQuest {
	public Q00095_SagaOfTheHellKnight() {
		super(95, Q00095_SagaOfTheHellKnight.class.getSimpleName(), "Saga of the Hell Knight");
		_npc = new int[] {
			31582,
			31623,
			31297,
			31297,
			31599,
			31646,
			31647,
			31653,
			31654,
			31655,
			31656,
			31297
		};
		_items = new int[] {
			7080,
			7532,
			7081,
			7510,
			7293,
			7324,
			7355,
			7386,
			7417,
			7448,
			7086,
			0
		};
		_mob = new int[] {
			27258,
			27244,
			27263
		};
		_classId = new int[] {
			91
		};
		_previousClass = new int[] {
			0x06
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
