package com.l2jserver.gameserver.handler;

import com.l2jserver.datapack.handlers.effecthandlers.consume.*;
import com.l2jserver.datapack.handlers.effecthandlers.custom.*;
import com.l2jserver.datapack.handlers.effecthandlers.instant.*;
import com.l2jserver.datapack.handlers.effecthandlers.pump.*;
import com.l2jserver.datapack.handlers.effecthandlers.ticks.TickHp;
import com.l2jserver.datapack.handlers.effecthandlers.ticks.TickHpFatal;
import com.l2jserver.datapack.handlers.effecthandlers.ticks.TickMp;
import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EffectHandler {
  private static final Logger LOG = LoggerFactory.getLogger(EffectHandler.class);
  private static final List<Class<? extends AbstractEffect>> EFFECTS =
      List.of(
          AddHate.class,
          AttackTrait.class,
          Backstab.class,
          Betray.class,
          Blink.class,
          BlockAction.class,
          BlockBuff.class,
          BlockBuffSlot.class,
          BlockChat.class,
          BlockDamage.class,
          BlockDebuff.class,
          BlockParty.class,
          BlockResurrection.class,
          Bluff.class,
          Buff.class,
          CallParty.class,
          CallPc.class,
          CallSkill.class,
          ChangeFace.class,
          ChangeFishingMastery.class,
          ChangeHairColor.class,
          ChangeHairStyle.class,
          ClanGate.class,
          Confuse.class,
          ConsumeAgathionEnergy.class,
          ConsumeBody.class,
          ConsumeChameleonRest.class,
          ConsumeFakeDeath.class,
          ConsumeHp.class,
          ConsumeMp.class,
          ConsumeMpByLevel.class,
          ConsumeRest.class,
          ConvertItem.class,
          Cp.class,
          CrystalGradeModify.class,
          CubicMastery.class,
          DeathLink.class,
          Debuff.class,
          DefenceTrait.class,
          DeleteHate.class,
          DeleteHateOfMe.class,
          DetectHiddenObjects.class,
          Detection.class,
          Disarm.class,
          DispelAll.class,
          DispelByCategory.class,
          DispelBySlot.class,
          DispelBySlotProbability.class,
          EnableCloak.class,
          EnergyAttack.class,
          Escape.class,
          FatalBlow.class,
          Fear.class,
          Fishing.class,
          Flag.class,
          FlySelf.class,
          FocusEnergy.class,
          FocusMaxEnergy.class,
          FocusSouls.class,
          FoodForPet.class,
          GetAgro.class,
          GiveRecommendation.class,
          GiveSp.class,
          Grow.class,
          Harvesting.class,
          HeadquarterCreate.class,
          Heal.class,
          Hide.class,
          Hp.class,
          HpByLevel.class,
          HpDrain.class,
          HpPerMax.class,
          ImmobileBuff.class,
          ImmobilePetBuff.class,
          InstantAgathionEnergy.class,
          InstantBetray.class,
          InstantDespawn.class,
          InstantDispelByName.class,
          Lethal.class,
          Lucky.class,
          MagicalAttack.class,
          MagicalAttackByAbnormal.class,
          MagicalAttackMp.class,
          MagicalAttackRange.class,
          MagicalSoulAttack.class,
          ManaHealByLevel.class,
          MaxCp.class,
          MaxHp.class,
          MaxMp.class,
          Mp.class,
          MpPerMax.class,
          Mute.class,
          NoblesseBless.class,
          OpenChest.class,
          OpenCommonRecipeBook.class,
          OpenDoor.class,
          OpenDwarfRecipeBook.class,
          OutpostCreate.class,
          OutpostDestroy.class,
          Paralyze.class,
          Passive.class,
          PhysicalAttack.class,
          PhysicalAttackHpLink.class,
          PhysicalAttackMute.class,
          PhysicalMute.class,
          PhysicalSoulAttack.class,
          ProtectionBlessing.class,
          Pumping.class,
          RandomizeHate.class,
          RebalanceHP.class,
          Recovery.class,
          Reeling.class,
          RefuelAirship.class,
          ResistSkill.class,
          Restoration.class,
          RestorationRandom.class,
          Resurrection.class,
          ResurrectionSpecial.class,
          Root.class,
          RunAway.class,
          ServitorShare.class,
          SetSkill.class,
          SilentMove.class,
          SingleTarget.class,
          SkillTurning.class,
          Sleep.class,
          SoulBlow.class,
          SoulEating.class,
          Sow.class,
          Spoil.class,
          StaticDamage.class,
          StealAbnormal.class,
          Stun.class,
          Summon.class,
          SummonAgathion.class,
          SummonCubic.class,
          SummonNpc.class,
          SummonPet.class,
          SummonTrap.class,
          Sweeper.class,
          TakeCastle.class,
          TakeFort.class,
          TakeFortStart.class,
          TakeTerritoryFlag.class,
          TalismanSlot.class,
          TargetCancel.class,
          TargetMe.class,
          TargetMeProbability.class,
          Teleport.class,
          TeleportToTarget.class,
          ThrowUp.class,
          TickHp.class,
          TickHpFatal.class,
          TickMp.class,
          TransferDamage.class,
          TransferHate.class,
          TransformHangover.class,
          Transformation.class,
          TrapDetect.class,
          TrapRemove.class,
          TriggerSkillByAttack.class,
          TriggerSkillByAvoid.class,
          TriggerSkillByDamage.class,
          TriggerSkillBySkill.class,
          Unsummon.class,
          UnsummonAgathion.class,
          VitalityPointUp.class);
  private final Map<String, Constructor<? extends AbstractEffect>> handlers;

  protected EffectHandler() {
    handlers =
        EFFECTS.stream().collect(Collectors.toMap(Class::getSimpleName, this::getConstructor));
    LOG.info("Registered {} effects", handlers.size());
  }

  /** Creates an effect given the parameters. */
  public AbstractEffect createEffect(
      Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
    final String name = set.getString("name");

    var handlerConstructor = handlers.get(name);

    return instantiateEffect(attachCond, applyCond, set, params, name, handlerConstructor);
  }

  private AbstractEffect instantiateEffect(
      Condition attachCond,
      Condition applyCond,
      StatsSet set,
      StatsSet params,
      String name,
      Constructor<? extends AbstractEffect> handlerConstructor) {
    int skillId = set.getInt("id");
    try {
      return handlerConstructor.newInstance(attachCond, applyCond, set, params);
    } catch (InstantiationException
        | IllegalAccessException
        | IllegalArgumentException
        | InvocationTargetException e) {
      throw new IllegalArgumentException(
          "Unable to initialize effect handler: "
              + name
              + " in skill["
              + skillId
              + "] : "
              + e.getMessage());
    }
  }

  private Constructor<? extends AbstractEffect> getConstructor(
      Class<? extends AbstractEffect> handler) {
    try {
      return handler.getConstructor(
          Condition.class, Condition.class, StatsSet.class, StatsSet.class);
    } catch (NoSuchMethodException | SecurityException e) {
      throw new IllegalArgumentException(
          " Requested unexistent constructor for effect handler: " + handler.getSimpleName(), e);
    }
  }
}
