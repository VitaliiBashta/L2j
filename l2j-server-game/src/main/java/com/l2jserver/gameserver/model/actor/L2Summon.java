package com.l2jserver.gameserver.model.actor;

import com.l2jserver.commons.util.Rnd;
import com.l2jserver.gameserver.ai.CtrlIntention;
import com.l2jserver.gameserver.ai.L2CharacterAI;
import com.l2jserver.gameserver.ai.L2SummonAI;
import com.l2jserver.gameserver.data.json.ExperienceData;
import com.l2jserver.gameserver.datatables.ItemTable;
import com.l2jserver.gameserver.enums.Race;
import com.l2jserver.gameserver.enums.ShotType;
import com.l2jserver.gameserver.enums.Team;
import com.l2jserver.gameserver.handler.IItemHandler;
import com.l2jserver.gameserver.handler.ItemHandler;
import com.l2jserver.gameserver.instancemanager.TerritoryWarManager;
import com.l2jserver.gameserver.model.AggroInfo;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.L2Party;
import com.l2jserver.gameserver.model.L2WorldRegion;
import com.l2jserver.gameserver.model.actor.instance.L2NpcInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.actor.knownlist.SummonKnownList;
import com.l2jserver.gameserver.model.actor.stat.SummonStat;
import com.l2jserver.gameserver.model.actor.status.SummonStatus;
import com.l2jserver.gameserver.model.actor.templates.L2NpcTemplate;
import com.l2jserver.gameserver.model.effects.L2EffectType;
import com.l2jserver.gameserver.model.events.EventDispatcher;
import com.l2jserver.gameserver.model.events.impl.character.player.OnPlayerSummonSpawn;
import com.l2jserver.gameserver.model.itemcontainer.PetInventory;
import com.l2jserver.gameserver.model.items.L2EtcItem;
import com.l2jserver.gameserver.model.items.L2Weapon;
import com.l2jserver.gameserver.model.items.instance.L2ItemInstance;
import com.l2jserver.gameserver.model.items.type.ActionType;
import com.l2jserver.gameserver.model.olympiad.OlympiadGameManager;
import com.l2jserver.gameserver.model.skills.Skill;
import com.l2jserver.gameserver.model.skills.targets.TargetType;
import com.l2jserver.gameserver.model.stats.Formulas;
import com.l2jserver.gameserver.model.zone.ZoneId;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.AbstractNpcInfo.SummonInfo;
import com.l2jserver.gameserver.network.serverpackets.*;
import com.l2jserver.gameserver.pathfinding.PathFinding;
import com.l2jserver.gameserver.taskmanager.DecayTaskManager;

import java.util.Set;

import static com.l2jserver.gameserver.config.Configuration.character;
import static com.l2jserver.gameserver.config.Configuration.geodata;
import static com.l2jserver.gameserver.enums.InstanceType.L2Summon;

public abstract class L2Summon extends L2Playable {
	private static final Set<Integer> PASSIVE_SUMMONS = Set.of(
		12564, 12621, 14702, 14703, 14704, 14705, 14706, 14707, 14708, 14709, 14710, 14711,
		14712, 14713, 14714, 14715, 14716, 14717, 14718, 14719, 14720, 14721, 14722, 14723,
		14724, 14725, 14726, 14727, 14728, 14729, 14730, 14731, 14732, 14733, 14734, 14735, 14736
	);
	protected boolean _restoreSummon = true;
	private L2PcInstance _owner;
	private int _attackRange = 36; // Melee range
	private boolean _follow = true;
	private boolean _previousFollowStatus = true;
	private int _shotsMask = 0;

	/**
	 * Creates an abstract summon.
	 */
	public L2Summon(L2NpcTemplate template, L2PcInstance owner) {
		super(template);
		setInstanceType(L2Summon);
		setInstanceId(owner.getInstanceId());
		setShowSummonAnimation(true);
		_owner = owner;
		getAI();
		
		setXYZInvisible(owner.getX() + Rnd.get(-100, 100), owner.getY() + Rnd.get(-100, 100), owner.getZ());
		
		// Copy the skills from template.
		for (Skill skill : template.getSkills().values()) {
			addSkill(skill);
		}
		
		Formulas.addFuncsToNewSummon(this);
	}
	

	public boolean isMountable() {
		return false;
	}
public long getExpForThisLevel() {
		if (getLevel() >= (character().getMaxPetLevel() + 1)) {
			return 0;
		}
		return ExperienceData.getInstance().getExpForLevel(getLevel());
	}
	
	public long getExpForNextLevel() {
		if (getLevel() >= (character().getMaxPetLevel())) {
			return 0;
		}
		return ExperienceData.getInstance().getExpForLevel(getLevel() + 1);
	}
public short getSoulShotsPerHit() {
		if (getTemplate().getSoulShot() > 0) {
			return (short) getTemplate().getSoulShot();
		}
		return 1;
	}
	
	public short getSpiritShotsPerHit() {
		if (getTemplate().getSpiritShot() > 0) {
			return (short) getTemplate().getSpiritShot();
		}
		return 1;
	}
public void followOwner() {
		setFollowStatus(true);
	}
	
	public boolean doDie(L2Character killer, boolean decayed) {
		if (!super.doDie(killer)) {
			return false;
		}
		if (!decayed) {
			DecayTaskManager.getInstance().add(this);
		}
		return true;
	}
public void stopDecay() {
		DecayTaskManager.getInstance().cancel(this);
	}
	
	public void unSummon(L2PcInstance owner) {
		if (isVisible() && !isDead()) {
			getAI().stopFollow();
			if (owner != null) {
				owner.sendPacket(new PetDelete(getSummonType(), getObjectId()));
				final L2Party party = owner.getParty();
				if (party != null) {
					party.broadcastToPartyMembers(owner, new ExPartyPetWindowDelete(this));
				}

				if ((getInventory() != null) && (getInventory().getSize() > 0)) {
					getOwner().setPetInvItems(true);
					sendPacket(SystemMessageId.ITEMS_IN_PET_INVENTORY);
				} else {
					getOwner().setPetInvItems(false);
				}
			}
			abortAttack();
			abortCast();
			storeMe();
			storeEffect(true);
			if (owner != null) {
				owner.setPet(null);
			}

			// Stop AI tasks
			if (hasAI()) {
				getAI().stopAITask();
			}

			stopAllEffects();
			L2WorldRegion oldRegion = getWorldRegion();
			decayMe();
			if (oldRegion != null) {
				oldRegion.removeFromZones(this);
			}
			getKnownList().removeAllKnownObjects();
			setTarget(null);
			if (owner != null) {
				for (int itemId : owner.getAutoSoulShot()) {
					String handler = ((L2EtcItem) ItemTable.getInstance().getTemplate(itemId)).getHandlerName();
					if ((handler != null) && handler.contains("Beast")) {
						owner.disableAutoShot(itemId);
					}
				}
			}
		}
	}
	public abstract int getSummonType();
	
		@Override
	public PetInventory getInventory() {
		return null;
	}
	@Override
	public void onTeleported() {
		super.onTeleported();
		sendPacket(new TeleportToLocation(this, getX(), getY(), getZ(), getHeading()));
	}
	
		@Override
	public final void broadcastPacket(L2GameServerPacket mov, int radiusInKnownlist) {
		if (getOwner() != null) {
			mov.setInvisible(getOwner().isInvisible());
		}
		super.broadcastPacket(mov, radiusInKnownlist);
	}
@Override
	public void broadcastStatusUpdate() {
		super.broadcastStatusUpdate();
		updateAndBroadcastStatus(1);
	}
	
		@Override
	public void doCast(Skill skill) {
		final L2PcInstance actingPlayer = getActingPlayer();
		if (!actingPlayer.checkPvpSkill(getTarget(), skill) && !actingPlayer.getAccessLevel().allowPeaceAttack()) {
			// Send a System Message to the L2PcInstance
			actingPlayer.sendPacket(SystemMessageId.TARGET_IS_INCORRECT);

			// Send a Server->Client packet ActionFailed to the L2PcInstance
			actingPlayer.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}

		super.doCast(skill);
	}
	@Override
	public final void broadcastPacket(L2GameServerPacket mov) {
		if (getOwner() != null) {
			mov.setInvisible(getOwner().isInvisible());
		}
		super.broadcastPacket(mov);
	}
	
	@Override
	protected L2CharacterAI initAI() {
		return new L2SummonAI(this);
	}
	@Override
	public L2Weapon getActiveWeaponItem() {
		return null;
	}
	
	@Override
	public void setIsImmobilized(boolean value) {
		super.setIsImmobilized(value);

		if (value) {
			_previousFollowStatus = getFollowStatus();
			// if immobilized temporarily disable follow mode
			if (_previousFollowStatus) {
				setFollowStatus(false);
			}
		} else {
			// if not more immobilized restore previous follow mode
			setFollowStatus(_previousFollowStatus);
		}
	}

	public boolean getFollowStatus() {
		return _follow;
	}
	
	public void setFollowStatus(boolean state) {
		_follow = state;
		if (_follow) {
			getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, getOwner());
		} else {
			getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE, null);
		}
	}

	@Override
	public L2Summon getSummon() {
		return this;
	}
	
		/**
	 * Return True if the L2Summon is invulnerable or if the summoner is in spawn protection.
	 */
	@Override
	public boolean isInvul() {
		return super.isInvul() || getOwner().isSpawnProtected();
	}

	@Override
	public boolean isUndead() {
		return getTemplate().getRace() == Race.UNDEAD;
	}
	
@Override
	public void onSpawn() {
		super.onSpawn();

		if (character().summonStoreSkillCooltime() && !isTeleporting()) {
			restoreEffects();
		}

		setFollowStatus(true);
		updateAndBroadcastStatus(0);
		sendPacket(new RelationChanged(this, getOwner().getRelation(getOwner()), false));
		for (L2PcInstance player : getOwner().getKnownList().getKnownPlayersInRadius(800)) {
			player.sendPacket(new RelationChanged(this, getOwner().getRelation(player), isAutoAttackable(player)));
		}
		L2Party party = getOwner().getParty();
		if (party != null) {
			party.broadcastToPartyMembers(getOwner(), new ExPartyPetWindowAdd(this));
		}
		setShowSummonAnimation(false); // addVisibleObject created the info packets with summon animation
		// if someone comes into range now, the animation shouldn't show any more
		_restoreSummon = false;

		// Notify to scripts
		EventDispatcher.getInstance().notifyEventAsync(new OnPlayerSummonSpawn(this), this);
	}

@Override
	public final void stopAllEffects() {
		super.stopAllEffects();
		updateAndBroadcastStatus(1);
	}
	
@Override
	public final void stopAllEffectsExceptThoseThatLastThroughDeath() {
		super.stopAllEffectsExceptThoseThatLastThroughDeath();
		updateAndBroadcastStatus(1);
	}
	
	@Override
	public void updateAbnormalEffect() {
		for (L2PcInstance player : getKnownList().getKnownPlayers().values()) {
			player.sendPacket(new SummonInfo(this, player, 1));
		}
	}
	
	@Override
	public boolean isInCombat() {
		return (getOwner() != null) && getOwner().isInCombat();
	}
	
	@Override
	public void onDecay() {
		deleteMe(_owner);
	}

public void deleteMe(L2PcInstance owner) {
		if (owner != null) {
			owner.sendPacket(new PetDelete(getSummonType(), getObjectId()));
			final L2Party party = owner.getParty();
			if (party != null) {
				party.broadcastToPartyMembers(owner, new ExPartyPetWindowDelete(this));
			}

			for (int itemId : owner.getAutoSoulShot()) {
				String handler = ((L2EtcItem) ItemTable.getInstance().getTemplate(itemId)).getHandlerName();
				if ((handler != null) && handler.contains("Beast")) {
					owner.disableAutoShot(itemId);
				}
			}
		}

		// pet will be deleted along with all his items
		if (getInventory() != null) {
			getInventory().destroyAllItems("pet deleted", getOwner(), this);
		}
		decayMe();
		getKnownList().removeAllKnownObjects();
		if (owner != null) {
			owner.setPet(null);
		}
		super.deleteMe();
	}
	
		@Override
	public L2ItemInstance getActiveWeaponInstance() {
		return null;
	}

		@Override
	public L2Weapon getSecondaryWeaponItem() {
		return null;
	}
	
	/**
	 * Return True if the L2Character has a Party in progress.
	 */
	@Override
	public boolean isInParty() {
		return (_owner != null) && _owner.isInParty();
	}
	@Override
	public L2ItemInstance getSecondaryWeaponInstance() {
		return null;
	}
	
	/**
	 * Return the L2Party object of its L2PcInstance owner or null.
	 */
	@Override
	public L2Party getParty() {
		if (_owner == null) {
			return null;
		}

		return _owner.getParty();
	}

	@Override
	public void reduceCurrentHp(double damage, L2Character attacker, Skill skill) {
		super.reduceCurrentHp(damage, attacker, skill);
		if ((getOwner() != null) && (attacker != null)) {
			SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_RECEIVED_DAMAGE_OF_S3_FROM_C2);
			sm.addNpcName(this);
			sm.addCharName(attacker);
			sm.addInt((int) damage);
			sendPacket(sm);
		}
	}
	
	@Override
	public void sendDamageMessage(L2Character target, int damage, boolean mcrit, boolean pcrit, boolean miss) {
		if (miss || (getOwner() == null)) {
			return;
		}

		// Prevents the double spam of system messages, if the target is the owning player.
		if (target.getObjectId() != getOwner().getObjectId()) {
			if (pcrit || mcrit) {
				if (isServitor()) {
					sendPacket(SystemMessageId.CRITICAL_HIT_BY_SUMMONED_MOB);
				} else {
					sendPacket(SystemMessageId.CRITICAL_HIT_BY_PET);
				}
			}

			if (getOwner().isInOlympiadMode() && (target instanceof L2PcInstance) && ((L2PcInstance) target).isInOlympiadMode() && (((L2PcInstance) target).getOlympiadGameId() == getOwner().getOlympiadGameId())) {
				OlympiadGameManager.getInstance().notifyCompetitorDamage(getOwner(), damage);
			}

			final SystemMessage sm;

			if (target.isInvul() && !(target instanceof L2NpcInstance)) {
				sm = SystemMessage.getSystemMessage(SystemMessageId.ATTACK_WAS_BLOCKED);
			} else {
				sm = SystemMessage.getSystemMessage(SystemMessageId.C1_DONE_S3_DAMAGE_TO_C2);
				sm.addNpcName(this);
				sm.addCharName(target);
				sm.addInt(damage);
			}

			sendPacket(sm);
		}
	}
	
@Override
	public final Team getTeam() {
		return getOwner() != null ? getOwner().getTeam() : Team.NONE;
	}
	
		@Override
	public int getClanId() {
		return (getOwner() != null) ? getOwner().getClanId() : 0;
	}
	
	@Override
	public int getAllyId() {
		return (getOwner() != null) ? getOwner().getAllyId() : 0;
	}
	
@Override
	public L2NpcTemplate getTemplate() {
		return (L2NpcTemplate) super.getTemplate();
	}

	public void updateAndBroadcastStatus(int val) {
		if (getOwner() == null) {
			return;
		}

		sendPacket(new PetInfo(this, val));
		sendPacket(new PetStatusUpdate(this));
		if (isVisible()) {
			broadcastNpcInfo(val);
		}
		L2Party party = getOwner().getParty();
		if (party != null) {
			party.broadcastToPartyMembers(getOwner(), new ExPartyPetWindowUpdate(this));
		}
		updateEffectIcons(true);
	}
	
		public void broadcastNpcInfo(int val) {
		for (L2PcInstance player : getKnownList().getKnownPlayers().values()) {
			if ((player == null) || (player == getOwner())) {
				continue;
			}
			player.sendPacket(new SummonInfo(this, player, val));
		}
	}
	
@Override
	public final SummonKnownList getKnownList() {
		return (SummonKnownList) super.getKnownList();
	}
	
	@Override
	public void initKnownList() {
		setKnownList(new SummonKnownList(this));
	}

@Override
	public SummonStat getStat() {
		return (SummonStat) super.getStat();
	}
	
	@Override
	public void initCharStat() {
		setStat(new SummonStat(this));
	}

@Override
	public SummonStatus getStatus() {
		return (SummonStatus) super.getStatus();
	}
	
@Override
	public void initCharStatus() {
		setStatus(new SummonStatus(this));
	}

@Override
	public boolean doDie(L2Character killer) {
		if (isNoblesseBlessedAffected()) {
			stopEffects(L2EffectType.NOBLESSE_BLESSING);
			storeEffect(true);
		} else {
			storeEffect(false);
		}

		if (!super.doDie(killer)) {
			return false;
		}

		final L2PcInstance owner = getOwner();
		if (owner != null) {
			for (L2Character TgMob : getKnownList().getKnownCharacters()) {
				// get the mobs which have aggro on the this instance
				if (TgMob instanceof L2Attackable) {
					if (TgMob.isDead()) {
						continue;
					}

					AggroInfo info = ((L2Attackable) TgMob).getAggroList().get(this);
					if (info != null) {
						((L2Attackable) TgMob).addDamageHate(owner, info.getDamage(), info.getHate());
					}
				}
			}
		}

		DecayTaskManager.getInstance().add(this);
		return true;
	}
	
@Override
	public final int getKarma() {
		return getOwner() != null ? getOwner().getKarma() : 0;
	}
	
	@Override
	public final byte getPvpFlag() {
		return getOwner() != null ? getOwner().getPvpFlag() : 0;
	}
	
	/**
	 * Check if the active L2Skill can be casted.<br>
	 * <B><U>Actions</U>:</B>
	 * <ul>
	 * <li>Check if the target is correct</li>
	 * <li>Check if the target is in the skill cast range</li>
	 * <li>Check if the summon owns enough HP and MP to cast the skill</li>
	 * <li>Check if all skills are enabled and this skill is enabled</li>
	 * <li>Check if the skill is active</li>
	 * <li>Notify the AI with AI_INTENTION_CAST and target</li>
	 * </ul>
	 * @param skill The L2Skill to use
	 * @param forceUse used to force ATTACK on players
	 * @param dontMove used to prevent movement, if not in range
	 */
	@Override
	public boolean useMagic(Skill skill, boolean forceUse, boolean dontMove) {
		// Null skill, dead summon or null owner are reasons to prevent casting.
		if ((skill == null) || isDead() || (getOwner() == null)) {
			return false;
		}

		// Check if the skill is active
		if (skill.isPassive()) {
			// just ignore the passive skill request. why does the client send it anyway ??
			return false;
		}

		// If a skill is currently being used
		if (isCastingNow()) {
			return false;
		}

		// Set current pet skill
		getOwner().setCurrentPetSkill(skill, forceUse, dontMove);

		// Get the target for the skill
		L2Object target = switch (skill.getTargetType()) {
			// OWNER_PET should be cast even if no target has been found
			case OWNER_PET -> getOwner();
			// PARTY, AURA, SELF should be cast even if no target has been found
			case PARTY, AURA, FRONT_AURA, BEHIND_AURA, SELF, AURA_CORPSE_MOB, AURA_UNDEAD_ENEMY, COMMAND_CHANNEL -> this;
			// Get the first target of the list
			default -> skill.getFirstOfTargetList(this);
		};

		// Check the validity of the target
		if (target == null) {
			sendPacket(SystemMessageId.TARGET_CANT_FOUND);
			return false;
		}

		// Check if this skill is enabled (e.g. reuse time)
		if (isSkillDisabled(skill)) {
			sendPacket(SystemMessageId.PET_SKILL_CANNOT_BE_USED_RECHARCHING);
			return false;
		}

		// Check if the summon has enough MP
		if (getCurrentMp() < (getStat().getMpConsume1(skill) + getStat().getMpConsume2(skill))) {
			// Send a System Message to the caster
			sendPacket(SystemMessageId.NOT_ENOUGH_MP);
			return false;
		}

		// Check if the summon has enough HP
		if (getCurrentHp() <= skill.getHpConsume()) {
			// Send a System Message to the caster
			sendPacket(SystemMessageId.NOT_ENOUGH_HP);
			return false;
		}

		if ((this != target) && skill.isPhysical() && //
			(geodata().getPathFinding() > 0) && //
			(PathFinding.getInstance().findPath(getX(), getY(), getZ(), target.getX(), target.getY(), target.getZ(), getInstanceId(), true) == null)) {
			sendPacket(SystemMessageId.CANT_SEE_TARGET);
			return false;
		}

		// Check if this is bad magic skill
		if (skill.isBad()) {
			if (getOwner() == target) {
				return false;
			}

			// Summons can cast skills on NPCs inside peace zones.
			if (isInsidePeaceZone(this, target) && !getOwner().getAccessLevel().allowPeaceAttack()) {
				// If summon or target is in a peace zone, send a system message:
				sendPacket(SystemMessageId.TARGET_IN_PEACEZONE);
				return false;
			}

			// If L2PcInstance is in Olympiad and the match isn't already start, send a Server->Client packet ActionFailed
			if (getOwner().isInOlympiadMode() && !getOwner().isOlympiadStart()) {
				sendPacket(ActionFailed.STATIC_PACKET);
				return false;
			}

			if ((target.getActingPlayer() != null) && (getOwner().getSiegeState() > 0) && getOwner().isInsideZone(ZoneId.SIEGE) && (target.getActingPlayer().getSiegeState() == getOwner().getSiegeState()) && (target.getActingPlayer() != getOwner())
				&& (target.getActingPlayer().getSiegeSide() == getOwner().getSiegeSide())) {
				if (TerritoryWarManager.getInstance().isTWInProgress()) {
					sendPacket(SystemMessageId.YOU_CANNOT_ATTACK_A_MEMBER_OF_THE_SAME_TERRITORY);
				} else {
					sendPacket(SystemMessageId.FORCED_ATTACK_IS_IMPOSSIBLE_AGAINST_SIEGE_SIDE_TEMPORARY_ALLIED_MEMBERS);
				}
				sendPacket(ActionFailed.STATIC_PACKET);
				return false;
			}

			// Check if the target is attackable
			if (target.isDoor()) {
				if (!target.isAutoAttackable(getOwner())) {
					return false;
				}
			} else {
				// Summons can cast skills on NPCs inside peace zones.
				if (!target.canBeAttacked() && !getOwner().getAccessLevel().allowPeaceAttack()) {
					return false;
				}

				// Check if a Forced attack is in progress on non-attackable target
				if (!target.isAutoAttackable(this) && !forceUse && !target.isNpc() && (skill.getTargetType() != TargetType.AURA) && (skill.getTargetType() != TargetType.FRONT_AURA) && (skill.getTargetType() != TargetType.BEHIND_AURA) && (skill.getTargetType() != TargetType.CLAN)
					&& (skill.getTargetType() != TargetType.PARTY) && (skill.getTargetType() != TargetType.SELF)) {
					return false;
				}
			}
		}
		// Notify the AI with AI_INTENTION_CAST and target
		getAI().setIntention(CtrlIntention.AI_INTENTION_CAST, skill, target);
		return true;
	}
	
public final L2PcInstance getOwner() {
		return _owner;
	}

		public void setOwner(L2PcInstance newOwner) {
		_owner = newOwner;
	}
	
	public int getAttackRange() {
		return _attackRange;
	}	// this defines the action buttons, 1 for Summon, 2 for Pets
	
public void setAttackRange(int range) {
		_attackRange = Math.max(range, 36);
	}
	
@Override
	public boolean isAutoAttackable(L2Character attacker) {
		return (_owner != null) && _owner.isAutoAttackable(attacker);
	}
	
@Override
	public boolean isSummon() {
		return true;
	}
	
	@Override
	public void sendInfo(L2PcInstance activeChar) {
		// Check if the L2PcInstance is the owner of the Pet
		if (activeChar == getOwner()) {
			activeChar.sendPacket(new PetInfo(this, 0));
			// The PetInfo packet wipes the PartySpelled (list of active spells' icons). Re-add them
			updateEffectIcons(true);
			if (isPet()) {
				activeChar.sendPacket(new PetItemList(getInventory().getItems()));
			}
		} else {
			activeChar.sendPacket(new SummonInfo(this, activeChar, 0));
		}
	}
	
	@Override
	public void sendPacket(SystemMessageId id) {
		if (getOwner() != null) {
			getOwner().sendPacket(id);
		}
	}
	
	@Override
	public void sendPacket(L2GameServerPacket mov) {
		if (getOwner() != null) {
			getOwner().sendPacket(mov);
		}
	}
	
@Override
	public L2PcInstance getActingPlayer() {
		return getOwner();
	}
	
@Override
	public boolean isChargedShot(ShotType type) {
		return (_shotsMask & type.getMask()) == type.getMask();
	}
	
@Override
	public void setChargedShot(ShotType type, boolean charged) {
		if (charged) {
			_shotsMask |= type.getMask();
		} else {
			_shotsMask &= ~type.getMask();
		}
	}
	
@Override
	public void rechargeShots(boolean physical, boolean magic) {
		L2ItemInstance item;
		IItemHandler handler;

		if ((getOwner().getAutoSoulShot() == null) || getOwner().getAutoSoulShot().isEmpty()) {
			return;
		}

		for (int itemId : getOwner().getAutoSoulShot()) {
			item = getOwner().getInventory().getItemByItemId(itemId);

			if (item != null) {
				if (magic) {
					if (item.getItem().getDefaultAction() == ActionType.SUMMON_SPIRITSHOT) {
						handler = ItemHandler.getInstance().getHandler(item.getEtcItem());
						if (handler != null) {
							handler.useItem(getOwner(), item, false);
						}
					}
				}

				if (physical) {
					if (item.getItem().getDefaultAction() == ActionType.SUMMON_SOULSHOT) {
						handler = ItemHandler.getInstance().getHandler(item.getEtcItem());
						if (handler != null) {
							handler.useItem(getOwner(), item, false);
						}
					}
				}
			} else {
				getOwner().removeAutoSoulShot(itemId);
			}
		}
	}
	
	@Override
	public String toString() {
		return super.toString() + "(" + getId() + ") Level: " + getLevel() + " Owner: " + getOwner();
	}
	
	public int getControlObjectId() {
		return 0;
	}
	
	public L2Weapon getActiveWeapon() {
		return null;
	}
	
public boolean isRestoreSummon() {
		return _restoreSummon;
	}
	
	public void setRestoreSummon(boolean val) {
	}
	
	public boolean isHungry() {
		return false;
	}
	
public int getWeapon() {
		return 0;
	}
	
public int getArmor() {
		return 0;
	}
	
/**
	 * Change the summon's state.
	 */
	public void switchMode() {
		// Do nothing.
	}
	
	/**
	 * Verify if the summon can perform an attack.
	 * @param ctrlPressed {@code true} if Ctrl key is pressed
	 * @return {@code true} if the summon can attack, {@code false} otherwise
	 */
	public final boolean canAttack(boolean ctrlPressed) {
		if (getOwner() == null) {
			return false;
		}

		final L2Object target = getOwner().getTarget();
		if ((target == null) || (this == target) || (getOwner() == target)) {
			return false;
		}

		// Sin eater, Big Boom, Wyvern can't attack with attack button.
		final int npcId = getId();
		if (PASSIVE_SUMMONS.contains(npcId)) {
			getOwner().sendPacket(ActionFailed.STATIC_PACKET);
			return false;
		}

		if (isBetrayed()) {
			sendPacket(SystemMessageId.PET_REFUSING_ORDER);
			sendPacket(ActionFailed.STATIC_PACKET);
			return false;
		}

		if (isAttackingDisabled()) {
			if (!isAttackingNow()) {
				return false;
			}
			getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, target);
		}

		if (isPet() && ((getLevel() - getOwner().getLevel()) > 20)) {
			sendPacket(SystemMessageId.PET_TOO_HIGH_TO_CONTROL);
			sendPacket(ActionFailed.STATIC_PACKET);
			return false;
		}

		if (getOwner().isInOlympiadMode() && !getOwner().isOlympiadStart()) {
			// If owner is in Olympiad and the match isn't already start, send a Server->Client packet ActionFailed
			getOwner().sendPacket(ActionFailed.STATIC_PACKET);
			return false;
		}

		if ((target.getActingPlayer() != null) && (getOwner().getSiegeState() > 0) && getOwner().isInsideZone(ZoneId.SIEGE) && (target.getActingPlayer().getSiegeSide() == getOwner().getSiegeSide())) {
			if (TerritoryWarManager.getInstance().isTWInProgress()) {
				sendPacket(SystemMessageId.YOU_CANNOT_ATTACK_A_MEMBER_OF_THE_SAME_TERRITORY);
			} else {
				sendPacket(SystemMessageId.FORCED_ATTACK_IS_IMPOSSIBLE_AGAINST_SIEGE_SIDE_TEMPORARY_ALLIED_MEMBERS);
			}
			sendPacket(ActionFailed.STATIC_PACKET);
			return false;
		}

		if (!getOwner().getAccessLevel().allowPeaceAttack() && getOwner().isInsidePeaceZone(this, target)) {
			sendPacket(SystemMessageId.TARGET_IN_PEACEZONE);
			return false;
		}

		if (isLockedTarget()) {
			sendPacket(SystemMessageId.FAILED_CHANGE_TARGET);
			return false;
		}

		// Summons can attack NPCs even when the owner cannot.
		if (!target.isAutoAttackable(getOwner()) && !ctrlPressed && !target.isNpc()) {
			setFollowStatus(false);
			getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, target);
			sendPacket(SystemMessageId.INCORRECT_TARGET);
			return false;
		}

		// Siege golems AI doesn't support attacking other than doors/walls at the moment.
		if (target.isDoor() && (getTemplate().getRace() != Race.SIEGE_WEAPON)) {
			return false;
		}

		return true;
	}
	
	/**
	 * Cancel the summon's action.
	 */
	public void cancelAction() {
		if (!isMovementDisabled()) {
			getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE, null);
		}
	}
	
	/**
	 * Performs an attack to the owner's target.
	 */
	public void doAttack() {
		if (getOwner() != null) {
			final L2Object target = getOwner().getTarget();
			if (target != null) {
				setTarget(target);
				getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, target);
			}
		}
	}
	
	/**
	 * Gets the summon ID.
	 * @return the summon ID
	 */
	@Override
	public final int getId() {
		return getTemplate().getId();
	}
	
public int getFormId() {
		int formId = 0;
		final int npcId = getId();
		if ((npcId == 16041) || (npcId == 16042)) {
			if (getLevel() > 69) {
				formId = 3;
			} else if (getLevel() > 64) {
				formId = 2;
			} else if (getLevel() > 59) {
				formId = 1;
			}
		} else if ((npcId == 16025) || (npcId == 16037)) {
			if (getLevel() > 69) {
				formId = 3;
			} else if (getLevel() > 64) {
				formId = 2;
			} else if (getLevel() > 59) {
				formId = 1;
			}
		}
		return formId;
	}
	

}
