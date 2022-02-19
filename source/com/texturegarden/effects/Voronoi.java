package com.texturegarden.effects;

import com.texturegarden.Texture;
import com.texturegarden.arrays.*;
import com.texturegarden.effects.ca.*;

/**
 * Voronoi...
 */

public class Voronoi {
  static Texture t;
  static int[] temp;
  static MooreOperation ho;

  static {

    ho = new MooreOperation() {
      int tfu;
      int tfv;

      public void init() {
        t.tfu_max = -1;
        t.tfv_max = -1;
        t.tfu_min = 0x10000;
        t.tfv_min = 0x10000;
      }
      //fjeiuj fioweufioeuw fuiewo

      // U = LIQUID, V = GROUND.
      // int process(int c, int n, int s, int e, int w) {

      // take the smallest possible step forwards...
      public  int process() {
        if (c != 0)
          return c;

        int i = 0;
        if (n != 0)
          i++;
        if (s != 0)
          i++;
        if (e != 0)
          i++;
        if (w != 0)
          i++;
        if (ne != 0)
          i++;
        if (nw != 0)
          i++;
        if (se != 0)
          i++;
        if (sw != 0)
          i++;

        if (i > 4) {
          if (n != 0)
            return n;
          if (s != 0)
            return s;
          if (e != 0)
            return e;
          if (w != 0)
            return w;
          if (ne != 0)
            return ne;
          if (nw != 0)
            return nw;
          if (se != 0)
            return se;
          if (sw != 0)
            return sw;
        }

        return 0;
      }
    };
  }

  public static void animate(Texture _t, int bn) {
    t = _t;

    //if (temp == null) {
    //temp = new int[t.height]; // ?
    //}

    SimpleArray2D _r = t.c_a2D[bn].r;

 //   int nl_term;

  //  int tfu;
 //   int tfv;

    t.tfu_max = -1;
    t.tfv_max = -1;
    t.tfu_min = 0x10000;
    t.tfv_min = 0x10000;

    _r.compensationForCA();

    CAProcess2D.generalMooreOperation(t.c_a2D[bn], ho);
  }
}
