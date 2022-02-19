package com.texturegarden.effects.ca;

import com.texturegarden.arrays.SimpleArray2D;

/*
 *  to do : presets...
 */

public class FractalLandscape {
  public static SimpleArray2D r; // needs a lock on this...
  public   static void generate(SimpleArray2D _r) {
    r = _r;
    // = multiplier
    r.fill(-1);
    recursivelyGenerate(0, 0, r.width, r.height, 0x8000, 0x8000, 0x8000, 0x8000, 0x8000);
  }

  // can be made to scale perfectly - with location-sensitive RNG...
  public static void recursivelyGenerate(int x1, int y1, int x2, int y2, int h_sw, int h_se, int h_nw, int h_ne, int m) {
    int dx;
    int dy;
    int h_n;
    int h_s;
    int h_e;
    int h_w;
    int h_c;
    int next_m;

    int mo2 = m >> 1;

    int cm = m << 1;
    int cmo2 = cm >> 1;

    dx = x2 - x1;
    if (dx > 1) {
      dy = y2 - y1;
      if (dy > 1) {
        next_m = m >>> 1;
        int mid_x = (x1 + x2) >> 1;
        int mid_y = (y1 + y2) >> 1;

        if ((h_w = r.v[x1][mid_y]) == -1) {
          h_w = ((h_nw + h_sw) >> 1) + r.rnd.nextInt(m) - mo2; // copy if possible...
        }

        if ((h_s = r.v[mid_x][y1]) == -1) {
          h_s = ((h_se + h_sw) >> 1) + r.rnd.nextInt(m) - mo2; // copy if possible...
        }

        h_c = (((h_ne + h_nw + h_se + h_sw)) >>> 2) + r.rnd.nextInt(cm) - cmo2;

        recursivelyGenerate(x1, y1, mid_x, mid_y, h_sw, h_s, h_w, h_c, next_m); // SW

        if (x2 >= (r.width - 1)) {
          h_e = r.v[0][mid_y];
        } else {
          h_e = ((h_ne + h_se) >> 1) + r.rnd.nextInt(m) - mo2; // read if possible...
        }

        recursivelyGenerate(mid_x, y1, x2, mid_y, h_s, h_se, h_c, h_e, next_m); // SE

        if (y2 >= (r.height - 1)) {
          h_n = r.v[mid_x][0];
        } else {
          h_n = ((h_ne + h_nw) >> 1) + r.rnd.nextInt(m) - mo2; // read if possible...
        }

        recursivelyGenerate(x1, mid_y, mid_x, y2, h_w, h_c, h_nw, h_n, next_m); // NW
        recursivelyGenerate(mid_x, mid_y, x2, y2, h_c, h_e, h_n, h_ne, next_m); // NE
      }
    } else {
      if (r.v[x1][y1] == -1) {
        r.v[x1][y1] = h_sw;
      }

      if (x1 < (r.width - 1)) {
        if (r.v[x1 + 1][y1] == -1) {
          r.v[x1 + 1][y1] = h_se;
        }

        if (y1 < (r.height - 1)) {
          if (r.v[x1 + 1][y1 + 1] == -1) {
            r.v[x1 + 1][y1 + 1] = h_ne;
          }
        }
      }

      if (y1 < (r.height - 1)) {
        if (r.v[x1][y1 + 1] == -1) {
          r.v[x1][y1 + 1] = h_nw;
        }
      }
    }
  }
}
