package com.l2jserver.gameserver.model;

public final class AbsorberInfo {
  private int _objectId;
  private double _absorbedHp;

  public AbsorberInfo(int objectId, double pAbsorbedHp) {
    _objectId = objectId;
    _absorbedHp = pAbsorbedHp;
  }

  public double getAbsorbedHp() {
    return _absorbedHp;
  }

  public void setAbsorbedHp(double absorbedHp) {
    _absorbedHp = absorbedHp;
  }

  @Override
  public int hashCode() {
    return _objectId;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }

    if (obj instanceof AbsorberInfo) {
      return (((AbsorberInfo) obj).getObjectId() == _objectId);
    }

    return false;
  }

  public int getObjectId() {
    return _objectId;
  }

  public void setObjectId(int objectId) {
    _objectId = objectId;
  }
}
