package com.texturegarden.effects.ca.waves;

import com.texturegarden.arrays.*;
import com.texturegarden.arrays.*;
import com.texturegarden.effects.*;
import com.texturegarden.effects.ca.CAProcess2D;
import com.texturegarden.effects.ca.VNOperation;
import com.texturegarden.utilities.*;

public class Water {
  public static void animate(ComplexArray2D c_a, int type) {
    switch (type) {
      case 0 :
        VNOperation vn_op = new VNOperation() {

          public int process() {
            int new_v;
            int vc;

            vc = (n & 0xffff) + (s & 0xffff) + (e & 0xffff) + (w & 0xffff);

            new_v = (vc >> 1) - (c >>> 16);

            if (new_v <= 0) {
              new_v = -new_v;
            } else {
              if (new_v > 0xffff) {
                new_v = 0x1ffff - new_v;
              }
            }

            return new_v | (c << 16);
          }
        };
        CAProcess2D.generalVNOperation(c_a, vn_op);

        break;

      case 1 :
        VNOperation vn_op_slow = new VNOperation() {

          public int process() {
            int new_v;
            int vel, vc;

            vc = (n & 0xffff) + (s & 0xffff) + (e & 0xffff) + (w & 0xffff);

            new_v = (vc >> 2) + (c & 0xffff) - (c >>> 16);

            if (new_v <= 0) {
              new_v = -new_v;
            } else {
              if (new_v > 0xffff) {
                new_v = 0x1ffff - new_v;
              }
            }

            return new_v | (c << 16);
          }
        };
        CAProcess2D.generalVNOperation(c_a, vn_op_slow);

        break;

      case 2 :
        VNOperation vn_op1 = new VNOperation() {
          public int process() {
            int new_v;
            int vel, vc;

            vc = (n & 0xffff) + (s & 0xffff) + (e & 0xffff) + (w & 0xffff);

            new_v = (vc >> 1) - (c >>> 16);

            if (new_v <= 0) {
              new_v = -new_v;
            } else {
              if (new_v > 0xffff) {
                new_v = 0x1ffff - new_v;
              }
            }

            return new_v | (c << 16);
          }
        };
        VNOperation vn_op2 = new VNOperation() {
          public int process() {
            int new_v;
            int vel, vc;

            vc = (n & 0xffff) + (s & 0xffff) + (e & 0xffff) + (w & 0xffff);

            new_v = (vc >> 2) + (c & 0xffff) - (c >>> 16);

            if (new_v <= 0) {
              new_v = -new_v;
            } else {
              if (new_v > 0xffff) {
                new_v = 0x1ffff - new_v;
              }
            }

            return new_v | (c << 16);
          }
        };
        WaterHelper.generalVNOperation(c_a, vn_op1, vn_op2);

        break;
    }

    // vn_op.init(i);
    c_a.r.compensationForCA();
  }

  //new_v = ((vc  * 3) +  ((c & 0xffff) << 2) - ((c >>> 16) * 8)) >> 3;
  //new_v = ((vc  * 7) +  ((c & 0xffff) << 2) - ((c >>> 16) * 16)) >> 4;
  //new_v = ((vc  * 15) +  ((c & 0xffff) << 2) - ((c >>> 16) * 32)) >> 5;
  //new_v = ((vc  * 31) +  ((c & 0xffff) << 2) - ((c >>> 16) * 64)) >> 6;
  //new_v = ((vc  * 63) +  ((c & 0xffff) << 2) - ((c >>> 16) * 128)) >> 7;
  ////new_v = ((vc ) - ((c >>> 16) << 1)) >> 1;

  // Swaps 2 adjacent points...
  void convection(SimpleArray2D _r) {
    int x;
    int y;
    int temp;
    int totn = (_r.width * _r.height) >> 6;

    for (int n = totn; --n >= 0;) {
      x = _r.rnd.nextInt(_r.height - 1);
      y = _r.rnd.nextInt(_r.height - 1);

      temp = _r.v[x][y];
      _r.v[x][y] = _r.v[x + 1][y];
      _r.v[x + 1][y] = temp;
    }

    for (int n = totn; --n >= 0;) {
      x = _r.rnd.nextInt(_r.width - 1);
      y = _r.rnd.nextInt(_r.height - 1);

      temp = _r.v[x][y];
      _r.v[x][y] = _r.v[x][y + 1];
      _r.v[x][y + 1] = temp;
    }
  }

  // Rotates 4 adjacent points...
  void convection2(SimpleArray2D _r) {
    int x;
    int y;
    int temp;
    int totn = (_r.width * _r.height) >> 9;

    for (int n = totn; --n >= 0;) {
      x = _r.rnd.nextInt(_r.width - 2);
      y = _r.rnd.nextInt(_r.height - 2);

      temp = _r.v[x][y];

      _r.v[x][y] = _r.v[x + 1][y];
      _r.v[x + 1][y] = _r.v[x + 1][y + 1];
      _r.v[x + 1][y + 1] = _r.v[x][y + 1];
      _r.v[x][y + 1] = temp;
    }
  }
}
