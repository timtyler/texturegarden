package com.texturegarden.effects.particle;

import com.texturegarden.arrays.ComplexArray2D;
import com.texturegarden.arrays.SimpleArray2D;

public class Particle {
  int x;
  int y;
  int dx;
  int dy;
  int sx;
  int sy;
  int intensity;
  int max;
  int oscillator;

  public Particle(int x, int y, int dx, int dy, int sx, int sy, int intensity, int max) {
    this.x = x;
    this.y = y;
    this.dx = dx;
    this.dy = dy;
    this.sx = sx;
    this.sy = sy;
    this.intensity = intensity;
    this.max = max;
  }

  public void animate(ComplexArray2D c_a) {
    x += dx;
    y += dy;

    oscillator++;
    //int v = ((oscillator & (max - 1)) > (max >> 1)) ? -1 : 0;
    if (max > 0) {
      int v = intensity + (int) (intensity * Math.cos(2 * Math.PI * oscillator / (float) max));
      if (sx == sy) {
        c_a.r.fillCircle(x, y, sx, sy, v);
      } else {
        c_a.r.fillRect(x, y, sx, sy, v | (v << 16));
      }
    } else {
      mirror(c_a, x, y, sx, sy);
    }

    // mirror...
    // c_a.r.fillRect(x, y, sx, sy, v | (v << 16));
  }

  public void mirror(ComplexArray2D c_a, int x, int y, int w, int h) {
    SimpleArray2D car = c_a.r;
    int ax = (x * car.width) >> 16;
    int ay = (y * car.height) >> 16;
    int aw = (w * car.width) >> 16;
    int ah = (h * car.height) >> 16;

    for (int cy = 2; cy < ah - 1; cy++) {
      c_a.r.safeWriteWithOffset(ax + 2, ay + cy, 0);
      c_a.r.safeWriteWithOffset(ax + aw - 2, ay + cy, 0);
    }

    for (int cy = 1; cy < ah; cy++) {
      c_a.r.safeWriteWithOffset(ax + 1, ay + cy, -1);
      c_a.r.safeWriteWithOffset(ax + aw - 1, ay + cy, -1);
    }

    for (int cy = 0; cy < ah; cy++) {
      int v = c_a.r.safeReadWithOffset(ax - 1, ay + cy);
      c_a.r.safeWriteWithOffset(ax, ay + cy, v);
      int v2 = c_a.r.safeReadWithOffset(ax + aw + 1, ay + cy);
      c_a.r.safeWriteWithOffset(ax + aw, ay + cy, v2);
    }

    for (int cx = 2; cx < aw - 1; cx++) {
      c_a.r.safeWriteWithOffset(ax + cx, ay + 2, 0);
      c_a.r.safeWriteWithOffset(ax + cx, ay + ah - 2, 0);
    }
    for (int cx = 1; cx < aw; cx++) {
      c_a.r.safeWriteWithOffset(ax + cx, ay + 1, -1);
      c_a.r.safeWriteWithOffset(ax + cx, ay + ah - 1, -1);
    }
    for (int cx = 0; cx < aw; cx++) {
      int v = c_a.r.safeReadWithOffset(ax + cx, ay - 1);
      c_a.r.safeWriteWithOffset(ax + cx, ay, v);
      int v2 = c_a.r.safeReadWithOffset(ax + cx, ay + ah + 1);
      c_a.r.safeWriteWithOffset(ax + cx, ay + ah, v2);
    }
  }
}
