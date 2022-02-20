package com.l2jserver.gameserver.model.holders;

public class UniqueItemHolder extends ItemHolder {
  private final int objectId;

  public UniqueItemHolder(int id, int objectId) {
    this(id, objectId, 1);
  }

  public UniqueItemHolder(int id, int objectId, long count) {
    super(id, count);
    this.objectId = objectId;
  }

  public int getObjectId() {
    return objectId;
  }

  @Override
  public String toString() {
    return "["
            + getClass().getSimpleName()
            + "] ID: "
            + getId()
            + ", object ID: "
            + objectId
            + ", count: "
            + getCount();
  }
}
