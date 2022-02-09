package com.l2jserver.gameserver.util;

public final class LinePointIterator3D {
  private final int dstX;
  private final int dstY;
  private final int dstZ;
  private final long dx;
  private final long dy;
  private final long dz;
  private final long sx;
  private final long sy;
  private final long sz;
  private int srcX;
  private int srcY;
  private int srcZ;
  private long error;
  private long error2;
  private boolean first;

  public LinePointIterator3D(int srcX, int srcY, int srcZ, int dstX, int dstY, int dstZ) {
    this.srcX = srcX;
    this.srcY = srcY;
    this.srcZ = srcZ;
    this.dstX = dstX;
    this.dstY = dstY;
    this.dstZ = dstZ;
    dx = Math.abs((long) dstX - srcX);
    dy = Math.abs((long) dstY - srcY);
    dz = Math.abs((long) dstZ - srcZ);
    sx = srcX < dstX ? 1 : -1;
    sy = srcY < dstY ? 1 : -1;
    sz = srcZ < dstZ ? 1 : -1;

    if ((dx >= dy) && (dx >= dz)) {
      error = error2 = dx / 2;
    } else if ((dy >= dx) && (dy >= dz)) {
      error = error2 = dy / 2;
    } else {
      error = error2 = dz / 2;
    }

    first = true;
  }

  public boolean next() {
    if (first) {
      first = false;
      return true;
    }
    if ((dx >= dy) && (dx >= dz)) {
      if (srcX != dstX) {
        return notSameX();
      }
    } else if ((dy >= dx) && (dy >= dz)) {
      if (srcY != dstY) {
        return notSameY();
      }
    } else if (srcZ != dstZ) {
      srcZ += sz;

      error += dx;
      if (error >= dz) {
        srcX += sx;
        error -= dz;
      }

      error2 += dy;
      if (error2 >= dz) {
        srcY += sy;
        error2 -= dz;
      }

      return true;
    }

    return false;
  }

  private boolean notSameY() {
    srcY += sy;

    error += dx;
    if (error >= dy) {
      srcX += sx;
      error -= dy;
    }

    error2 += dz;
    if (error2 >= dy) {
      srcZ += sz;
      error2 -= dy;
    }

    return true;
  }

  private boolean notSameX() {
    srcX += sx;

    error += dy;
    if (error >= dx) {
      srcY += sy;
      error -= dx;
    }

    error2 += dz;
    if (error2 >= dx) {
      srcZ += sz;
      error2 -= dx;
    }
    return true;
  }

  public int x() {
    return srcX;
  }

  public int y() {
    return srcY;
  }

  public int z() {
    return srcZ;
  }
}
