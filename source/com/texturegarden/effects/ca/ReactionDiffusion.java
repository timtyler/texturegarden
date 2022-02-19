package com.texturegarden.effects.ca;

import com.texturegarden.Texture;
import com.texturegarden.arrays.SimpleArray2D;

public class ReactionDiffusion {
  public  static int i_mul_u = 240;
  public static int i_mul_v = 128;

  public static int i_f = 1500;
  public static int i_k = 4970;

  public static int i_scale_u = 16;
  public static int i_scale_v = 16;

  public static int f = i_f;
  public static int k = i_k;

  public static int mul_u = i_mul_u;
  public static int mul_v = i_mul_v;

  public static int scale_u = i_scale_u;
  public static int scale_v = i_scale_v;

  public static Texture t;
  public static int[] temp;

  public  static void animateRDVN3(Texture _t) {
    t = _t;

    if (temp == null) {
      temp = new int[t.height]; // ?
    }

    SimpleArray2D _r = t.c_a2D[0].r;

    t.tfu_max = -1;
    t.tfv_max = -1;
    t.tfu_min = 0x10000;
    t.tfv_min = 0x10000;

    _r.compensationForCA();

    VNOperation vno = new VNOperation() {
      int nl_term;

      int tfu;
      int tfv;
      public  void init() {
        t.tfu_max = -1;
        t.tfv_max = -1;
        t.tfu_min = 0x10000;
        t.tfv_min = 0x10000;
      }

      // int process(int c, int n, int s, int e, int w) {

      public  int process() {
        tfu = c & 0xFFFF;
        tfv = (c >>> 16) & 0xFFFF;

        nl_term = ((((tfu) * (tfv)) >>> 16) * tfv); // (SquareRoot.sqrt(0xFFFF - tfv)));
        // int nl_term2 = ((((tfv) * (tfv)) >>> 16) * (tfu));

        tfu += ((((n & 0xFFFF) + (s & 0xFFFF) + (e & 0xFFFF) + (w & 0xFFFF) - ((c & 0xFFFF) << 2)) * mul_u) >> 10) - (nl_term >>> scale_u) + ((f * (0xFFFF - tfu)) >>> 16); // 
        tfv += ((((n >>> 16) + (s >>> 16) + (e >>> 16) + (w >>> 16) - ((c >>> 16) << 2)) * mul_v) >> 10) + (nl_term >>> scale_v) - (((k * (tfv))) >>> 16);

        tfu = (tfu < 0) ? 0 : ((tfu > 0xFFFF) ? 0xFFFF : tfu);
        tfv = (tfv < 0) ? 0 : ((tfv > 0xFFFF) ? 0xFFFF : tfv);

        t.tfu_max = (tfu > t.tfu_max) ? tfu : t.tfu_max;
        t.tfu_min = (tfu < t.tfu_min) ? tfu : t.tfu_min;
        t.tfv_max = (tfv > t.tfv_max) ? tfv : t.tfv_max;
        t.tfv_min = (tfv < t.tfv_min) ? tfv : t.tfv_min;

        return tfu | (tfv << 16);
      }
    };

    CAProcess2D.generalVNOperation(t.c_a2D[0], vno);
  }

  static void animateRDVN2(Texture _t) {
    t = _t;

    if (temp == null) {
      temp = new int[t.height]; // ?
    }

    SimpleArray2D _r = t.c_a2D[0].r;

    t.tfu_max = -1;
    t.tfv_max = -1;
    t.tfu_min = 0x10000;
    t.tfv_min = 0x10000;

    _r.compensationForCA();

    VNOperation vno = new VNOperation() {
      int nl_term;

      int tfu;
      int tfv;

      public  void init() {
        t.tfu_max = -1;
        t.tfv_max = -1;
        t.tfu_min = 0x10000;
        t.tfv_min = 0x10000;
      }

      // int process(int c, int n, int s, int e, int w) {
      public  int process() {
        tfu = c & 0xFFFF;
        tfv = (c >>> 16) & 0xFFFF;

        // nl_term = ((((tfu * tfv) >>> 16) * tfv) >>> 16) * tfu; // also works...
        // nl_term = (((tfu * tfv) >>> 16) * (0xFFFF - tfv));
        // nl_term = (((tfu * tfv) >>> 16) * tfu);
        // nl_term = (((tfu * tfv) >>> 16) * tfu);
        // nl_term = ((((tfu) * tfv) >>> 16) * (tfv + 0x1000));
        // nl_term = (((tfu * tfv) >>> 16) * tfv);
        // nl_term = ((((((tfv * tfv) >>> 16) * tfv) >>> 16) * tfu) >>> 16) * tfu; //

        nl_term = ((((tfv) * (tfv)) >>> 16) * (tfu));
        // int nl_term2 = ((((tfv) * (tfv)) >>> 16) * (tfu));

        tfu += ((((n & 0xFFFF) + (s & 0xFFFF) + (e & 0xFFFF) + (w & 0xFFFF) - ((c & 0xFFFF) << 2)) * mul_u) >> 10) + (nl_term >>> scale_u) - ((f * (0xFFFF - tfu)) >>> 16); // 
        tfv += ((((n >>> 16) + (s >>> 16) + (e >>> 16) + (w >>> 16) - ((c >>> 16) << 2)) * mul_v) >> 10) - (nl_term >>> scale_v) + (((k * (tfv))) >>> 16);

        tfu = (tfu < 0) ? 0 : ((tfu > 0xFFFF) ? 0xFFFF : tfu);
        tfv = (tfv < 0) ? 0 : ((tfv > 0xFFFF) ? 0xFFFF : tfv);

        t.tfu_max = (tfu > t.tfu_max) ? tfu : t.tfu_max;
        t.tfu_min = (tfu < t.tfu_min) ? tfu : t.tfu_min;
        t.tfv_max = (tfv > t.tfv_max) ? tfv : t.tfv_max;
        t.tfv_min = (tfv < t.tfv_min) ? tfv : t.tfv_min;

        return tfu | (tfv << 16);
      }
    };

    CAProcess2D.generalVNOperation(t.c_a2D[0], vno);
  }

  public  static void animateRDVN(Texture _t, int bn) {
    t = _t;

    if (temp == null) {
      temp = new int[t.height]; // ?
    }

    SimpleArray2D _r = t.c_a2D[bn].r;

    t.tfu_max = -1;
    t.tfv_max = -1;
    t.tfu_min = 0x10000;
    t.tfv_min = 0x10000;

    _r.compensationForCA();

    VNOperation vno = new VNOperation() {
      int nl_term;

      int tfu;
      int tfv;

      public void init() {
        t.tfu_max = -1;
        t.tfv_max = -1;
        t.tfu_min = 0x10000;
        t.tfv_min = 0x10000;
      }

      // int process(int c, int n, int s, int e, int w) {
      public  int process() {
        tfu = c & 0xFFFF;
        tfv = (c >>> 16) & 0xFFFF;

        // nl_term = ((((tfu * tfv) >>> 16) * tfv) >>> 16) * tfu; // also works...
        // nl_term = (((tfu * tfv) >>> 16) * (0xFFFF - tfv));
        // nl_term = (((tfu * tfv) >>> 16) * tfu);
        // nl_term = (((tfu * tfv) >>> 16) * tfu);
        // nl_term = ((((tfu) * tfv) >>> 16) * (tfv + 0x1000));
        // nl_term = (((tfu * tfv) >>> 16) * tfv);
        // nl_term = ((((((tfv * tfv) >>> 16) * tfv) >>> 16) * tfu) >>> 16) * tfu; //

        nl_term = (((tfu * tfv) >>> 16) * tfv);

        tfu += ((((n & 0xFFFF) + (s & 0xFFFF) + (e & 0xFFFF) + (w & 0xFFFF) - ((c & 0xFFFF) << 2)) * mul_u) >> 10) - (nl_term >>> scale_u) + ((f * (0xFFFF - tfu)) >>> 16);
        tfv += ((((n >>> 16) + (s >>> 16) + (e >>> 16) + (w >>> 16) - ((c >>> 16) << 2)) * mul_v) >> 10) + (nl_term >>> scale_v) - ((k * tfv) >>> 16);

        tfu = (tfu < 0) ? 0 : ((tfu > 0xFFFF) ? 0xFFFF : tfu);
        tfv = (tfv < 0) ? 0 : ((tfv > 0xFFFF) ? 0xFFFF : tfv);

        t.tfu_max = (tfu > t.tfu_max) ? tfu : t.tfu_max;
        t.tfu_min = (tfu < t.tfu_min) ? tfu : t.tfu_min;
        t.tfv_max = (tfv > t.tfv_max) ? tfv : t.tfv_max;
        t.tfv_min = (tfv < t.tfv_min) ? tfv : t.tfv_min;

        return tfu | (tfv << 16);
      }
    };

    CAProcess2D.generalVNOperation(t.c_a2D[bn], vno);
  }

  static void animateRDMoore(Texture _t) {
    t = _t;

    if (temp == null) {
      temp = new int[t.height]; // ?
    }

    SimpleArray2D _r = t.c_a2D[0].r;

    t.tfu_max = -1;
    t.tfv_max = -1;
    t.tfu_min = 0x10000;
    t.tfv_min = 0x10000;

    _r.compensationForCA();

     MooreOperation moore_o = new MooreOperation() {
      int nl_term;

      int tfu;
      int tfv;
      public   void init() {
        t.tfu_max = -1;
        t.tfv_max = -1;
        t.tfu_min = 0x10000;
        t.tfv_min = 0x10000;
      }

      // int process(int c, int n, int s, int e, int w) {
      public   int process() {
        tfu = c & 0xFFFF;
        tfv = (c >>> 16) & 0xFFFF;

        // nl_term = ((((tfu * tfv) >>> 16) * tfv) >>> 16) * tfu; // also works...
        // nl_term = (((tfu * tfv) >>> 16) * (0xFFFF - tfv));
        // nl_term = (((tfu * tfv) >>> 16) * tfu);
        // nl_term = (((tfu * tfv) >>> 16) * tfu);
        // nl_term = ((((tfu) * tfv) >>> 16) * (tfv + 0x1000));
        // nl_term = (((tfu * tfv) >>> 16) * tfv);
        // nl_term = ((((((tfv * tfv) >>> 16) * tfv) >>> 16) * tfu) >>> 16) * tfu; //

        nl_term = (((tfu * tfv) >>> 16) * tfv);

        tfu += ((((n & 0xFFFF) + (s & 0xFFFF) + (e & 0xFFFF) + (w & 0xFFFF) + (ne & 0xFFFF) + (nw & 0xFFFF) + (se & 0xFFFF) + (sw & 0xFFFF) - ((c & 0xFFFF) << 3)) * mul_u) >> 11) - (nl_term >>> scale_u) + ((f * (0xFFFF - tfu)) >>> 16);
        tfv += ((((n >>> 16) + (s >>> 16) + (e >>> 16) + (w >>> 16) + (ne >>> 16) + (nw >>> 16) + (se >>> 16) + (sw >>> 16) - ((c >>> 16) << 3)) * mul_v) >> 11) + (nl_term >>> scale_v) - ((k * tfv) >>> 16);

        tfu = (tfu < 0) ? 0 : ((tfu > 0xFFFF) ? 0xFFFF : tfu);
        tfv = (tfv < 0) ? 0 : ((tfv > 0xFFFF) ? 0xFFFF : tfv);

        t.tfu_max = (tfu > t.tfu_max) ? tfu : t.tfu_max;
        t.tfu_min = (tfu < t.tfu_min) ? tfu : t.tfu_min;
        t.tfv_max = (tfv > t.tfv_max) ? tfv : t.tfv_max;
        t.tfv_min = (tfv < t.tfv_min) ? tfv : t.tfv_min;

        return tfu | (tfv << 16);
      }
    };

    CAProcess2D.generalMooreOperation(t.c_a2D[0], moore_o);
  }

  static void animateRDHex(Texture _t) {
    t = _t;

    if (temp == null) {
      temp = new int[t.height]; // ?
    }

    SimpleArray2D _r = t.c_a2D[0].r;

    t.tfu_max = -1;
    t.tfv_max = -1;
    t.tfu_min = 0x10000;
    t.tfv_min = 0x10000;

    _r.compensationForCA();

    MooreOperation moore_o = new MooreOperation() {
      int nl_term;

      int tfu;
      int tfv;

      public  void init() {
        t.tfu_max = -1;
        t.tfv_max = -1;
        t.tfu_min = 0x10000;
        t.tfv_min = 0x10000;
      }

      public   int process() {
        tfu = c & 0xFFFF;
        tfv = (c >>> 16) & 0xFFFF;

        // nl_term = ((((tfu * tfv) >>> 16) * tfv) >>> 16) * tfu; // also works...
        // nl_term = (((tfu * tfv) >>> 16) * (0xFFFF - tfv));
        // nl_term = (((tfu * tfv) >>> 16) * tfu);
        // nl_term = (((tfu * tfv) >>> 16) * tfu);
        // nl_term = ((((tfu) * tfv) >>> 16) * (tfv + 0x1000));
        // nl_term = (((tfu * tfv) >>> 16) * tfv);
        // nl_term = ((((((tfv * tfv) >>> 16) * tfv) >>> 16) * tfu) >>> 16) * tfu; //

        nl_term = (((tfu * tfv) >>> 16) * tfv);

        tfu += ((((e & 0xFFFF) + (w & 0xFFFF) + (ne & 0xFFFF) + (nw & 0xFFFF) + (se & 0xFFFF) + (sw & 0xFFFF) - ((c & 0xFFFF) * 6)) * mul_u) >> 11) - (nl_term >>> scale_u) + ((f * (0xFFFF - tfu)) >>> 16);
        tfv += ((((e >>> 16) + (w >>> 16) + (ne >>> 16) + (nw >>> 16) + (se >>> 16) + (sw >>> 16) - ((c >>> 16) * 6)) * mul_v) >> 11) + (nl_term >>> scale_v) - ((k * tfv) >>> 16);

        tfu = (tfu < 0) ? 0 : ((tfu > 0xFFFF) ? 0xFFFF : tfu);
        tfv = (tfv < 0) ? 0 : ((tfv > 0xFFFF) ? 0xFFFF : tfv);

        t.tfu_max = (tfu > t.tfu_max) ? tfu : t.tfu_max;
        t.tfu_min = (tfu < t.tfu_min) ? tfu : t.tfu_min;
        t.tfv_max = (tfv > t.tfv_max) ? tfv : t.tfv_max;
        t.tfv_min = (tfv < t.tfv_min) ? tfv : t.tfv_min;

        return tfu | (tfv << 16);
      }
    };

    CAProcess2D.generalHexOperation(t.c_a2D[0], moore_o);
  }

  public  static int getF() {
    return f;
  }

  public  static int getK() {
    return k;
  }

  public  static void setF(int v) {
    f = v;
  }

  public static void setK(int v) {
    k = v;
  }

  public static int getMultU() {
    return mul_u;
  }

  public static int getMultV() {
    return mul_v;
  }

  public static int getScaleU() {
    return scale_u;
  }

  public static int getScaleV() {
    return scale_v;
  }

  //set
  public static void setMultU(int v) {
    mul_u = v;
  }

  public  static void setMultV(int v) {
    mul_v = v;
  }

  public static void setScaleU(int v) {
    scale_u = v;
  }

  public static void setScaleV(int v) {
    scale_v = v;
  }
}
