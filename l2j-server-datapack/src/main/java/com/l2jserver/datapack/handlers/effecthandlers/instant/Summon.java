
package com.l2jserver.datapack.handlers.effecthandlers.instant;

import static com.l2jserver.gameserver.config.Configuration.character;

import com.l2jserver.gameserver.data.json.ExperienceData;
import com.l2jserver.gameserver.data.xml.impl.NpcData;
import com.l2jserver.gameserver.enums.Race;
import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.actor.instance.L2ServitorInstance;
import com.l2jserver.gameserver.model.actor.templates.L2NpcTemplate;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.holders.ItemHolder;
import com.l2jserver.gameserver.model.skills.BuffInfo;

/**
 * Summon effect implementation.
 * @author UnAfraid
 */
public final class Summon extends AbstractEffect {
	private final int _npcId;
	private final float _expMultiplier;
	private final ItemHolder _consumeItem;
	private final int _lifeTime;
	private final int _consumeItemInterval;
	
	public Summon(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
		
		if (params.isEmpty()) {
			throw new IllegalArgumentException("Summon effect without parameters!");
		}
		
		_npcId = params.getInt("npcId");
		_expMultiplier = params.getFloat("expMultiplier", 1);
		_consumeItem = new ItemHolder(params.getInt("consumeItemId", 0), params.getInt("consumeItemCount", 1));
		_consumeItemInterval = params.getInt("consumeItemInterval", 0);
		_lifeTime = params.getInt("lifeTime", 3600) * 1000;
	}
	
	@Override
	public boolean isInstant() {
		return true;
	}
	
	@Override
	public void onStart(BuffInfo info) {
		if (!info.getEffected().isPlayer() || info.getEffected().hasSummon()) {
			return;
		}
		
		final L2PcInstance player = info.getEffected().getActingPlayer();
		final L2NpcTemplate template = NpcData.getInstance().getTemplate(_npcId);
		final L2ServitorInstance summon = new L2ServitorInstance(template, player);
		final int consumeItemInterval = (_consumeItemInterval > 0 ? _consumeItemInterval : (template.getRace() != Race.SIEGE_WEAPON ? 240 : 60)) * 1000;
		
		summon.setName(template.getName());
		summon.setTitle(info.getEffected().getName());
		summon.setReferenceSkill(info.getSkill().getId());
		summon.setExpMultiplier(_expMultiplier);
		summon.setLifeTime(_lifeTime);
		summon.setItemConsume(_consumeItem);
		summon.setItemConsumeInterval(consumeItemInterval);
		
		if (summon.getLevel() >= character().getMaxPetLevel()) {
			summon.getStat().setExp(ExperienceData.getInstance().getExpForLevel(character().getMaxPetLevel() - 1));
			_log.warning(Summon.class.getSimpleName() + ": (" + summon.getName() + ") NpcID: " + summon.getId() + " has a level above " + character().getMaxPetLevel() + ". Please rectify.");
		} else {
			summon.getStat().setExp(ExperienceData.getInstance().getExpForLevel(summon.getLevel() % character().getMaxPetLevel()));
		}
		
		summon.setCurrentHp(summon.getMaxHp());
		summon.setCurrentMp(summon.getMaxMp());
		summon.setHeading(player.getHeading());
		
		player.setPet(summon);
		
		summon.setRunning();
		summon.spawnMe();
	}
}
