package com.l2jserver.datapack.handlers.targethandlers;

import com.l2jserver.gameserver.handler.TargetTypeHandler;
import com.l2jserver.gameserver.instancemanager.DuelManager;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.skills.Skill;
import com.l2jserver.gameserver.model.skills.targets.TargetType;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.l2jserver.gameserver.model.skills.targets.AffectScope.SINGLE;
import static com.l2jserver.gameserver.model.skills.targets.TargetType.ENEMY_ONLY;
import static com.l2jserver.gameserver.model.zone.ZoneId.PVP;
import static com.l2jserver.gameserver.network.SystemMessageId.INCORRECT_TARGET;

@Service
public class EnemyOnly implements TargetTypeHandler {
  @Override
  public List<L2Object> getTargetList(
      Skill skill, L2Character activeChar, boolean onlyFirst, L2Character target) {
    if (skill.getAffectScope() != SINGLE) {
      return EMPTY_TARGET_LIST;
    }

    if (target == null) {
      return EMPTY_TARGET_LIST;
    }

    if (target.getObjectId() == activeChar.getObjectId()) {
      activeChar.sendPacket(INCORRECT_TARGET);
      return EMPTY_TARGET_LIST;
    }

    if (target.isDead()) {
      activeChar.sendPacket(INCORRECT_TARGET);
      return EMPTY_TARGET_LIST;
    }

    if (target.isNpc()) {
      if (target.isAttackable()) {
        return List.of(target);
      }
      activeChar.sendPacket(INCORRECT_TARGET);
      return EMPTY_TARGET_LIST;
    }

    final var player = activeChar.getActingPlayer();
    if (player == null) {
      return EMPTY_TARGET_LIST;
    }

    // In Olympiad, different sides.
    if (player.isInOlympiadMode()) {
      final var targetPlayer = target.getActingPlayer();
      if ((targetPlayer != null) && (player.getOlympiadSide() != targetPlayer.getOlympiadSide())) {
        return List.of(target);
      }
      player.sendPacket(INCORRECT_TARGET);
      return EMPTY_TARGET_LIST;
    }

    // In Duel, different sides.
    if (player.isInDuelWith(target)) {
      final var targetPlayer = target.getActingPlayer();
      final var duel = DuelManager.getInstance().getDuel(player.getDuelId());
      final var teamA = duel.getTeamA();
      final var teamB = duel.getTeamB();
      if (teamA.contains(player) && teamB.contains(targetPlayer)
          || //
          teamB.contains(player) && teamA.contains(targetPlayer)) {
        return List.of(target);
      }
      player.sendPacket(INCORRECT_TARGET);
      return EMPTY_TARGET_LIST;
    }

    // Not in same party.
    if (player.isInPartyWith(target)) {
      player.sendPacket(INCORRECT_TARGET);
      return EMPTY_TARGET_LIST;
    }

    // In PVP Zone.
    if (player.isInsideZone(PVP)) {
      return List.of(target);
    }

    // Not in same clan.
    if (player.isInClanWith(target)) {
      player.sendPacket(INCORRECT_TARGET);
      return EMPTY_TARGET_LIST;
    }

    // TODO(Zoey76): Validate.
    // Not in same alliance.
    if (player.isInAllyWith(target)) {
      player.sendPacket(INCORRECT_TARGET);
      return EMPTY_TARGET_LIST;
    }

    // TODO(Zoey76): Validate.
    // Not in same command channel.
    if (player.isInCommandChannelWith(target)) {
      player.sendPacket(INCORRECT_TARGET);
      return EMPTY_TARGET_LIST;
    }

    // Not on same Siege Side.
    if (player.isOnSameSiegeSideWith(target)) {
      player.sendPacket(INCORRECT_TARGET);
      return EMPTY_TARGET_LIST;
    }

    // At Clan War.
    if (player.isAtWarWith(target)) {
      return List.of(target);
    }

    // Cannot PvP.
    if (!player.checkIfPvP(target)) {
      player.sendPacket(INCORRECT_TARGET);
      return EMPTY_TARGET_LIST;
    }

    return List.of(target);
  }

  @Override
  public Enum<TargetType> getTargetType() {
    return ENEMY_ONLY;
  }
}
