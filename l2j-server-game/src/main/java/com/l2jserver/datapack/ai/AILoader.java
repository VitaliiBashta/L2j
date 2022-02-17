package com.l2jserver.datapack.ai;

import com.l2jserver.datapack.ai.fantasy_isle.HandysBlockCheckerEvent;
import com.l2jserver.datapack.ai.fantasy_isle.MC_Show;
import com.l2jserver.datapack.ai.group_template.*;
import com.l2jserver.datapack.ai.individual.*;
import com.l2jserver.datapack.ai.individual.Antharas.Antharas;
import com.l2jserver.datapack.ai.individual.Baium.Baium;
import com.l2jserver.datapack.ai.npc.Abercrombie.Abercrombie;
import com.l2jserver.datapack.ai.npc.Alarm.Alarm;
import com.l2jserver.datapack.ai.npc.Alexandria.Alexandria;
import com.l2jserver.datapack.ai.npc.AvantGarde.AvantGarde;
import com.l2jserver.datapack.ai.npc.BlackMarketeerOfMammon.BlackMarketeerOfMammon;
import com.l2jserver.datapack.ai.npc.CastleAmbassador.CastleAmbassador;
import com.l2jserver.datapack.ai.npc.CastleBlacksmith.CastleBlacksmith;
import com.l2jserver.datapack.ai.npc.CastleChamberlain.CastleChamberlain;
import com.l2jserver.datapack.ai.npc.CastleCourtMagician.CastleCourtMagician;
import com.l2jserver.datapack.ai.npc.CastleMercenaryManager.CastleMercenaryManager;
import com.l2jserver.datapack.ai.npc.CastleTeleporter.CastleTeleporter;
import com.l2jserver.datapack.ai.npc.CastleWarehouse.CastleWarehouse;
import com.l2jserver.datapack.ai.npc.ClanTrader.ClanTrader;
import com.l2jserver.datapack.ai.npc.ClassMaster.ClassMaster;
import com.l2jserver.datapack.ai.npc.Dorian.Dorian;
import com.l2jserver.datapack.ai.npc.DragonVortex.DragonVortex;
import com.l2jserver.datapack.ai.npc.FameManager.FameManager;
import com.l2jserver.datapack.ai.npc.Fisherman.Fisherman;
import com.l2jserver.datapack.ai.npc.ForgeOfTheGods.ForgeOfTheGods;
import com.l2jserver.datapack.ai.npc.FortressArcherCaptain.FortressArcherCaptain;
import com.l2jserver.datapack.ai.npc.FortressSiegeManager.FortressSiegeManager;
import com.l2jserver.datapack.ai.npc.FreyasSteward.FreyasSteward;
import com.l2jserver.datapack.ai.npc.Jinia.Jinia;
import com.l2jserver.datapack.ai.npc.KetraOrcSupport.KetraOrcSupport;
import com.l2jserver.datapack.ai.npc.Kier.Kier;
import com.l2jserver.datapack.ai.npc.ManorManager.ManorManager;
import com.l2jserver.datapack.ai.npc.MercenaryCaptain.MercenaryCaptain;
import com.l2jserver.datapack.ai.npc.Minigame.Minigame;
import com.l2jserver.datapack.ai.npc.MonumentOfHeroes.MonumentOfHeroes;
import com.l2jserver.datapack.ai.npc.NevitsHerald.NevitsHerald;
import com.l2jserver.datapack.ai.npc.NpcBuffers.NpcBuffers;
import com.l2jserver.datapack.ai.npc.Rafforty.Rafforty;
import com.l2jserver.datapack.ai.npc.Rignos.Rignos;
import com.l2jserver.datapack.ai.npc.Selina.Selina;
import com.l2jserver.datapack.ai.npc.Sirra.Sirra;
import com.l2jserver.datapack.ai.npc.SubclassCertification.SubclassCertification;
import com.l2jserver.datapack.ai.npc.Summons.MerchantGolem.GolemTrader;
import com.l2jserver.datapack.ai.npc.Summons.Pets.ImprovedBabyPets;
import com.l2jserver.datapack.ai.npc.Summons.Servitors.Servitors;
import com.l2jserver.datapack.ai.npc.SupportUnitCaptain.SupportUnitCaptain;
import com.l2jserver.datapack.ai.npc.SymbolMaker.SymbolMaker;
import com.l2jserver.datapack.ai.npc.Teleports.Asher.Asher;
import com.l2jserver.datapack.ai.npc.Teleports.CrumaTower.CrumaTower;
import com.l2jserver.datapack.ai.npc.Teleports.DelusionTeleport.DelusionTeleport;
import com.l2jserver.datapack.ai.npc.Teleports.ElrokiTeleporters.ElrokiTeleporters;
import com.l2jserver.datapack.ai.npc.Teleports.GatekeeperSpirit.GatekeeperSpirit;
import com.l2jserver.datapack.ai.npc.Teleports.GhostChamberlainOfElmoreden.GhostChamberlainOfElmoreden;
import com.l2jserver.datapack.ai.npc.Teleports.GrandBossTeleporters.GrandBossTeleporters;
import com.l2jserver.datapack.ai.npc.Teleports.HuntingGroundsTeleport.HuntingGroundsTeleport;
import com.l2jserver.datapack.ai.npc.Teleports.Klemis.Klemis;
import com.l2jserver.datapack.ai.npc.Teleports.MithrilMinesTeleporter.MithrilMinesTeleporter;
import com.l2jserver.datapack.ai.npc.Teleports.NewbieGuide.NewbieGuide;
import com.l2jserver.datapack.ai.npc.Teleports.NoblesseTeleport.NoblesseTeleport;
import com.l2jserver.datapack.ai.npc.Teleports.OracleTeleport.OracleTeleport;
import com.l2jserver.datapack.ai.npc.Teleports.PaganTeleporters.PaganTeleporters;
import com.l2jserver.datapack.ai.npc.Teleports.SeparatedSoul.SeparatedSoul;
import com.l2jserver.datapack.ai.npc.Teleports.StakatoNestTeleporter.StakatoNestTeleporter;
import com.l2jserver.datapack.ai.npc.Teleports.SteelCitadelTeleport.SteelCitadelTeleport;
import com.l2jserver.datapack.ai.npc.Teleports.StrongholdsTeleports.StrongholdsTeleports;
import com.l2jserver.datapack.ai.npc.Teleports.Survivor.Survivor;
import com.l2jserver.datapack.ai.npc.Teleports.TeleportToFantasy.TeleportToFantasy;
import com.l2jserver.datapack.ai.npc.Teleports.TeleportToRaceTrack.TeleportToRaceTrack;
import com.l2jserver.datapack.ai.npc.Teleports.TeleportToUndergroundColiseum.TeleportToUndergroundColiseum;
import com.l2jserver.datapack.ai.npc.Teleports.TeleportWithCharm.TeleportWithCharm;
import com.l2jserver.datapack.ai.npc.Teleports.ToIVortex.ToIVortex;
import com.l2jserver.datapack.ai.npc.TerritoryManagers.TerritoryManagers;
import com.l2jserver.datapack.ai.npc.TownPets.TownPets;
import com.l2jserver.datapack.ai.npc.Trainers.HealerTrainer.HealerTrainer;
import com.l2jserver.datapack.ai.npc.Tunatun.Tunatun;
import com.l2jserver.datapack.ai.npc.VarkaSilenosSupport.VarkaSilenosSupport;
import com.l2jserver.datapack.ai.npc.VillageMasters.FirstClassTransferTalk.FirstClassTransferTalk;
import com.l2jserver.datapack.ai.npc.WeaverOlf.WeaverOlf;
import com.l2jserver.datapack.ai.npc.WyvernManager.WyvernManager;
import com.l2jserver.datapack.village_master.Alliance.Alliance;
import com.l2jserver.datapack.village_master.Clan.Clan;
import com.l2jserver.datapack.village_master.DarkElfChange1.DarkElfChange1;
import com.l2jserver.datapack.village_master.DarkElfChange2.DarkElfChange2;
import com.l2jserver.datapack.village_master.DwarfBlacksmithChange2.DwarfBlacksmithChange2;
import com.l2jserver.datapack.village_master.DwarfWarehouseChange1.DwarfWarehouseChange1;
import com.l2jserver.datapack.village_master.DwarfWarehouseChange2.DwarfWarehouseChange2;
import com.l2jserver.datapack.village_master.ElfHumanFighterChange1.ElfHumanFighterChange1;
import com.l2jserver.datapack.village_master.ElfHumanFighterChange2.ElfHumanFighterChange2;
import com.l2jserver.datapack.village_master.ElfHumanWizardChange2.ElfHumanWizardChange2;
import com.l2jserver.datapack.village_master.KamaelChange1.KamaelChange1;
import com.l2jserver.datapack.village_master.KamaelChange2.KamaelChange2;
import com.l2jserver.datapack.village_master.OrcChange1.OrcChange1;
import com.l2jserver.datapack.village_master.OrcChange2.OrcChange2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AILoader {

  private static final Logger LOG = LoggerFactory.getLogger(AILoader.class);

  private static final Class<?>[] SCRIPTS = {
    // NPC
    Abercrombie.class,
    Alarm.class,
    Alexandria.class,
    AvantGarde.class,
    BlackMarketeerOfMammon.class,
    CastleAmbassador.class,
    CastleBlacksmith.class,
    CastleCourtMagician.class,
    CastleChamberlain.class,
    CastleMercenaryManager.class,
    CastleTeleporter.class,
    CastleWarehouse.class,
    ClanTrader.class,
    ClassMaster.class,
    Dorian.class,
    DragonVortex.class,
    FameManager.class,
    Fisherman.class,
    ForgeOfTheGods.class,
    FortressArcherCaptain.class,
    FortressSiegeManager.class,
    FreyasSteward.class,
    Jinia.class,
    KetraOrcSupport.class,
    Kier.class,
    ManorManager.class,
    MercenaryCaptain.class,
    Minigame.class,
    MonumentOfHeroes.class,
    NevitsHerald.class,
    NpcBuffers.class,
    Rignos.class,
    Rafforty.class,
    Selina.class,
    Sirra.class,
    SubclassCertification.class,
    GolemTrader.class,
    ImprovedBabyPets.class,
    Servitors.class,
    SupportUnitCaptain.class,
    SymbolMaker.class,
    Asher.class,
    CrumaTower.class,
    DelusionTeleport.class,
    ElrokiTeleporters.class,
    GatekeeperSpirit.class,
    GhostChamberlainOfElmoreden.class,
    GrandBossTeleporters.class,
    HuntingGroundsTeleport.class,
    Klemis.class,
    MithrilMinesTeleporter.class,
    NewbieGuide.class,
    NoblesseTeleport.class,
    OracleTeleport.class,
    PaganTeleporters.class,
    SeparatedSoul.class,
    StakatoNestTeleporter.class,
    SteelCitadelTeleport.class,
    StrongholdsTeleports.class,
    Survivor.class,
    TeleportToFantasy.class,
    TeleportToRaceTrack.class,
    TeleportToUndergroundColiseum.class,
    TeleportWithCharm.class,
    ToIVortex.class,
    TerritoryManagers.class,
    TownPets.class,
    HealerTrainer.class,
    Tunatun.class,
    VarkaSilenosSupport.class,
    FirstClassTransferTalk.class,
    WeaverOlf.class,
    WyvernManager.class,
    // Fantasy Isle
    MC_Show.class,
    HandysBlockCheckerEvent.class,
    // Group Template
    AltarsOfSacrifice.class,
    BeastFarm.class,
    DenOfEvil.class,
    DragonValley.class,
    FairyTrees.class,
    FeedableBeasts.class,
    FleeMonsters.class,
    FrozenLabyrinth.class,
    GiantsCave.class,
    HotSprings.class,
    IsleOfPrayer.class,
    LairOfAntharas.class,
    MinionSpawnManager.class,
    MonasteryOfSilence.class,
    NonLethalableNpcs.class,
    NonTalkingNpcs.class,
    PavelArchaic.class,
    PlainsOfDion.class,
    PlainsOfLizardman.class,
    PolymorphingAngel.class,
    PolymorphingOnAttack.class,
    PrimevalIsle.class,
    PrisonGuards.class,
    RaidBossCancel.class,
    RandomSpawn.class,
    RangeGuard.class,
    Remnants.class,
    Sandstorms.class,
    SeeThroughSilentMove.class,
    SelMahumDrill.class,
    SelMahumSquad.class,
    SilentValley.class,
    StakatoNest.class,
    SummonPc.class,
    TreasureChest.class,
    TurekOrcs.class,
    VarkaKetra.class,
    WarriorFishingBlock.class,
    // Individual
    Antharas.class,
    Baium.class,
    Anais.class,
    Ballista.class,
    Beleth.class,
    BloodyBerserker.class,
    BloodyKarik.class,
    BloodyKarinness.class,
    CrimsonHatuOtis.class,
    Core.class,
    DarkWaterDragon.class,
    DivineBeast.class,
    DrakosWarrior.class,
    DustRider.class,
    EmeraldHorn.class,
    Epidos.class,
    FrightenedRagnaOrc.class,
    GiganticGolem.class,
    Gordon.class,
    GraveRobbers.class,
    Knoriks.class,
    MuscleBomber.class,
    Orfen.class,
    RagnaOrcCommander.class,
    RagnaOrcHero.class,
    RagnaOrcSeer.class,
    NecromancerOfTheValley.class,
    ShadowSummoner.class,
    SinEater.class,
    Valakas.class,
    // Village Master
    Clan.class,
    Alliance.class,
    DarkElfChange1.class,
    DarkElfChange2.class,
    DwarfBlacksmithChange2.class,
    DwarfWarehouseChange1.class,
    DwarfWarehouseChange2.class,
    ElfHumanFighterChange1.class,
    ElfHumanFighterChange2.class,
    ElfHumanWizardChange2.class,
    KamaelChange1.class,
    KamaelChange2.class,
    OrcChange1.class,
    OrcChange2.class
  };

  public static void main(String[] args) {
    int n = 0;
    for (var ai : SCRIPTS) {
      try {
        ai.getDeclaredConstructor().newInstance();
        n++;
      } catch (Exception ex) {
        LOG.error("Error loading AI {}!", ai.getSimpleName(), ex);
      }
    }
    LOG.info("Loaded {} AI scripts.", n);
  }
}
