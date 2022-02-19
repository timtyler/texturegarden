package com.texturegarden.effects.ca.waves;

import java.util.Random;

import com.texturegarden.arrays.ComplexArray2D;
import com.texturegarden.arrays.SimpleArray2D;
import com.texturegarden.effects.ca.VNOperation;
import com.texturegarden.utilities.JUR;

/**
 * CAProcess2D - Tim Tyler 2000-2003
 */

public class WaterHelper {
  static JUR rnd = new JUR();

  //Random rnd = new Random();
  // /slightly/ wasteful use of safeRead :-(
  public static void generalVNOperation(ComplexArray2D c_a, VNOperation vno1, VNOperation vno2) {
    int offset_y = (c_a.r.offset_y * c_a.r.height) >>> 16;

    VNOperation use_vn;

    SimpleArray2D _r = c_a.r;
    int y_threshold = _r.height >> 1;
   int ayo;

    int[] tempx0 = new int[_r.height];
    int[] tempy0 = new int[_r.width];
    int[] tempx1 = new int[_r.height];
    int[] tempy1 = new int[_r.width];

    vno1.init();

    // x = 0 case...
    for (int y = 0; y < _r.height; y++) {
      ayo = getAYO(offset_y, y, c_a.r.height);
      use_vn = (ayo < y_threshold) ? vno1 : vno2;
      use_vn.c = _r.safeRead(1, y + 1);
      use_vn.n = _r.safeRead(1, y);
      use_vn.s = _r.safeRead(1, y + 2);
      use_vn.e = _r.safeRead(2, y + 1);
      use_vn.w = _r.safeRead(0, y + 1);

      tempx0[y] = use_vn.process();
    }

    // y = 0 case...
    ayo = getAYO(offset_y, 0, c_a.r.height);
    use_vn = (ayo < y_threshold) ? vno1 : vno2;
    for (int x = 0; x < _r.width; x++) {
      use_vn.c = _r.safeRead(x + 1, 1);
      use_vn.n = _r.safeRead(x + 1, 0);
      use_vn.s = _r.safeRead(x + 1, 2);
      use_vn.e = _r.safeRead(x + 2, 1);
      use_vn.w = _r.safeRead(x, 1);

      tempy0[x] = use_vn.process();
    }

    // x = 1 case...
    for (int y = 0; y < _r.height; y++) {
      ayo = getAYO(offset_y, y, c_a.r.height);
      use_vn = (ayo < y_threshold) ? vno1 : vno2;
      use_vn.c = _r.safeRead(2, y + 1);
      use_vn.n = _r.safeRead(2, y);
      use_vn.s = _r.safeRead(2, y + 2);
      use_vn.e = _r.safeRead(3, y + 1);
      use_vn.w = _r.safeRead(1, y + 1);

      tempx1[y] = use_vn.process();
    }

    // y = 1 case...
    ayo = getAYO(offset_y, 1, c_a.r.height);
    use_vn = (ayo < y_threshold) ? vno1 : vno2;
    for (int x = 0; x < _r.width; x++) {
      use_vn.c = _r.safeRead(x + 1, 2);
      use_vn.n = _r.safeRead(x + 1, 1);
      use_vn.s = _r.safeRead(x + 1, 3);
      use_vn.e = _r.safeRead(x + 2, 2);
      use_vn.w = _r.safeRead(x, 2);

      tempy1[x] = use_vn.process();
    }

    int x_max = _r.width - 2;
    int y_max = _r.height - 2;

    // 4 x points at SE corner...
    vno1.c = _r.safeRead(x_max + 1, y_max + 1);
    vno1.n = _r.safeRead(x_max + 1, y_max);
    vno1.s = _r.safeRead(x_max + 1, y_max + 2);
    vno1.e = _r.safeRead(x_max + 2, y_max + 1);
    vno1.w = _r.safeRead(x_max, y_max + 1);
    int temp_00 = vno1.process();
    x_max++;
    vno1.c = _r.safeRead(x_max + 1, y_max + 1);
    vno1.n = _r.safeRead(x_max + 1, y_max);
    vno1.s = _r.safeRead(x_max + 1, y_max + 2);
    vno1.e = _r.safeRead(x_max + 2, y_max + 1);
    vno1.w = _r.safeRead(x_max, y_max + 1);
    int temp_10 = vno1.process();
    y_max++;
    vno1.c = _r.safeRead(x_max + 1, y_max + 1);
    vno1.n = _r.safeRead(x_max + 1, y_max);
    vno1.s = _r.safeRead(x_max + 1, y_max + 2);
    vno1.e = _r.safeRead(x_max + 2, y_max + 1);
    vno1.w = _r.safeRead(x_max, y_max + 1);
    int temp_11 = vno1.process();
    x_max--;
    vno1.c = _r.safeRead(x_max + 1, y_max + 1);
    vno1.n = _r.safeRead(x_max + 1, y_max);
    vno1.s = _r.safeRead(x_max + 1, y_max + 2);
    vno1.e = _r.safeRead(x_max + 2, y_max + 1);
    vno1.w = _r.safeRead(x_max, y_max + 1);
    int temp_01 = vno1.process();
    y_max--;

    for (int y = 2; y < y_max; y++) {
      ayo = getAYO(offset_y, y, c_a.r.height);

      use_vn = (ayo < (y_max >> 1)) ? vno1 : vno2;
      use_vn.c = _r.v[2 + 1][y + 1];
      use_vn.n = _r.v[2 + 1][y];
      use_vn.s = _r.v[2 + 1][y + 2];
      use_vn.e = _r.v[2 + 2][y + 1];
      use_vn.w = _r.v[2][y + 1];
      _r.v[2][y] = use_vn.process();

      for (int x = 3; x < x_max; x++) {
        use_vn.w = use_vn.c;
        use_vn.c = use_vn.e;
        use_vn.n = _r.v[x + 1][y];
        use_vn.s = _r.v[x + 1][y + 2];
        use_vn.e = _r.v[x + 2][y + 1];
        _r.v[x][y] = use_vn.process();
      }
    }

    // 4 x ends...
    for (int y = 2; y < _r.height - 2; y++) {
      ayo = getAYO(offset_y, y, c_a.r.height);

      use_vn = (ayo < (y_max >> 1)) ? vno1 : vno2;
      use_vn.c = _r.safeRead(x_max + 1, y + 1);
      use_vn.n = _r.safeRead(x_max + 1, y);
      use_vn.s = _r.safeRead(x_max + 1, y + 2);
      use_vn.e = _r.safeRead(x_max + 2, y + 1);
      use_vn.w = _r.safeRead(x_max, y + 1);

      _r.v[x_max][y] = use_vn.process();
    }

    x_max++;
    for (int y = 2; y < _r.height - 2; y++) {
      ayo = getAYO(offset_y, y, c_a.r.height);
      use_vn = (ayo < (y_max >> 1)) ? vno1 : vno2;
      use_vn.c = _r.safeRead(x_max + 1, y + 1);
      use_vn.n = _r.safeRead(x_max + 1, y);
      use_vn.s = _r.safeRead(x_max + 1, y + 2);
      use_vn.e = _r.safeRead(x_max + 2, y + 1);
      use_vn.w = _r.safeRead(x_max, y + 1);

      _r.v[x_max][y] = use_vn.process();
    }

    ayo = getAYO(offset_y, y_max, c_a.r.height);
    use_vn = (ayo < (y_max >> 1)) ? vno1 : vno2;
    for (int x = 2; x < _r.width - 2; x++) {
      use_vn.c = _r.safeRead(x + 1, y_max + 1);
      use_vn.n = _r.safeRead(x + 1, y_max);
      use_vn.s = _r.safeRead(x + 1, y_max + 2);
      use_vn.e = _r.safeRead(x + 2, y_max + 1);
      use_vn.w = _r.safeRead(x, y_max + 1);

      _r.v[x][y_max] = use_vn.process();
    }

    y_max++;
    ayo = getAYO(offset_y, y_max, c_a.r.height);
    use_vn = (ayo < (y_max >> 1)) ? vno1 : vno2;
    for (int x = 2; x < _r.width - 2; x++) {
      use_vn.c = _r.safeRead(x + 1, y_max + 1);
      use_vn.n = _r.safeRead(x + 1, y_max);
      use_vn.s = _r.safeRead(x + 1, y_max + 2);
      use_vn.e = _r.safeRead(x + 2, y_max + 1);
      use_vn.w = _r.safeRead(x, y_max + 1);

      _r.v[x][y_max] = use_vn.process();
    }

    for (int x = 0; x < _r.width; x++) {
      _r.v[x][0] = tempy0[x];
    }

    for (int x = 0; x < _r.width; x++) {
      _r.v[x][1] = tempy1[x];
    }

    for (int y = 2; y < _r.height; y++) {
      _r.v[0][y] = tempx0[y];
    }

    for (int y = 2; y < _r.height; y++) {
      _r.v[1][y] = tempx1[y];
    }

    x_max--;
    y_max--;

    _r.v[x_max][y_max] = temp_00;
    _r.v[x_max + 1][y_max] = temp_10;
    _r.v[x_max][y_max + 1] = temp_01;
    _r.v[x_max + 1][y_max + 1] = temp_11;
  }

  private static int getAYO(int offset_y, int y, int h) {
    int ayo = y - offset_y;
    while (ayo < 0) {
      ayo += h;
    }
    while (ayo >= h) {
      ayo -= h;
    }

    return ayo;
  }
}
