package com.texturegarden.effects.ca;

import com.texturegarden.Texture;
import com.texturegarden.TG;
import com.texturegarden.arrays.SimpleArray2D;

/**
 * LiquidCrystal...
 */

public class LiquidCrystal {
  static Texture t;
  static int[] temp;
  static int speed = 1;
  static int noise = 1;
  static int seep_rate = 0;
  static int upper_threshold = 0xC000;
  static int lower_threshold = 0x1000;
  static int flow_rate = 1;
  static int ground_rising = 0x40;
  static int log_resistance = 8;
  static VNOperation vno;
  static MooreOperation mo;

  static {
    vno = new VNOperation() {
      int pot(int theta1, int theta2) {
        int dt;
        dt = ((theta1 > theta2) ? (theta1 - theta2) : (theta2 - theta1));
        dt = ((dt > 0x8000) ? (0xFFFF - dt) : (dt));

        return (dt * dt) >> 3;
      }

      // U = LIQUID, V = GROUND.
      // int process(int c, int n, int s, int e, int w) {
      public  int process() {
        int v1;
        int v2;
        int t1 = 0;
        int t2;
        int vmin = 0x7FFFFFFF; // big number.

        c += speed;

        for (int theta = 0; theta < 0x10000; theta += 0x2000) {
          v1 = pot(n, theta) + pot(s, theta) + pot(e, theta) + pot(w, theta) + pot(c, theta);
          if (v1 < vmin) {
            vmin = v1;
            t1 = theta;
          }
        }

        t2 = t1;

        for (int theta = t1 - 0x2000; theta < (t1 + 0x2000); theta += 0x800) {
          v2 = pot(n, theta) + pot(s, theta) + pot(e, theta) + pot(w, theta) + pot(c, theta);
          if (v2 < vmin) {
            vmin = v2;
            t2 = theta;
          }
        }

        t1 = t2;

        for (int theta = t2 - 0x800; theta < (t2 + 0x800); theta += 0x200) {
          v1 = pot(n, theta) + pot(s, theta) + pot(e, theta) + pot(w, theta) + pot(c, theta);
          if (v1 < vmin) {
            vmin = v1;
            t1 = theta;
          }
        }

        t2 = t1;

        for (int theta = t1 - 0x200; theta < (t1 + 0x200); theta += 0x80) {
          v2 = pot(n, theta) + pot(s, theta) + pot(e, theta) + pot(w, theta) + pot(c, theta);
          if (v2 < vmin) {
            vmin = v2;
            t2 = theta;
          }
        }

        // t2 += speed;

        if (noise > 0) {
          return (t2 + t.rnd.nextInt(noise)) & 0xFFFF;
        } else {
          if (noise < 0) {
            return (t2 - t.rnd.nextInt(-noise)) & 0xFFFF;
          } else {
            return t2 & 0xFFFF;
          }
        }
      }
    };

    mo = new MooreOperation() {
      int pot(int theta1, int theta2) {
        int dt;
        dt = ((theta1 > theta2) ? (theta1 - theta2) : (theta2 - theta1));
        dt = ((dt > 0x8000) ? (0xFFFF - dt) : (dt));

        return (dt * dt) >> 3;
      }

      // U = LIQUID, V = GROUND.
      // int process(int c, int n, int s, int e, int w) {
      public  int process() {
        int v1;
        int v2;
        int t1 = 0;
        int t2;
        int vmin = 0x7FFFFFFF; // big number.

        c += speed;

        for (int theta = 0; theta < 0x10000; theta += 0x2000) {
          v1 = pot(n, theta) + pot(s, theta) + pot(e, theta) + pot(w, theta) + pot(c, theta) + pot(ne, theta) + pot(nw, theta) + pot(se, theta) + pot(sw, theta);
          if (v1 < vmin) {
            vmin = v1;
            t1 = theta;
          }
        }

        t2 = t1;

        for (int theta = t1 - 0x2000; theta < (t1 + 0x2000); theta += 0x800) {
          v2 = pot(n, theta) + pot(s, theta) + pot(e, theta) + pot(w, theta) + pot(c, theta) + pot(ne, theta) + pot(nw, theta) + pot(se, theta) + pot(sw, theta);
          if (v2 < vmin) {
            vmin = v2;
            t2 = theta;
          }
        }

        t1 = t2;

        for (int theta = t2 - 0x800; theta < (t2 + 0x800); theta += 0x200) {
          v1 = pot(n, theta) + pot(s, theta) + pot(e, theta) + pot(w, theta) + pot(c, theta) + pot(ne, theta) + pot(nw, theta) + pot(se, theta) + pot(sw, theta);
          if (v1 < vmin) {
            vmin = v1;
            t1 = theta;
          }
        }

        t2 = t1;

        for (int theta = t1 - 0x200; theta < (t1 + 0x200); theta += 0x80) {
          v2 = pot(n, theta) + pot(s, theta) + pot(e, theta) + pot(w, theta) + pot(c, theta) + pot(ne, theta) + pot(nw, theta) + pot(se, theta) + pot(sw, theta);
          if (v2 < vmin) {
            vmin = v2;
            t2 = theta;
          }
        }

        // t2 += speed;

        if (noise > 0) {
          return (t2 + t.rnd.nextInt(noise)) & 0xFFFF;
        } else {
          if (noise < 0) {
            return (t2 - t.rnd.nextInt(-noise)) & 0xFFFF;
          } else {
            return t2 & 0xFFFF;
          }
        }
      }
    };
  }

  public static void animate(Texture _t, int bn, int s, int n) {
    t = _t;
    speed = s - (n >>> 1);
    noise = n;

    if (temp == null) {
      temp = new int[t.height]; // ?
    }

    SimpleArray2D _r = t.c_a2D[bn].r;

    int nl_term;

    int tfu;
    int tfv;

    t.tfu_max = -1;
    t.tfv_max = -1;
    t.tfu_min = 0x10000;
    t.tfv_min = 0x10000;

    _r.compensationForCA();

    // CAProcess2D.generalVNOperation(t.c_a2D[bn], vno);
    CAProcess2D.generalMooreOperation(t.c_a2D[bn], mo);
  }

  static void setFlowRate(int fa) {
    flow_rate = fa;
  }

  static int getFlowRate() {
    return flow_rate;
  }

  static void setAddRate(int fa) {
    //Log.log("add_rate:" + add_rate);
    speed = fa;
    //Log.log("add_rate:" + add_rate);
  }

  static int getAddRate() {
    return speed;
  }

  static void setSeepRate(int fa) {
    seep_rate = fa;
  }

  static int getSeepRate() {
    return seep_rate;
  }

  static int getUpperThreshold() {
    return upper_threshold;
  }

  static void setUpperThreshold(int fa) {
    upper_threshold = fa;
  }

  static int getLowerThreshold() {
    return lower_threshold;
  }

  static void setLowerThreshold(int fa) {
    lower_threshold = fa;
  }

  static int getLogResistance() {
    return log_resistance;
  }

  static void setLogResistance(int fa) {
    log_resistance = fa;
  }

  static int getGroundRising() {
    return ground_rising;
  }

  static void setGroundRising(int fa) {
    ground_rising = fa;
  }

  public static void main(String[] args) {
    TG.main(null);
  }
}
