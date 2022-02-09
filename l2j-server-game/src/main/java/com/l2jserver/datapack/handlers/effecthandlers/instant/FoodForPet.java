
package com.l2jserver.datapack.handlers.effecthandlers.instant;

import static com.l2jserver.gameserver.config.Configuration.rates;

import com.l2jserver.gameserver.enums.MountType;
import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PetInstance;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.skills.BuffInfo;

/**
 * Food For Pet effect implementation.
 * @author Adry_85
 * @since 2.6.0.0
 */
public final class FoodForPet extends AbstractEffect {
	private final int _normal;
	private final int _ride;
	private final int _wyvern;
	
	public FoodForPet(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
		
		_normal = params.getInt("normal", 0);
		_ride = params.getInt("ride", 0);
		_wyvern = params.getInt("wyvern", 0);
	}
	
	@Override
	public boolean isInstant() {
		return true;
	}
	
	@Override
	public void onStart(BuffInfo info) {
		final L2Character activeChar = info.getEffector();
		
		if (activeChar.isPet()) {
			final L2PetInstance pet = (L2PetInstance) activeChar;
			pet.setCurrentFed(pet.getCurrentFed() + (_normal * rates().getPetFoodRate()));
		} else if (activeChar.isPlayer()) {
			final L2PcInstance player = activeChar.getActingPlayer();
			if (player.getMountType() == MountType.WYVERN) {
				player.setCurrentFeed(player.getCurrentFeed() + _wyvern);
			} else {
				player.setCurrentFeed(player.getCurrentFeed() + _ride);
			}
		}
	}
}
