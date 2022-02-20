package com.l2jserver.gameserver.ai;

import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.L2Vehicle;
import com.l2jserver.gameserver.model.skills.Skill;

public abstract class L2VehicleAI extends L2CharacterAI {

  protected L2VehicleAI(L2Vehicle creature) {
		super(creature);
	}
	
	@Override
	protected void onIntentionAttack(L2Character target) {
	}
	
	@Override
	protected void onIntentionCast(Skill skill, L2Object target) {
	}
	
	@Override
	protected void onIntentionFollow(L2Character target) {
	}
	
	@Override
	protected void onIntentionPickUp(L2Object item) {
	}
	
	@Override
	protected void onIntentionInteract(L2Object object) {
	}
	
	@Override
	protected void onEvtAttacked(L2Character attacker) {
	}
	
	@Override
	protected void onEvtAggression(L2Character target, long aggro) {
	}
	
	@Override
	protected void onEvtStunned(L2Character attacker) {
	}
	
	@Override
	protected void onEvtSleeping(L2Character attacker) {
	}
	
	@Override
	protected void onEvtRooted(L2Character attacker) {
	}
	
	@Override
	protected void onEvtForgetObject(L2Object object) {
	}
	
	@Override
	protected void onEvtCancel() {
	}
	
	@Override
	protected void onEvtDead() {
	}
	
	@Override
	protected void onEvtFakeDeath() {
	}
	
	@Override
	protected void onEvtFinishCasting() {
	}
	
	@Override
	protected void clientActionFailed() {
	}
	
	@Override
	protected void moveToPawn(L2Object pawn, int offset) {
	}
	
	@Override
	protected void clientStoppedMoving() {
	}
}