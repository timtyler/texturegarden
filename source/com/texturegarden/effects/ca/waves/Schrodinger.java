package com.texturegarden.effects.ca.waves;

import com.texturegarden.arrays.*;
import com.texturegarden.arrays.*;
import com.texturegarden.effects.*;
import com.texturegarden.effects.ca.CAProcess2D;
import com.texturegarden.effects.ca.VNOperation;
import com.texturegarden.utilities.*;

public class Schrodinger {
  static int cnt = 0;
  public static void animate(ComplexArray2D c_a, int type) {
    animationHelper(c_a);
    animationHelper(c_a);
  }
  
  private static void animationHelper(ComplexArray2D c_a) {
    cnt++;
    VNOperation vn_op_slow;
    if ((cnt & 1) == 0) {
      vn_op_slow = new VNOperation() {
        public int process() {
          int new_v;
          int vc;

          vc = (n & 0xffff) + (s & 0xffff) + (e & 0xffff) + (w & 0xffff);

          new_v = (c >>> 16) + (int) ((vc - ((c & 0xffff) << 2)) / 7.01f);

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
    } else {
      vn_op_slow = new VNOperation() {
        public int process() {
          int new_v;
          int vc;

          vc = (n & 0xffff) + (s & 0xffff) + (e & 0xffff) + (w & 0xffff);

          new_v = (c >>> 16) - (int) ((vc - ((c & 0xffff) << 2)) / 7.01f);

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

    }

    CAProcess2D.generalVNOperation(c_a, vn_op_slow);

    // vn_op.init(i);
    c_a.r.compensationForCA();
  }

  //new_v = ((vc  * 3) +  ((c & 0xffff) << 2) - ((c >>> 16) * 8)) >> 3;
  //new_v = ((vc  * 7) +  ((c & 0xffff) << 2) - ((c >>> 16) * 16)) >> 4;
  //new_v = ((vc  * 15) +  ((c & 0xffff) << 2) - ((c >>> 16) * 32)) >> 5;
  //new_v = ((vc  * 31) +  ((c & 0xffff) << 2) - ((c >>> 16) * 64)) >> 6;
  //new_v = ((vc  * 63) +  ((c & 0xffff) << 2) - ((c >>> 16) * 128)) >> 7;
  ////new_v = ((vc ) - ((c >>> 16) << 1)) >> 1;
}
