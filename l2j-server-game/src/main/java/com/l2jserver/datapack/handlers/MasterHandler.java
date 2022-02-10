package com.l2jserver.datapack.handlers;

import com.l2jserver.commons.util.Util;
import com.l2jserver.datapack.handlers.actionhandlers.*;
import com.l2jserver.datapack.handlers.actionshifthandlers.*;
import com.l2jserver.datapack.handlers.admincommandhandlers.*;
import com.l2jserver.datapack.handlers.bypasshandlers.*;
import com.l2jserver.datapack.handlers.chathandlers.*;
import com.l2jserver.datapack.handlers.communityboard.*;
import com.l2jserver.datapack.handlers.itemhandlers.*;
import com.l2jserver.datapack.handlers.punishmenthandlers.BanHandler;
import com.l2jserver.datapack.handlers.punishmenthandlers.ChatBanHandler;
import com.l2jserver.datapack.handlers.punishmenthandlers.JailHandler;
import com.l2jserver.datapack.handlers.targethandlers.*;
import com.l2jserver.datapack.handlers.telnethandlers.*;
import com.l2jserver.datapack.handlers.usercommandhandlers.*;
import com.l2jserver.datapack.handlers.voicedcommandhandlers.*;
import com.l2jserver.gameserver.handler.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.l2jserver.gameserver.config.Configuration.general;

public class MasterHandler {
  private static final Logger LOG = LoggerFactory.getLogger(MasterHandler.class);

  private static final List<Class<?>> ACTION_HANDLERS =
      List.of(
          L2ArtefactInstanceAction.class,
          L2DecoyAction.class,
          L2DoorInstanceAction.class,
          L2ItemInstanceAction.class,
          L2NpcAction.class,
          L2PcInstanceAction.class,
          L2PetInstanceAction.class,
          L2StaticObjectInstanceAction.class,
          L2SummonAction.class,
          L2TrapAction.class);

  private static final List<Class<?>> ACTION_SHIFT_HANDLERS =
      List.of(
          L2DoorInstanceActionShift.class,
          L2ItemInstanceActionShift.class,
          L2NpcActionShift.class,
          L2PcInstanceActionShift.class,
          L2StaticObjectInstanceActionShift.class,
          L2SummonActionShift.class);

  private static final List<Class<?>> ADMIN_HANDLERS =
      List.of(
          AdminAnnouncements.class,
          AdminBBS.class,
          AdminChangeAccessLevel.class,
          AdminManor.class,
          AdminMobGroup.class,
          AdminUnblockIp.class,
          AdminZone.class);

  private static final List<Class<?>> BYPASS_HANDLERS =
      List.of(
          Augment.class,
          Buy.class,
          BuyShadowItem.class,
          ChatLink.class,
          ClanWarehouse.class,
          EventEngine.class,
          Festival.class,
          Freight.class,
          ItemAuctionLink.class,
          Link.class,
          Loto.class,
          Multisell.class,
          NpcViewMod.class,
          Observation.class,
          OlympiadObservation.class,
          OlympiadManagerLink.class,
          QuestLink.class,
          PlayerHelp.class,
          PrivateWarehouse.class,
          QuestList.class,
          ReceivePremium.class,
          ReleaseAttribute.class,
          RentPet.class,
          Rift.class,
          SkillList.class,
          SupportBlessing.class,
          SupportMagic.class,
          TerritoryStatus.class,
          TutorialClose.class,
          VoiceCommand.class,
          Wear.class);

  private static final List<Class<?>> CHAT_HANDLERS =
      List.of(
          ChatAll.class,
          ChatAlliance.class,
          ChatBattlefield.class,
          ChatClan.class,
          ChatHeroVoice.class,
          ChatParty.class,
          ChatPartyMatchRoom.class,
          ChatPartyRoomAll.class,
          ChatPartyRoomCommander.class,
          ChatPetition.class,
          ChatShout.class,
          ChatTell.class,
          ChatTrade.class);

  private static final List<Class<?>> COMMUNITY_HANDLERS =
      List.of(
          ClanBoard.class,
          FavoriteBoard.class,
          FriendsBoard.class,
          HomeBoard.class,
          HomepageBoard.class,
          MailBoard.class,
          MemoBoard.class,
          RegionBoard.class);

  private static final List<Class<?>> ITEM_HANDLERS =
      List.of(
          BeastSoulShot.class,
          BeastSpiritShot.class,
          BlessedSpiritShot.class,
          Book.class,
          Bypass.class,
          Calculator.class,
          CharmOfCourage.class,
          Disguise.class,
          Elixir.class,
          EnchantAttribute.class,
          EnchantScrolls.class,
          EventItem.class,
          ExtractableItems.class,
          FishShots.class,
          Harvester.class,
          ItemSkillsTemplate.class,
          ItemSkills.class,
          ManaPotion.class,
          Maps.class,
          MercTicket.class,
          NicknameColor.class,
          PetFood.class,
          Recipes.class,
          RollingDice.class,
          Seed.class,
          SevenSignsRecord.class,
          SoulShots.class,
          SpecialXMas.class,
          SpiritShot.class,
          SummonItems.class,
          TeleportBookmark.class);

  private static final List<Class<?>> PUNISHMENT_HANDLERS =
      List.of(BanHandler.class, ChatBanHandler.class, JailHandler.class);

  private static final List<Class<?>> USER_COMMAND_HANDLERS =
      List.of(
          ClanPenalty.class,
          ClanWarsList.class,
          Dismount.class,
          Unstuck.class,
          InstanceZone.class,
          Loc.class,
          Mount.class,
          PartyInfo.class,
          Time.class,
          OlympiadStat.class,
          ChannelLeave.class,
          ChannelDelete.class,
          ChannelInfo.class,
          MyBirthday.class,
          SiegeStatus.class);

  private static final List<Class<?>> TARGET_HANDLERS =
      List.of(
          Area.class,
          AreaCorpseMob.class,
          AreaFriendly.class,
          AreaSummon.class,
          Aura.class,
          AuraCorpseMob.class,
          AuraFriendly.class,
          AuraUndeadEnemy.class,
          BehindArea.class,
          BehindAura.class,
          Clan.class,
          ClanMember.class,
          CommandChannel.class,
          CorpseClan.class,
          CorpseMob.class,
          Enemy.class,
          EnemyNot.class,
          EnemyOnly.class,
          EnemySummon.class,
          FlagPole.class,
          FrontArea.class,
          FrontAura.class,
          Ground.class,
          Holy.class,
          One.class,
          OwnerPet.class,
          Party.class,
          PartyClan.class,
          PartyMember.class,
          PartyNotMe.class,
          PartyOther.class,
          PcBody.class,
          Pet.class,
          Self.class,
          Servitor.class,
          Summon.class,
          Target.class,
          TargetParty.class,
          Unlockable.class);

  private static final List<Class<?>> TELNET_HANDLERS =
      List.of(
          ChatsHandler.class,
          DebugHandler.class,
          HelpHandler.class,
          PlayerHandler.class,
          ServerHandler.class,
          StatusHandler.class,
          ThreadHandler.class);

  private static final List<Class<?>> VOICED_COMMAND_HANDLERS =
      List.of(
          AutoLoot.class,
          StatsVCmd.class,
          // TODO: Add configuration options for this voiced commands:
          // CastleVCmd.class,
          // SetVCmd.class,
          Wedding.class,
          Banking.class,
          ChatAdmin.class,
          Lang.class,
          Debug.class,
          ChangePassword.class);

  // private static final Class<?>[] CUSTOM_HANDLERS =
  // {
  // CustomAnnouncePkPvP.class
  // };

  public void init() {
    if (general().noHandlers()) {
      LOG.info("Handlers disabled...");
      return;
    }

    loadHandlers(VoicedCommandHandler.getInstance(), VOICED_COMMAND_HANDLERS);
    loadHandlers(ActionHandler.getInstance(), ACTION_HANDLERS);
    loadHandlers(ActionShiftHandler.getInstance(), ACTION_SHIFT_HANDLERS);
    loadHandlers(AdminCommandHandler.getInstance(), ADMIN_HANDLERS);
    loadHandlers(BypassHandler.getInstance(), BYPASS_HANDLERS);
    loadHandlers(ChatHandler.getInstance(), CHAT_HANDLERS);
    loadHandlers(CommunityBoardHandler.getInstance(), COMMUNITY_HANDLERS);
    loadHandlers(ItemHandler.getInstance(), ITEM_HANDLERS);
    loadHandlers(PunishmentHandler.getInstance(), PUNISHMENT_HANDLERS);
    loadHandlers(UserCommandHandler.getInstance(), USER_COMMAND_HANDLERS);
    loadHandlers(TargetHandler.getInstance(), TARGET_HANDLERS);
    loadHandlers(TelnetHandler.getInstance(), TELNET_HANDLERS);
  }

  private void loadHandlers(IHandler<?, ?> handler, List<Class<?>> classes) {
    for (Class<?> c : classes) {

      try {
        handler.registerByClass(c);
      } catch (Exception ex) {
        LOG.error("Failed loading handler {}!", c.getSimpleName(), ex);
      }
    }
    String handlerName = Util.splitWords(handler.getClass().getSimpleName());
    LOG.info("Loaded {} {}.", handler.size(), handlerName);
  }
}
