package com.l2jserver.gameserver.util;

public final class LinePointIterator {
  private final int dstX;
  private final int dstY;
  private final long dx;
  private final long dy;
  private final long sx;
  private final long sy;
  // src is moved towards dst in next()
  private int srcX;
  private int srcY;
  private long error;

  private boolean first;

  public LinePointIterator(int srcX, int srcY, int dstX, int dstY) {
    this.srcX = srcX;
    this.srcY = srcY;
    this.dstX = dstX;
    this.dstY = dstY;
    dx = Math.abs((long) dstX - srcX);
    dy = Math.abs((long) dstY - srcY);
    sx = srcX < dstX ? 1 : -1;
    sy = srcY < dstY ? 1 : -1;

    if (dx >= dy) {
      error = dx / 2;
    } else {
      error = dy / 2;
    }

    first = true;
  }

  public boolean next() {
    if (first) {
      first = false;
      return true;
    } else if (dx >= dy) {
      if (srcX != dstX) {
        srcX += sx;

        error += dy;
        if (error >= dx) {
          srcY += sy;
          error -= dx;
        }

        return true;
      }
    } else {
      if (srcY != dstY) {
        srcY += sy;

        error += dx;
        if (error >= dy) {
          srcX += sx;
          error -= dy;
        }

        return true;
      }
    }

    return false;
  }

  public int x() {
    return srcX;
  }

  public int y() {
    return srcY;
  }
}
