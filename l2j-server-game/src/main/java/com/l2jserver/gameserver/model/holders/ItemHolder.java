package com.l2jserver.gameserver.model.holders;

import com.l2jserver.gameserver.model.interfaces.Identifiable;

public class ItemHolder implements Identifiable {
  private final int id;
  private final long count;

  public ItemHolder(int id, long count) {
    this.id = id;
    this.count = count;
  }

  @Override
  public int getId() {
    return id;
  }

  public long getCount() {
    return count;
  }

  @Override
  public String toString() {
    return "[" + getClass().getSimpleName() + "] ID: " + id + ", count: " + count;
  }
}
