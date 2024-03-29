package com.l2jserver.gameserver.model.effects;

import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.skills.BuffInfo;
import com.l2jserver.gameserver.model.skills.Skill;
import com.l2jserver.gameserver.model.stats.functions.AbstractFunction;
import com.l2jserver.gameserver.model.stats.functions.FuncTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import static com.l2jserver.gameserver.config.Configuration.character;

/**
 * Abstract effect implementation.<br>
 * Instant effects should not override {@link #onExit(BuffInfo)}.<br>
 * Instant effects should not override {@link #canStart(BuffInfo)}, all checks should be done {@link
 * #onStart(BuffInfo)}.<br>
 * Do not call super class methods {@link #onStart(BuffInfo)} nor {@link #onExit(BuffInfo)}.
 */
public abstract class AbstractEffect {
  protected static final Logger _log = Logger.getLogger(AbstractEffect.class.getName());

  // Conditions
  /** Attach condition. */
  private final Condition attachCond;
  /** Effect name. */
  private final String name;

  private List<FuncTemplate> funcTemplates;
  private int ticks;

  /** Abstract effect constructor. */
  protected AbstractEffect(
      Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
    this.attachCond = attachCond;
    name = set.getString("name");
  }

  /** Tests the attach condition. */
  public boolean testConditions(L2Character caster, L2Character target, Skill skill) {
    return (attachCond == null) || attachCond.test(caster, target, skill);
  }

  /** Attaches a function template. */
  public void attach(FuncTemplate f) {
    if (funcTemplates == null) {
      funcTemplates = new ArrayList<>(1);
    }
    funcTemplates.add(f);
  }

  /** Gets the effect name. */
  public String getName() {
    return name;
  }

  /** Gets the effect ticks */
  public int getTicks() {
    return ticks;
  }

  /** Sets the effect ticks */
  protected void setTicks(int ticks) {
    this.ticks = ticks;
  }

  public double getTicksMultiplier() {
    return (getTicks() * character().getEffectTickRatio()) / 1000f;
  }

  public List<FuncTemplate> getFuncTemplates() {
    return funcTemplates;
  }

  /**
   * Calculates whether this effects land or not.<br>
   * If it lands will be scheduled and added to the character effect list.<br>
   * Override in effect implementation to change behavior. <br>
   * <b>Warning:</b> Must be used only for instant effects continuous effects will not call this
   * they have their success handled by activate_rate.
   *
   * @param info the buff info
   * @return {@code true} if this effect land, {@code false} otherwise
   */
  public boolean calcSuccess(BuffInfo info) {
    return true;
  }

  /**
   * Get this effect's type.<br>
   * TODO: Remove.
   *
   * @return the effect type
   */
  public L2EffectType getEffectType() {
    return L2EffectType.NONE;
  }

  /**
   * Verify if the buff can start.<br>
   * Used for continuous effects.
   *
   * @param info the buff info
   * @return {@code true} if all the start conditions are meet, {@code false} otherwise
   */
  public boolean canStart(BuffInfo info) {
    return true;
  }

  /**
   * Called on effect start.
   *
   * @param info the buff info
   */
  public void onStart(BuffInfo info) {}

  /**
   * Called on each tick.<br>
   * If the abnormal time is lesser than zero it will last forever.
   *
   * @param info the buff info
   * @return if {@code true} this effect will continue forever, if {@code false} it will stop after
   *     abnormal time has passed
   */
  public boolean onActionTime(BuffInfo info) {
    return false;
  }

  /**
   * Called when the effect is exited.
   *
   * @param info the buff info
   */
  public void onExit(BuffInfo info) {}

  /**
   * Get this effect's stats functions.
   *
   * @param caster the caster
   * @param target the target
   * @param skill the skill
   * @return a list of stat functions.
   */
  public List<AbstractFunction> getStatFuncs(L2Character caster, L2Character target, Skill skill) {
    if (getFuncTemplates() == null) {
      return Collections.emptyList();
    }

    final List<AbstractFunction> functions = new ArrayList<>(getFuncTemplates().size());
    for (FuncTemplate functionTemplate : getFuncTemplates()) {
      final AbstractFunction function = functionTemplate.getFunc(caster, target, skill, this);
      if (function != null) {
        functions.add(function);
      }
    }
    return functions;
  }

  /** Get the effect flags. */
  public int getEffectFlags() {
    return EffectFlag.NONE.getMask();
  }

  @Override
  public String toString() {
    return "Effect " + name;
  }

  public void decreaseForce() {}

  public void increaseEffect() {}

  public boolean checkCondition(Object obj) {
    return true;
  }

  /** Verify if this effect is an instant effect. */
  public boolean isInstant() {
    return false;
  }
}
