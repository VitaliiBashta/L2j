package com.l2jserver.gameserver.ai;

import com.l2jserver.gameserver.GameTimeController;
import com.l2jserver.gameserver.ThreadPoolManager;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.L2Summon;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.skills.Skill;
import com.l2jserver.gameserver.network.serverpackets.*;
import com.l2jserver.gameserver.taskmanager.AttackStanceTaskManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Future;

import static com.l2jserver.gameserver.ai.CtrlIntention.AI_INTENTION_ATTACK;
import static com.l2jserver.gameserver.ai.CtrlIntention.AI_INTENTION_FOLLOW;
import static com.l2jserver.gameserver.ai.CtrlIntention.AI_INTENTION_IDLE;


public abstract class AbstractAI {
	protected static final Logger LOG = LoggerFactory.getLogger(AbstractAI.class);
	private static final int FOLLOW_INTERVAL = 1000;
	private static final int ATTACK_FOLLOW_INTERVAL = 500;
	/** The character that this AI manages */
	protected final L2Character _actor;
	/** Current long-term intention */
	protected CtrlIntention _intention = AI_INTENTION_IDLE;
	/** Current long-term intention parameter */
	protected Object _intentionArg0 = null;
	/** Current long-term intention parameter */
	protected Object _intentionArg1 = null;
	/** Flags about client's state, in order to know which messages to send */
	protected volatile boolean _clientMoving;
	/** Flags about client's state, in order to know which messages to send */
	protected volatile boolean _clientAutoAttacking;
	/** Flags about client's state, in order to know which messages to send */
	protected int _clientMovingToPawnOffset;
	protected L2Character _attackTarget;
	protected L2Character _followTarget;
	protected Future<?> _followTask = null;
	/** The skill we are currently casting by INTENTION_CAST */
	Skill _skill;
	private NextAction nextAction;
	/** Different targets this AI maintains */
	private L2Object _target;
	private L2Character _castTarget;
	/** Different internal state flags */
	private int _moveToPawnTimeout;
	
	/**
	 * Constructor of AbstractAI.
	 * @param creature the creature
	 */
	protected AbstractAI(L2Character creature) {
		_actor = creature;
	}

	public NextAction getNextAction() {
		return nextAction;
	}

	public void setNextAction(NextAction nextAction) {
		this.nextAction = nextAction;
	}
	
	public CtrlIntention getIntention() {
		return _intention;
	}
	
	public final void setIntention(CtrlIntention intention) {
		setIntention(intention, null, null);
	}
	
	public final void setIntention(CtrlIntention intention, Object arg0, Object arg1) {
		// Stop the follow mode if necessary
		if ((intention != AI_INTENTION_FOLLOW) && (intention != AI_INTENTION_ATTACK)) {
			stopFollow();
		}

		// Launch the onIntention method of the L2CharacterAI corresponding to the new Intention
		switch (intention) {
			case AI_INTENTION_IDLE -> onIntentionIdle();
			case AI_INTENTION_ACTIVE -> onIntentionActive();
			case AI_INTENTION_REST -> onIntentionRest();
			case AI_INTENTION_ATTACK -> onIntentionAttack((L2Character) arg0);
			case AI_INTENTION_CAST -> onIntentionCast((Skill) arg0, (L2Object) arg1);
			case AI_INTENTION_MOVE_TO -> onIntentionMoveTo((Location) arg0);
			case AI_INTENTION_FOLLOW -> onIntentionFollow((L2Character) arg0);
			case AI_INTENTION_PICK_UP -> onIntentionPickUp((L2Object) arg0);
			case AI_INTENTION_INTERACT -> onIntentionInteract((L2Object) arg0);
		}

		// If do move or follow intention drop next action.
		if ((nextAction != null) && nextAction.getIntentions().contains(intention)) {
			nextAction = null;
		}
	}
	
	protected abstract void onIntentionIdle();
	
	protected abstract void onIntentionActive();
	
	protected abstract void onIntentionRest();
	
	protected abstract void onIntentionAttack(L2Character target);
	
	protected abstract void onIntentionCast(Skill skill, L2Object target);
	
	protected abstract void onIntentionMoveTo(Location destination);
	
	protected abstract void onIntentionFollow(L2Character target);
	
	protected abstract void onIntentionPickUp(L2Object item);
	
	protected abstract void onIntentionInteract(L2Object object);
	
	/**
	 * Stop an AI Follow Task.
	 */
	public synchronized void stopFollow() {
		if (_followTask != null) {
			// Stop the Follow Task
			_followTask.cancel(false);
			_followTask = null;
		}
		_followTarget = null;
	}
	
	/**
	 * @return the current cast target.
	 */
	public L2Character getCastTarget() {
		return _castTarget;
	}
	
	protected void setCastTarget(L2Character target) {
		_castTarget = target;
	}
	
	public L2Character getAttackTarget() {
		return _attackTarget;
	}
	
	protected void setAttackTarget(L2Character target) {
		_attackTarget = target;
	}
	
	/**
	 * Set the Intention of this AbstractAI.<br>
	 * <FONT COLOR=#FF0000><B> <U>Caution</U> : This method is USED by AI classes</B></FONT><B><U><br>
	 * Overridden in </U> : </B><BR>
	 * <B>L2AttackableAI</B> : Create an AI Task executed every 1s (if necessary)<BR>
	 * <B>L2PlayerAI</B> : Stores the current AI intention parameters to later restore it if necessary.
	 * @param intention The new Intention to set to the AI
	 * @param arg0 The first parameter of the Intention
	 * @param arg1 The second parameter of the Intention
	 */
	synchronized void changeIntention(CtrlIntention intention, Object arg0, Object arg1) {
		_intention = intention;
		_intentionArg0 = arg0;
		_intentionArg1 = arg1;
	}
	
	public final void setIntention(CtrlIntention intention, Object arg0) {
		setIntention(intention, arg0, null);
	}
	
	public final void notifyEvent(CtrlEvent evt) {
		notifyEvent(evt, null, null);
	}
	
	public final void notifyEvent(CtrlEvent evt, Object... args) {
		if ((!_actor.isVisible() && !_actor.isTeleporting()) || !_actor.hasAI()) {
			return;
		}

		switch (evt) {
			case EVT_THINK:
				onEvtThink();
				break;
			case EVT_ATTACKED:
				onEvtAttacked((L2Character) args[0]);
				break;
			case EVT_AGGRESSION:
				onEvtAggression((L2Character) args[0], ((Number) args[1]).intValue());
				break;
			case EVT_STUNNED:
				onEvtStunned((L2Character) args[0]);
				break;
			case EVT_PARALYZED:
				onEvtParalyzed((L2Character) args[0]);
				break;
			case EVT_SLEEPING:
				onEvtSleeping((L2Character) args[0]);
				break;
			case EVT_ROOTED:
				onEvtRooted((L2Character) args[0]);
				break;
			case EVT_CONFUSED:
				onEvtConfused((L2Character) args[0]);
				break;
			case EVT_MUTED:
				onEvtMuted((L2Character) args[0]);
				break;
			case EVT_EVADED:
				onEvtEvaded((L2Character) args[0]);
				break;
			case EVT_READY_TO_ACT:
				if (!_actor.isCastingNow() && !_actor.isCastingSimultaneouslyNow()) {
					onEvtReadyToAct();
				}
				break;
			case EVT_USER_CMD:
				onEvtUserCmd(args[0], args[1]);
				break;
			case EVT_ARRIVED:
				// happens e.g. from stopmove but we don't process it if we're casting
				if (!_actor.isCastingNow() && !_actor.isCastingSimultaneouslyNow()) {
					onEvtArrived();
				}
				break;
			case EVT_ARRIVED_REVALIDATE:
				// this is disregarded if the char is not moving any more
				if (_actor.isMoving()) {
					onEvtArrivedRevalidate();
				}
				break;
			case EVT_ARRIVED_BLOCKED:
				onEvtArrivedBlocked((Location) args[0]);
				break;
			case EVT_FORGET_OBJECT:
				onEvtForgetObject((L2Object) args[0]);
				break;
			case EVT_CANCEL:
				onEvtCancel();
				break;
			case EVT_DEAD:
				onEvtDead();
				break;
			case EVT_FAKE_DEATH:
				onEvtFakeDeath();
				break;
			case EVT_FINISH_CASTING:
				onEvtFinishCasting();
				break;
			case EVT_AFRAID: {
				onEvtAfraid((L2Character) args[0], (Boolean) args[1]);
				break;
			}
		}

		// Do next action.
		if ((nextAction != null) && nextAction.getEvents().contains(evt)) {
			nextAction.doAction();
		}
	}
	
	protected abstract void onEvtThink();
	
	protected abstract void onEvtAttacked(L2Character attacker);
	
	protected abstract void onEvtAggression(L2Character target, long aggro);
	
	protected abstract void onEvtStunned(L2Character attacker);
	
	protected abstract void onEvtParalyzed(L2Character attacker);
	
	protected abstract void onEvtSleeping(L2Character attacker);
	
	protected abstract void onEvtRooted(L2Character attacker);
	
	protected abstract void onEvtConfused(L2Character attacker);
	
	protected abstract void onEvtMuted(L2Character attacker);
	
	protected abstract void onEvtEvaded(L2Character attacker);
	
	protected abstract void onEvtReadyToAct();
	
	protected abstract void onEvtUserCmd(Object arg0, Object arg1);
	
	protected abstract void onEvtArrived();
	
	protected abstract void onEvtArrivedRevalidate();
	
	protected abstract void onEvtArrivedBlocked(Location blocked_at_pos);
	
	protected abstract void onEvtForgetObject(L2Object object);
	
	protected abstract void onEvtCancel();
	
	protected abstract void onEvtDead();
	
	protected abstract void onEvtFakeDeath();
	
	protected abstract void onEvtFinishCasting();
	
	protected abstract void onEvtAfraid(L2Character effector, boolean start);
	
	public final void notifyEvent(CtrlEvent evt, Object arg0) {
		notifyEvent(evt, arg0, null);
	}
	
	/**
	 * Move the actor to Pawn server side AND client side by sending Server->Client packet MoveToPawn <I>(broadcast)</I>.<br>
	 * <FONT COLOR=#FF0000><B> <U>Caution</U> : Low level function, used by AI subclasses</B></FONT>
	 */
	protected void moveToPawn(L2Object pawn, int offset) {
		// Check if actor can move
		if (!_actor.isMovementDisabled()) {
			if (offset < 10) {
				offset = 10;
			}

			// prevent possible extra calls to this function (there is none?),
			// also don't send movetopawn packets too often
			boolean sendPacket = true;
			if (_clientMoving && (_target == pawn)) {
				if (_clientMovingToPawnOffset == offset) {
					if (GameTimeController.getInstance().getGameTicks() < _moveToPawnTimeout) {
						return;
					}
					sendPacket = false;
				} else if (_actor.isOnGeodataPath()) {
					// minimum time to calculate new route is 2 seconds
					if (GameTimeController.getInstance().getGameTicks() < (_moveToPawnTimeout + 10)) {
						return;
					}
				}
			}

			// Set AI movement data
			_clientMoving = true;
			_clientMovingToPawnOffset = offset;
			_target = pawn;
			_moveToPawnTimeout = GameTimeController.getInstance().getGameTicks();
			_moveToPawnTimeout += 1000 / GameTimeController.MILLIS_IN_TICK;

			if (pawn == null) {
				return;
			}

			// Calculate movement data for a move to location action and add the actor to movingObjects of GameTimeController
			_actor.moveToLocation(pawn.getX(), pawn.getY(), pawn.getZ(), offset);

			if (!_actor.isMoving()) {
				clientActionFailed();
				return;
			}

			// Send a Server->Client packet MoveToPawn/CharMoveToLocation to the actor and all L2PcInstance in its _knownPlayers
			if (pawn instanceof L2Character) {
				if (_actor.isOnGeodataPath()) {
					_actor.broadcastPacket(new MoveToLocation(_actor));
					_clientMovingToPawnOffset = 0;
				} else if (sendPacket) {
					_actor.broadcastPacket(new MoveToPawn(_actor, (L2Character) pawn, offset));
				}
			} else {
				_actor.broadcastPacket(new MoveToLocation(_actor));
			}
		} else {
			clientActionFailed();
		}
	}
	
	/**
	 * Cancel action client side by sending Server->Client packet ActionFailed to the L2PcInstance actor. <FONT COLOR=#FF0000><B> <U>Caution</U> : Low level function, used by AI subclasses</B></FONT>
	 */
	protected void clientActionFailed() {
		if (_actor instanceof L2PcInstance) {
			_actor.sendPacket(ActionFailed.STATIC_PACKET);
		}
	}
	
	/**
	 * Move the actor to Location (x,y,z) server side AND client side by sending Server->Client packet CharMoveToLocation <I>(broadcast)</I>.<br>
	 * <FONT COLOR=#FF0000><B> <U>Caution</U> : Low level function, used by AI subclasses</B></FONT>
	 */
	protected void moveTo(int x, int y, int z) {
		// Chek if actor can move
		if (!_actor.isMovementDisabled()) {
			// Set AI movement data
			_clientMoving = true;
			_clientMovingToPawnOffset = 0;

			// Calculate movement data for a move to location action and add the actor to movingObjects of GameTimeController
			_actor.moveToLocation(x, y, z, 0);

			// Send a Server->Client packet CharMoveToLocation to the actor and all L2PcInstance in its _knownPlayers
			_actor.broadcastPacket(new MoveToLocation(_actor));

		} else {
			clientActionFailed();
		}
	}
	
	/**
	 * Stop the actor movement server side AND client side by sending Server->Client packet StopMove/StopRotation <I>(broadcast)</I>.<br>
	 * <FONT COLOR=#FF0000><B> <U>Caution</U> : Low level function, used by AI subclasses</B></FONT>
	 */
	protected void clientStopMoving(Location loc) {
		// Stop movement of the L2Character
		if (_actor.isMoving()) {
			_actor.stopMove(loc);
		}

		_clientMovingToPawnOffset = 0;

		if (_clientMoving || (loc != null)) {
			_clientMoving = false;

			// Send a Server->Client packet StopMove to the actor and all L2PcInstance in its _knownPlayers
			_actor.broadcastPacket(new StopMove(_actor));

			if (loc != null) {
				// Send a Server->Client packet StopRotation to the actor and all L2PcInstance in its _knownPlayers
				_actor.broadcastPacket(new StopRotation(_actor.getObjectId(), loc.getHeading(), 0));
			}
		}
	}
	
	/**
	 * Kill the actor client side by sending Server->Client packet AutoAttackStop, StopMove/StopRotation, Die <I>(broadcast)</I>.<br>
	 * <FONT COLOR=#FF0000><B> <U>Caution</U> : Low level function, used by AI subclasses</B></FONT>
	 */
	protected void clientNotifyDead() {
		// Send a Server->Client packet Die to the actor and all L2PcInstance in its _knownPlayers
		Die msg = new Die(_actor);
		_actor.broadcastPacket(msg);

		// Init AI
		_intention = AI_INTENTION_IDLE;
		_target = null;
		_castTarget = null;
		_attackTarget = null;

		// Cancel the follow task if necessary
		stopFollow();
	}
	
	/**
	 * Client has already arrived to target, no need to force StopMove packet.
	 */
	protected void clientStoppedMoving() {
		if (_clientMovingToPawnOffset > 0) // movetoPawn needs to be stopped
		{
			_clientMovingToPawnOffset = 0;
			_actor.broadcastPacket(new StopMove(_actor));
		}
		_clientMoving = false;
	}
	
	public boolean isAutoAttacking() {
		return _clientAutoAttacking;
	}
	
	public void setAutoAttacking(boolean isAutoAttacking) {
		if (_actor instanceof L2Summon) {
			L2Summon summon = (L2Summon) _actor;
			if (summon.getOwner() != null) {
				summon.getOwner().getAI().setAutoAttacking(isAutoAttacking);
			}
			return;
		}
		_clientAutoAttacking = isAutoAttacking;
	}
	
	/**
	 * Start the actor Auto Attack client side by sending Server->Client packet AutoAttackStart <I>(broadcast)</I>.<br>
	 * <FONT COLOR=#FF0000><B> <U>Caution</U> : Low level function, used by AI subclasses</B></FONT>
	 */
	public void clientStartAutoAttack() {
		if (_actor instanceof L2Summon) {
			L2Summon summon = (L2Summon) _actor;
			if (summon.getOwner() != null) {
				summon.getOwner().getAI().clientStartAutoAttack();
			}
			return;
		}
		if (!isAutoAttacking()) {
			if (_actor.isPlayer() && _actor.hasSummon()) {
				_actor.getSummon().broadcastPacket(new AutoAttackStart(_actor.getSummon().getObjectId()));
			}
			// Send a Server->Client packet AutoAttackStart to the actor and all L2PcInstance in its _knownPlayers
			_actor.broadcastPacket(new AutoAttackStart(_actor.getObjectId()));
			setAutoAttacking(true);
		}
		AttackStanceTaskManager.getInstance().addAttackStanceTask(_actor);
	}
	
	/**
	 * Stop the actor auto-attack client side by sending Server->Client packet AutoAttackStop <I>(broadcast)</I>.<br>
	 * <FONT COLOR=#FF0000><B> <U>Caution</U> : Low level function, used by AI subclasses</B></FONT>
	 */
	public void clientStopAutoAttack() {
		if (_actor instanceof L2Summon) {
			L2Summon summon = (L2Summon) _actor;
			if (summon.getOwner() != null) {
				summon.getOwner().getAI().clientStopAutoAttack();
			}
			return;
		}
		if (_actor instanceof L2PcInstance) {
			if (!AttackStanceTaskManager.getInstance().hasAttackStanceTask(_actor) && isAutoAttacking()) {
				AttackStanceTaskManager.getInstance().addAttackStanceTask(_actor);
			}
		} else if (isAutoAttacking()) {
			_actor.broadcastPacket(new AutoAttackStop(_actor.getObjectId()));
			setAutoAttacking(false);
		}
	}
	
	/**
	 * Create and Launch an AI Follow Task to execute every 1s.
	 * @param target The L2Character to follow
	 */
	public synchronized void startFollow(L2Character target) {
		if (_followTask != null) {
			_followTask.cancel(false);
			_followTask = null;
		}

		// Create and Launch an AI Follow Task to execute every 1s
		_followTarget = target;
		_followTask = ThreadPoolManager.getInstance().scheduleAiAtFixedRate(new FollowTask(), 5, FOLLOW_INTERVAL);
	}
	
	/**
	 * Update the state of this actor client side by sending Server->Client packet MoveToPawn/CharMoveToLocation and AutoAttackStart to the L2PcInstance player.<br>
	 * <FONT COLOR=#FF0000><B> <U>Caution</U> : Low level function, used by AI subclasses</B></FONT>
	 * @param player The L2PcInstance to notify with state of this L2Character
	 */
	public void describeStateToPlayer(L2PcInstance player) {
		if (getActor().isVisibleFor(player)) {
			if (_clientMoving) {
				if ((_clientMovingToPawnOffset != 0) && (_followTarget != null)) {
					// Send a Server->Client packet MoveToPawn to the actor and all L2PcInstance in its _knownPlayers
					player.sendPacket(new MoveToPawn(_actor, _followTarget, _clientMovingToPawnOffset));
				} else {
					// Send a Server->Client packet CharMoveToLocation to the actor and all L2PcInstance in its _knownPlayers
					player.sendPacket(new MoveToLocation(_actor));
				}
			}
		}
	}
	
	public L2Character getActor() {
		return _actor;
	}
	
	/**
	 * Create and Launch an AI Follow Task to execute every 0.5s, following at specified range.
	 * @param target The L2Character to follow
	 * @param range
	 */
	public synchronized void startFollow(L2Character target, int range) {
		if (_followTask != null) {
			_followTask.cancel(false);
			_followTask = null;
		}

		_followTarget = target;
		_followTask = ThreadPoolManager.getInstance().scheduleAiAtFixedRate(new FollowTask(range), 5, ATTACK_FOLLOW_INTERVAL);
	}
	
	protected L2Character getFollowTarget() {
		return _followTarget;
	}
	
	protected L2Object getTarget() {
		return _target;
	}
	
	protected void setTarget(L2Object target) {
		_target = target;
	}
	
	/**
	 * Stop all Ai tasks and futures.
	 */
	public void stopAITask() {
		stopFollow();
	}
	
	@Override
	public String toString() {
		return "Actor: " + _actor;
	}
	
	private class FollowTask implements Runnable {
		protected int _range = 70;

		public FollowTask() {
		}

		public FollowTask(int range) {
			_range = range;
		}

		@Override
		public void run() {
			try {
				if (_followTask == null) {
					return;
				}

				L2Character followTarget = _followTarget; // copy to prevent NPE
				if (followTarget == null) {
					if (_actor instanceof L2Summon) {
						((L2Summon) _actor).setFollowStatus(false);
					}
					setIntention(AI_INTENTION_IDLE);
					return;
				}

				if (!_actor.isInsideRadius(followTarget, _range, true, false)) {
					if (!_actor.isInsideRadius(followTarget, 3000, true, false)) {
						// if the target is too far (maybe also teleported)
						if (_actor instanceof L2Summon) {
							((L2Summon) _actor).setFollowStatus(false);
						}

						setIntention(AI_INTENTION_IDLE);
						return;
					}

					moveToPawn(followTarget, _range);
				}
			} catch (Exception e) {
				LOG.warn("{}: There has been a problem running the follow task!", getClass().getSimpleName(), e);
			}
		}
	}
}
