
package com.l2jserver.datapack.handlers.usercommandhandlers;

import com.l2jserver.gameserver.GameTimeController;
import com.l2jserver.gameserver.ThreadPoolManager;
import com.l2jserver.gameserver.handler.IUserCommandHandler;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.entity.TvTEvent;
import com.l2jserver.gameserver.model.holders.SkillHolder;
import com.l2jserver.gameserver.network.serverpackets.MagicSkillUse;
import com.l2jserver.gameserver.network.serverpackets.SetupGauge;
import com.l2jserver.gameserver.util.Broadcast;
import org.springframework.stereotype.Service;

import static com.l2jserver.gameserver.GameTimeController.MILLIS_IN_TICK;
import static com.l2jserver.gameserver.ai.CtrlIntention.AI_INTENTION_IDLE;
import static com.l2jserver.gameserver.config.Configuration.character;
import static com.l2jserver.gameserver.model.TeleportWhereType.TOWN;
import static com.l2jserver.gameserver.network.SystemMessageId.THIS_SKILL_IS_NOT_AVAILABLE_FOR_THE_OLYMPIAD_EVENT;
import static com.l2jserver.gameserver.network.serverpackets.ActionFailed.STATIC_PACKET;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;

@Service
public class Unstuck implements IUserCommandHandler {
	private static final long FIVE_MINUTES = MINUTES.toSeconds(5);
	
	private static final SkillHolder ESCAPE_5_MINUTES = new SkillHolder(2099);
	
	private static final SkillHolder ESCAPE_1_SECOND = new SkillHolder(2100);
	
	private static final int RETURN = 1050;
	
	private static final int[] COMMAND_IDS = {
		52
	};
	
	@Override
	public boolean useUserCommand(int id, L2PcInstance activeChar) {
		if (!TvTEvent.onEscapeUse(activeChar.getObjectId())) {
			activeChar.sendPacket(STATIC_PACKET);
			return false;
		}
		
		if (activeChar.isJailed()) {
			activeChar.sendMessage("You cannot use unstuck while you are in jail.");
			return false;
		}
		
		if (activeChar.isInOlympiadMode()) {
			activeChar.sendPacket(THIS_SKILL_IS_NOT_AVAILABLE_FOR_THE_OLYMPIAD_EVENT);
			return false;
		}
		
		if (activeChar.isCastingNow() || activeChar.isMovementDisabled() || activeChar.isMuted() || //
			activeChar.isAlikeDead() || activeChar.inObserverMode() || activeChar.isCombatFlagEquipped()) {
			return false;
		}
		
		final int unstuckTimer = (activeChar.isGM() ? 1000 : character().getUnstuckInterval());
		activeChar.forceIsCasting(GameTimeController.getInstance().getGameTicks() + (unstuckTimer / MILLIS_IN_TICK));
		
		if (activeChar.isGM()) {
			activeChar.doCast(ESCAPE_1_SECOND);
			return true;
		}
		
		if (character().getUnstuckInterval() == FIVE_MINUTES) {
			activeChar.doCast(ESCAPE_5_MINUTES);
			return true;
		}
		
		if (MILLISECONDS.toSeconds(character().getUnstuckInterval()) > 100) {
			activeChar.sendMessage("You use Escape: " + MILLISECONDS.toMinutes(character().getUnstuckInterval()) + " minutes.");
		} else {
			activeChar.sendMessage("You use Escape: " + MILLISECONDS.toSeconds(character().getUnstuckInterval()) + " seconds.");
		}
		
		activeChar.getAI().setIntention(AI_INTENTION_IDLE);
		// SoE Animation section
		activeChar.setTarget(activeChar);
		activeChar.disableAllSkills();
		Broadcast.toSelfAndKnownPlayersInRadius(activeChar, new MagicSkillUse(activeChar, RETURN, 1, unstuckTimer, 0), 900);
		activeChar.sendPacket(new SetupGauge(0, unstuckTimer));
		// End SoE Animation section
		
		// Continue execution later
		activeChar.setSkillCast(ThreadPoolManager.getInstance().scheduleGeneral(() -> {
			if (!activeChar.isDead()) {
				activeChar.setIsIn7sDungeon(false);
				activeChar.enableAllSkills();
				activeChar.setIsCastingNow(false);
				activeChar.setInstanceId(0);
				activeChar.teleToLocation(TOWN);
			}
		}, unstuckTimer));
		return true;
	}
	
	@Override
	public int[] getUserCommandList() {
		return COMMAND_IDS;
	}
}