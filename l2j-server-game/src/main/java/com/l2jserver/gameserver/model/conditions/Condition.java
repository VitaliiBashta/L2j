package com.l2jserver.gameserver.model.conditions;

import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.items.L2Item;
import com.l2jserver.gameserver.model.skills.Skill;

/** The Class Condition. */
public abstract class Condition implements ConditionListener {
  private ConditionListener listener;
  private String msg;
  private int msgId;
  private boolean addName = false;
  private boolean result;

  /** Gets the message. */
  public final String getMessage() {
    return msg;
  }

  /** Sets the message. */
  public final void setMessage(String msg) {
    this.msg = msg;
  }

  /** Gets the message id. */
  public final int getMessageId() {
    return msgId;
  }

  /** Sets the message id. */
  public final void setMessageId(int msgId) {
    this.msgId = msgId;
  }

  /** Adds the name. */
  public final void addName() {
    addName = true;
  }

  /** Checks if is adds the name. */
  public final boolean isAddName() {
    return addName;
  }

  /** Gets the listener. */
  final ConditionListener getListener() {
    return listener;
  }

  /** Sets the listener. */
  void setListener(ConditionListener listener) {
    this.listener = listener;
    notifyChanged();
  }

  public final boolean test(L2Character caster, L2Character target, Skill skill) {
    return test(caster, target, skill, null);
  }

  public final boolean test(L2Character caster, L2Character target, Skill skill, L2Item item) {
    boolean res = testImpl(caster, target, skill, item);
    if ((listener != null) && (res != result)) {
      result = res;
      notifyChanged();
    }
    return res;
  }

  /** Test the condition. */
  public abstract boolean testImpl(
      L2Character effector, L2Character effected, Skill skill, L2Item item);

  @Override
  public final void notifyChanged() {
    if (listener != null) {
      listener.notifyChanged();
    }
  }
}
