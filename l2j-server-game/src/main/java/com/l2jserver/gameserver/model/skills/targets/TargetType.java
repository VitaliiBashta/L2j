package com.l2jserver.gameserver.model.skills.targets;

import com.l2jserver.gameserver.Custom;

/**
 * Target type enumerated.
 * @author Zoey76
 * @version 2.6.2.0
 */
public enum TargetType {
	/** Advance Head Quarters (Outposts). */
	ADVANCE_BASE,
	/** Enemies in high terrain or protected by castle walls and doors. */
	ARTILLERY,
	/** Doors or treasure chests. */
	DOOR_TREASURE,
	/** Any enemies (included allies). */
	ENEMY,
	/** Friendly. */
	ENEMY_NOT,
	/** Only enemies (not included allies). */
	ENEMY_ONLY,
	/** Fortress's Flagpole. */
	FORTRESS_FLAGPOLE,
	/** Ground. */
	GROUND,
	/** Holy Artifacts from sieges. */
	HOLYTHING,
	/** Items. */
	ITEM,
	/** Nothing. */
	NONE,
	/** NPC corpses. */
	NPC_BODY,
	/** Others, except caster. */
	OTHERS,
	/** Player corpses. */
	PC_BODY,
	/** Self. */
	SELF,
	/** Servitor, not pet. */
	SUMMON,
	/** Anything targetable. */
	TARGET,
	/** Wyverns. */
	WYVERN_TARGET,
	@Custom
	AREA,
	@Custom
	AREA_CORPSE_MOB,
	@Custom
	AREA_FRIENDLY,
	@Custom
	AREA_SUMMON,
	@Custom
	AREA_UNDEAD,
	@Custom
	AURA,
	@Custom
	AURA_CORPSE_MOB,
	@Custom
	AURA_FRIENDLY,
	@Custom
	AURA_UNDEAD_ENEMY,
	@Custom
	BEHIND_AREA,
	@Custom
	BEHIND_AURA,
	@Custom
	CLAN,
	@Custom
	CLAN_MEMBER,
	@Custom
	COMMAND_CHANNEL,
	@Custom
	CORPSE,
	@Custom
	CORPSE_CLAN,
	@Custom
	CORPSE_MOB,
	@Custom
	ENEMY_SUMMON,
	@Custom
	FLAGPOLE,
	@Custom
	FRONT_AREA,
	@Custom
	FRONT_AURA,
	@Custom
	HOLY,
	@Custom
	ONE,
	@Custom
	OWNER_PET,
	@Custom
	PARTY,
	@Custom
	PARTY_CLAN,
	@Custom
	PARTY_MEMBER,
	@Custom
	PARTY_NOTME,
	@Custom
	PARTY_OTHER,
	@Custom
	PET,
	@Custom
	SERVITOR,
	@Custom
	TARGET_PARTY,
	@Custom
	UNDEAD,
	@Custom
	UNLOCKABLE
}
