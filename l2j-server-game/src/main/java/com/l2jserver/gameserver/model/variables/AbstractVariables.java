package com.l2jserver.gameserver.model.variables;

import com.l2jserver.gameserver.model.StatsSet;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractVariables extends StatsSet {
  private final AtomicBoolean hasChanges = new AtomicBoolean(false);

  /**
   * Overriding following methods to prevent from doing useless database operations if there is no
   * changes since player's login.
   */
  @Override
  public final void set(String name, boolean value) {
    hasChanges.compareAndSet(false, true);
    super.set(name, value);
  }

  @Override
  public final void set(String name, long value) {
    hasChanges.compareAndSet(false, true);
    super.set(name, value);
  }

  @Override
  public final void set(String name, double value) {
    hasChanges.compareAndSet(false, true);
    super.set(name, value);
  }

  @Override
  public final void set(String name, String value) {
    hasChanges.compareAndSet(false, true);
    super.set(name, value);
  }

  @Override
  public final void set(String name, Enum<?> value) {
    hasChanges.compareAndSet(false, true);
    super.set(name, value);
  }

  @Override
  public final void set(String name, int value) {
    hasChanges.compareAndSet(false, true);
    super.set(name, value);
  }

  /**
   * Return true if there exists a record for the variable name.
   *
   * @param name
   * @return
   */
  public boolean hasVariable(String name) {
    return getSet().containsKey(name);
  }

  /** @return {@code true} if changes are made since last load/save. */
  public final boolean hasChanges() {
    return hasChanges.get();
  }

  /**
   * Atomically sets the value to the given updated value if the current value {@code ==} the
   * expected value.
   *
   * @param expect
   * @param update
   * @return {@code true} if successful. {@code false} return indicates that the actual value was
   *     not equal to the expected value.
   */
  public final boolean compareAndSetChanges(boolean expect, boolean update) {
    return hasChanges.compareAndSet(expect, update);
  }

  /**
   * Removes variable
   *
   * @param name
   */
  public final void remove(String name) {
    hasChanges.compareAndSet(false, true);
    getSet().remove(name);
  }
}
