package com.texturegarden.effects.ca;

import com.texturegarden.Texture;
import com.texturegarden.TG;
import com.texturegarden.arrays.SimpleArray2D;

/**
 * DiffusionLimitedAggregation
 */

/* 
 * ToDo 
 * ====
 * 1) Check if all gas
 * 2) move gas particles
 * 3) attach gas particles
 * 4) decrease snake tails
 *
 */

public class DiffusionLimitedAggregation {
  public static Texture t;
  public static int[] temp;
  public static int threshold = 64;
  public static int density = TG.ising_model ? 45 : 32;
  public static int initial_value = 255;
  public static int number_of_seeds = 1;

  public static int frame_number = 0;

  public static void animate(Texture _t) {
    t = _t;

    if (temp == null) {
      temp = new int[t.height]; // ?
    }

    //SimpleArray2D _r = t.c_a2D[0].r;

    // int nl_term;

    // int tfu;
    // int tfv;

    // Log.log("!!!");
    // only do this once!!!!
    MargolusOperation margolus_operation = new MargolusOperation() {
        // int tfu;
    // int tfv;
  void init() {
          //t.tfu_max = -1;
    //t.tfv_max = -1;
    //t.tfu_min = 0x10000;
    //t.tfv_min = 0x10000;
    // initial_value = ((initial_value + 1) & 0xFF);
  }

        // Margolus:
    // 24, 16
    // 8 , 0
  int process() {
        int temp;

        if (((nw | ne | sw | se) & 0xFE) == 0) { // gas: rotate....
          if (b) { // rotate ACW
            temp = nw;
            nw = ne;
            ne = se;
            se = sw;
            sw = temp;
          } else { // rotate CW
            temp = nw;
            nw = sw;
            sw = se;
            se = ne;
            ne = temp;
          }
        } else { // solid...
          // freeze...
          if ((nw > threshold) || (ne > threshold) || (sw > threshold) || (se > threshold)) {
            nw = (nw == 1) ? initial_value : nw;
            ne = (ne == 1) ? initial_value : ne;
            sw = (sw == 1) ? initial_value : sw;
            se = (se == 1) ? initial_value : se;
          }

          // melt...
          if (b) {
            nw = (nw > 1) ? nw - 1 : nw;
            ne = (ne > 1) ? ne - 1 : ne;
            sw = (sw > 1) ? sw - 1 : sw;
            se = (se > 1) ? se - 1 : se;
          }
        }

        return se | (sw << 8) | (ne << 16) | (nw << 24);
      }
    };

    MargolusProcess2D.setSeed(0);

    if ((t.generation++ & 1) == 0) {
      MargolusProcess2D.generalMargolusOperationNS(t.c_a2D[0], margolus_operation);
    } else {
      MargolusProcess2D.generalMargolusOperationEW(t.c_a2D[0], margolus_operation);
    }

    // for (int i = 0; i < t.height; i++) { 
    //t.c_a.r.v[t.c_a.r.x_offset][i] = 0xFFFFFFFF;
    // }

    // CAProcess2D.generalMargolusOperation(t.c_a, margolus_operation2);
  }

  public static void diffusionLimitedAggregationSetup(Texture t) {
    SimpleArray2D _r = t.c_a2D[0].r;

    for (int x = 0; x < _r.width; x++) {
      for (int y = 0; y < _r.height; y++) {
        if (_r.rnd.nextInt(100) < density) {
          _r.v[x][y] = 0x01010101; // rnd.nextInt() & 
        } else {
          _r.v[x][y] = 0;
        }

        if (_r.rnd.nextInt(65536) < number_of_seeds) {
          _r.v[x][y] = 0xFFFFFFFF;
        }
      }
    }

    //for (int i = 0; i < number_of_seeds; i++) {
    //_r.v[_r.rnd.nextInt(_r.width)][_r.rnd.nextInt(_r.height)] = 0xFFFFFFFF;
    //}

    // _r.resetOffsets();
  }

  public   static void setThreshold(int fa) {
    threshold = fa;
  }

  public  static int getThreshold() {
    return threshold;
  }

  public  static void setDensity(int fa) {
    density = fa;
  }

  public  static int getDensity() {
    return density;
  }

  public   static void setInitialValue(int fa) {
    initial_value = fa;
  }

  public  static int getInitialValue() {
    return initial_value;
  }
}
