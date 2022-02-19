package com.texturegarden.effects.ca;

import com.texturegarden.arrays.*;
import com.texturegarden.arrays.*;
import com.texturegarden.utilities.JUR;

/**
 * MargolusProcess2D Tim Tyler 2000-2003
 */

public class MargolusProcess2D {
  static JUR rnd = new JUR();

  static void setSeed(int s) {
    rnd.setSeed(s);
  }

  static void generalMargolusOperationNS(ComplexArray2D c_a, MargolusOperation margolus_operation) {
    SimpleArray2D _r = c_a.r;
    int vN, vS;
    int temp;

    margolus_operation.init();

    for (int x = 0; x < _r.width; x++) {
      vN = _r.v[x][_r.height - 1];
      vS = _r.v[x][0];
      margolus_operation.nw = (vN >> 8) & 0xFF;
      margolus_operation.ne = vN & 0xFF;
      margolus_operation.sw = vS >>> 24;
      margolus_operation.se = (vS >> 16) & 0xFF;
      margolus_operation.b = rnd.nextBooleanEfficiently();

      temp = margolus_operation.process();

      _r.v[x][_r.height - 1] = (vN & 0xFFFF0000) | (temp >>> 16);
      _r.v[x][0] = (vS & 0xFFFF) | (temp << 16);

      for (int y = 1; y < _r.height; y++) {
        vN = _r.v[x][y - 1];
        vS = _r.v[x][y];
        margolus_operation.nw = (vN >> 8) & 0xFF;
        margolus_operation.ne = vN & 0xFF;
        margolus_operation.sw = vS >>> 24;
        margolus_operation.se = (vS >> 16) & 0xFF;
        margolus_operation.b = rnd.nextBooleanEfficiently();

        temp = margolus_operation.process();

        _r.v[x][y - 1] = (vN & 0xFFFF0000) | (temp >>> 16);
        _r.v[x][y] = (vS & 0xFFFF) | (temp << 16);
      }
    }
  }

  static void generalMargolusOperationEW(ComplexArray2D c_a, MargolusOperation margolus_operation) {
    SimpleArray2D _r = c_a.r;
    int vW, vE;
    int temp;

    //int[] tempx0 = new int[_r.height];
    //int[] tempy0 = new int[_r.width];
    //int[] tempx1 = new int[_r.height];
    //int[] tempy1 = new int[_r.width];

    margolus_operation.init();

    for (int y = 0; y < _r.height; y++) {
      vW = _r.v[_r.width - 1][y];
      vE = _r.v[0][y];
      margolus_operation.nw = (vW >> 16) & 0xFF;
      margolus_operation.sw = vW & 0xFF;
      margolus_operation.ne = vE >>> 24;
      margolus_operation.se = (vE >> 8) & 0xFF;
      margolus_operation.b = rnd.nextBooleanEfficiently();

      temp = margolus_operation.process();

      _r.v[_r.width - 1][y] = (vW & 0xFF00FF00) | ((temp & 0xFF00FF00) >>> 8);
      _r.v[0][y] = (vE & 0x00FF00FF) | ((temp & 0x00FF00FF) << 8);

      for (int x = 1; x < _r.width; x++) {
        vW = _r.v[x - 1][y];
        vE = _r.v[x][y];
        margolus_operation.nw = (vW >> 16) & 0xFF;
        margolus_operation.sw = vW & 0xFF;
        margolus_operation.ne = vE >>> 24;
        margolus_operation.se = (vE >> 8) & 0xFF;
        margolus_operation.b = rnd.nextBooleanEfficiently();

        temp = margolus_operation.process();

        //Log.log("X" + x + " Y:" + y);
        //Log.log("S:" + _r.v.length);
        //Log.log("S:" + _r.v[x].length);
        _r.v[x - 1][y] = (vW & 0xFF00FF00) | ((temp & 0xFF00FF00) >>> 8);
        _r.v[x][y] = (vE & 0x00FF00FF) | ((temp & 0x00FF00FF) << 8);
      }
    }
  }
}
