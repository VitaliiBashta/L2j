
package com.l2jserver.datapack.ai.npc.MonumentOfHeroes;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.util.Util;

/**
 * Monument of Heroes AI.
 * @author Adry_85
 */
public final class MonumentOfHeroes extends AbstractNpcAI {
	// NPCs
	private static final int[] MONUMENTS = {
		31690,
		31769,
		31770,
		31771,
		31772
	};
	// Items
	private static final int WINGS_OF_DESTINY_CIRCLET = 6842;
	private static final int[] WEAPONS = {
		6611, // Infinity Blade
		6612, // Infinity Cleaver
		6613, // Infinity Axe
		6614, // Infinity Rod
		6615, // Infinity Crusher
		6616, // Infinity Scepter
		6617, // Infinity Stinger
		6618, // Infinity Fang
		6619, // Infinity Bow
		6620, // Infinity Wing
		6621, // Infinity Spear
		9388, // Infinity Rapier
		9389, // Infinity Sword
		9390, // Infinity Shooter
	};
	
	public MonumentOfHeroes() {
		super(MonumentOfHeroes.class.getSimpleName(), "ai/npc");
		addStartNpc(MONUMENTS);
		addTalkId(MONUMENTS);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
		switch (event) {
			case "HeroWeapon": {
				if (player.isHero()) {
					return hasAtLeastOneQuestItem(player, WEAPONS) ? "already_have_weapon.htm" : "weapon_list.htm";
				}
				return "no_hero_weapon.htm";
			}
			case "HeroCirclet": {
				if (player.isHero()) {
					if (!hasQuestItems(player, WINGS_OF_DESTINY_CIRCLET)) {
						giveItems(player, WINGS_OF_DESTINY_CIRCLET, 1);
					} else {
						return "already_have_circlet.htm";
					}
				} else {
					return "no_hero_circlet.htm";
				}
				break;
			}
			default: {
				int weaponId = Integer.parseInt(event);
				if (Util.contains(WEAPONS, weaponId)) {
					giveItems(player, weaponId, 1);
				}
				break;
			}
		}
		return super.onAdvEvent(event, npc, player);
	}
}