package com.texturegarden.effects.ca;

import com.texturegarden.Texture;
import com.texturegarden.arrays.SimpleArray2D;

/*
 *  to do : presets...
 */

public class FractalDrainage {
  public static Texture t;
  public static int[] temp;
  public static int add_rate = 210;
  public static int seep_rate = 0;
  public static int upper_threshold = 0xC000;
  public static int lower_threshold = 0x1000;
  public static int flow_rate = 1;
  public static int ground_rising = 0x40;
  public static int log_resistance = 8;
  public static VNOperation vno;
  public static VNOperation vno2;
  public static MooreOperation ho;
  public static MooreOperation Mothershipmo;
  public static MooreOperation mo;
  public static MooreOperation intermo;

  static {
    // Log.log("!!!");
    vno = new VNOperation() {
      int tfu;
      int tfv;

      public  void init() {
        t.tfu_max = -1;
        t.tfv_max = -1;
        t.tfu_min = 0x10000;
        t.tfv_min = 0x10000;
      }

      // U = LIQUID, V = GROUND.
      // int process(int c, int n, int s, int e, int w) {
      public  int process() {
        // does everything twice - a pity...
        int hn, hs, he, hw, hc;
        int dn, ds, de, dw, dc;
       // int sum_n, sum_s, sum_e, sum_w, sum_c;
        int rn, rs, re, rw;

        // get heights...
        hn = n >>> 16;
        hs = s >>> 16;
        he = e >>> 16;
        hw = w >>> 16;
        hc = c >>> 16;

        // get densities...
        dn = n & 0xFFFF;
        ds = s & 0xFFFF;
        de = e & 0xFFFF;
        dw = w & 0xFFFF;
        dc = c & 0xFFFF;
        // in proportion...
        // make index variable...
        re = (((he - hc) * de) >> flow_rate);
        rw = (((hw - hc) * dw) >> flow_rate);
        rn = (((hn - hc) * dn) >> flow_rate);
        rs = (((hs - hc) * ds) >> flow_rate);

        if (re > (de >> 2)) {
          re = de >> 2;
        }

        if (-re > (dc >> 2)) {
          re = -dc >> 2;
        }

        if (rw > (dw >> 2)) {
          rw = dw >> 2;
        }

        if (-rw > (dc >> 2)) {
          rw = -dc >> 2;
        }

        if (rn > (dn >> 2)) {
          rn = dn >> 2;
        }

        if (-rn > (dc >> 2)) {
          rn = -dc >> 2;
        }

        if (rs > (ds >> 2)) {
          rs = ds >> 2;
        }

        if (-rs > (dc >> 2)) {
          rs = -dc >> 2;
        }

        //rain...
        // tfu = (dc + dc + dc + dc + rn + rs + re + rw + t.rnd.nextInt(0x800)) >> 3; // tfu = (rn + rs + re + rw + 0x400) >> 2;
        // tfu = (dc + rne + rse + rnw + rsw + re + rw + t.rnd.nextInt(0x700)) / 7; // + t.rnd.nextInt(0x700)) / 7; // tfu = (rn + rs + re + rw + 0x400) >> 2;
        // tfu = ((dc * 2) + rn + rs + rne + rse + rnw + rsw + re + rw + 0x400) / 10;
        // tfu = (rn + rs + rne + rse + rnw + rsw + re + rw + 0x400) / 8;
        // tfu = (rn + rs + rne + rse + rnw + rsw + re + rw + t.rnd.nextInt(0x800)) / 8;
        // tfu = (rn + rs + rne + rse + rnw + rsw + re + rw + t.rnd.nextInt(0x400)) >> 3;
        // tfu = ((dc << 3) + n + rs + rne + rse + rnw + rsw + re + rw) >> 4;

        tfu = (((dc << 2) + (rn + rs + re + rw)) >> 2) - seep_rate;
        if (hc > upper_threshold) {
          tfu += t.rnd.nextInt(add_rate); // rain on high ground...
        }

        if (dc >= dn) {
          if (dc >= ds) {
            if (dc >= de) {
              if (dc >= dw) {
                tfu += t.rnd.nextInt(add_rate); // rain on high ground...
              }
            }
          }
        }

        // tfv = (((hc << 3) + hn + hs + hne + hnw + hse + hsw + he + hw) >> 4) + 0x40 - (tfu >> 8);

        if (hc < lower_threshold) {
          tfu -= 0x1000; // seep into low ground...
        }

        if (hc < hn) {
          if (hc < hs) {
            if (hc < he) {
              if (hc < hw) {
                tfu -= 0x40; // seep into holes in the ground...
              }
            }
          }
        }

        tfu = (tfu < 0) ? 0 : ((tfu > 0xFFFF) ? 0xFFFF : tfu);
        tfv = ((hn + hs + he + hw) >> 2) + 0x20 - (tfu >> 7);
        tfv = (tfv < 0) ? 0 : ((tfv > 0xFFFF) ? 0xFFFF : tfv);

        t.tfu_max = (tfu > t.tfu_max) ? tfu : t.tfu_max;
        t.tfu_min = (tfu < t.tfu_min) ? tfu : t.tfu_min;
        t.tfv_max = (tfv > t.tfv_max) ? tfv : t.tfv_max;
        t.tfv_min = (tfv < t.tfv_min) ? tfv : t.tfv_min;

        return tfu | (tfv << 16);
      }
    };

    ho = new MooreOperation() {
      int tfu;
      int tfv;

      public void init() {
        t.tfu_max = -1;
        t.tfv_max = -1;
        t.tfu_min = 0x10000;
        t.tfv_min = 0x10000;
      }

      // U = LIQUID, V = GROUND.
      // int process(int c, int n, int s, int e, int w) {
      public int process() {
        // does everything twice - a pity...
        int hne, hse, hnw, hsw, hn, hs, he, hw, hc;
        int dne, dse, dnw, dsw, dn, ds, de, dw, dc;
        int sum_ne, sum_se, sum_nw, sum_sw, sum_n, sum_s, sum_e, sum_w, sum_c;
        int rne, rse, rnw, rsw, rn, rs, re, rw;

        // get heights...
        //hn = n >>> 16;
        //hs = s >>> 16;
        he = e >>> 16;
        hw = w >>> 16;
        hc = c >>> 16;
        hne = ne >>> 16;
        hnw = nw >>> 16;
        hse = se >>> 16;
        hsw = sw >>> 16;

        // get densities...
        //dn = n & 0xFFFF;
        //ds = s & 0xFFFF;
        de = e & 0xFFFF;
        dw = w & 0xFFFF;
        dc = c & 0xFFFF;
        dne = ne & 0xFFFF;
        dnw = nw & 0xFFFF;
        dse = se & 0xFFFF;
        dsw = sw & 0xFFFF;

        // flow:

        // no juice?
        re = dc + (((he - hc) * de) >> 7);
        rw = dc + (((hw - hc) * dw) >> 7);
        rne = dc + (((hne - hc) * dne) >> 7);
        rse = dc + (((hse - hc) * dse) >> 7);
        rnw = dc + (((hnw - hc) * dnw) >> 7);
        rsw = dc + (((hsw - hc) * dsw) >> 7);

        tfu = (rne + rse + rnw + rsw + re + rw) / 6;

        if (hc > 0xF000) {
          tfu += t.rnd.nextInt(add_rate); // rain on high ground...
        }

        if (hc < 0x800) {
          tfu -= 0xFFFF; // seep into low ground...
        }

        tfu = (tfu < 0) ? 0 : ((tfu > 0xFFFF) ? 0xFFFF : tfu);

        // tfv = (((dc * 8) + hn + hs + hne + hnw + hse + hsw + he + hw) >> 4) + 0x40 - (tfu >> 8);
        tfv = ((hne + hnw + hse + hsw + he + hw) / 6) + 0x20 - (tfu >> 8);

        // maximum and minimum check...
        tfu = (tfu < 0) ? 0 : ((tfu > 0xFFFF) ? 0xFFFF : tfu);

        t.tfu_max = (tfu > t.tfu_max) ? tfu : t.tfu_max;
        t.tfu_min = (tfu < t.tfu_min) ? tfu : t.tfu_min;
        t.tfv_max = (tfv > t.tfv_max) ? tfv : t.tfv_max;
        t.tfv_min = (tfv < t.tfv_min) ? tfv : t.tfv_min;

        return tfu | (tfv << 16);
      }
    };

    // Normal usage
    intermo = new MooreOperation() {

      int tfu;
      int tfv;
      int frp2;

      public  void init() {

        t.tfu_max = -1;
        t.tfv_max = -1;
        t.tfu_min = 0x10000;
        t.tfv_min = 0x10000;

        frp2 = (flow_rate + 2);
      }

      // U = LIQUID, V = GROUND.
      // int process(int c, int n, int s, int e, int w) {

      public int process() {

        // does everything twice - a pity...
        int hne, hse, hnw, hsw, hn, hs, he, hw, hc;
        int dne, dse, dnw, dsw, dn, ds, de, dw, dc;
        int sum_ne, sum_se, sum_nw, sum_sw, sum_n, sum_s, sum_e, sum_w, sum_c;
        int rne, rse, rnw, rsw, rn, rs, re, rw;
        int jne, jse, jnw, jsw, jn, js, je, jw;

        // get heights...
        hn = n >>> 16;
        hs = s >>> 16;
        he = e >>> 16;
        hw = w >>> 16;
        hc = c >>> 16;
        hne = ne >>> 16;
        hnw = nw >>> 16;
        hse = se >>> 16;
        hsw = sw >>> 16;

        // get densities...
        dn = n & 0xFFFF;
        ds = s & 0xFFFF;
        de = e & 0xFFFF;
        dw = w & 0xFFFF;
        dc = c & 0xFFFF;
        dne = ne & 0xFFFF;
        dnw = nw & 0xFFFF;
        dse = se & 0xFFFF;
        dsw = sw & 0xFFFF;

        // flow:
        // in proportion...
        // make index variable...
        re = (((he - hc) * de) >> flow_rate);
        rw = (((hw - hc) * dw) >> flow_rate);
        rn = (((hn - hc) * dn) >> flow_rate);
        rs = (((hs - hc) * ds) >> flow_rate);
        rne = ((((hne - hc) * dne) >> frp2)); //  * 11) >> 11);
        rse = ((((hse - hc) * dse) >> frp2)); // * 11) >> 11);
        rnw = ((((hnw - hc) * dnw) >> frp2)); //* 11) >> 11);
        rsw = ((((hsw - hc) * dsw) >> frp2)); // * 11) >> 11);

        int max_liquid_available = dc >> 3;

        if (re > (de >> 3)) {
          re = de >> 3;
        }

        if (-re > (max_liquid_available)) {
          re = -max_liquid_available;
        }

        if (rw > (dw >> 3)) {
          rw = dw >> 3;
        }

        if (-rw > (max_liquid_available)) {
          rw = -max_liquid_available;
        }

        if (rn > (dn >> 3)) {
          rn = dn >> 3;
        }

        if (-rn > (max_liquid_available)) {
          rn = -max_liquid_available;
        }

        if (rs > (ds >> 3)) {
          rs = ds >> 3;
        }

        if (-rs > (max_liquid_available)) {
          rs = -max_liquid_available;
        }

        if (rne > (dne >> 3)) {
          rne = dne >> 3;
        }

        if (-rne > (max_liquid_available)) {
          rne = -max_liquid_available;
        }

        if (rse > (dse >> 3)) {
          rse = dse >> 3;
        }

        if (-rse > (max_liquid_available)) {
          rse = -max_liquid_available;
        }

        if (rnw > (dnw >> 3)) {
          rnw = dnw >> 3;
        }

        if (-rnw > (max_liquid_available)) {
          rnw = -max_liquid_available;
        }

        if (rsw > (dsw >> 3)) {
          rsw = dsw >> 3;
        }

        if (-rsw > (max_liquid_available)) {
          rsw = -max_liquid_available;
        }

        // rain...
        tfu = (((dc << 3) + rn + rs + re + rw + rne + rse + rnw + rsw) >> 3) - seep_rate;
        if (hc > upper_threshold) {
          tfu += t.rnd.nextInt(add_rate); // rain on high ground...
        }

        // tfv = (((hc << 3) + hn + hs + hne + hnw + hse + hsw + he + hw) >> 4) + 0x40 - (tfu >> 8);

        if (hc < lower_threshold) {
          tfu -= 0x4000; // seep into low ground...
        }

        tfu = (tfu < 0) ? 0 : ((tfu > 0xFFFF) ? 0xFFFF : tfu);

        // other liquid...
        je = (((de - dc) * he) >> flow_rate);
        jw = (((dw - dc) * hw) >> flow_rate);
        jn = (((dn - dc) * hn) >> flow_rate);
        js = (((ds - dc) * hs) >> flow_rate);
        jne = (((dne - dc) * hne) >> frp2);
        jse = (((dse - dc) * hse) >> frp2);
        jnw = (((dnw - dc) * hnw) >> frp2);
        jsw = (((dsw - dc) * hsw) >> frp2);

        max_liquid_available = hc >> 3;

        if (je > (he >> 3)) {
          je = he >> 3;
        }

        if (-je > (max_liquid_available)) {
          je = -max_liquid_available;
        }

        if (jw > (hw >> 3)) {
          jw = hw >> 3;
        }

        if (-jw > (max_liquid_available)) {
          jw = -max_liquid_available;
        }

        if (jn > (hn >> 3)) {
          jn = hn >> 3;
        }

        if (-jn > (max_liquid_available)) {
          jn = -max_liquid_available;
        }

        if (js > (hs >> 3)) {
          js = hs >> 3;
        }

        if (-js > (max_liquid_available)) {
          js = -max_liquid_available;
        }

        if (jne > (hne >> 3)) {
          jne = hne >> 3;
        }

        if (-jne > (max_liquid_available)) {
          jne = -max_liquid_available;
        }

        if (jse > (hse >> 3)) {
          jse = hse >> 3;
        }

        if (-jse > (max_liquid_available)) {
          jse = -max_liquid_available;
        }

        if (jnw > (hnw >> 3)) {
          jnw = hnw >> 3;
        }

        if (-jnw > (max_liquid_available)) {
          jnw = -max_liquid_available;
        }

        if (jsw > (hsw >> 3)) {
          jsw = hsw >> 3;
        }

        if (-jsw > (max_liquid_available)) {
          jsw = -max_liquid_available;
        }

        tfv = (((hc << 3) + jn + js + je + jw + jne + jse + jnw + jsw) >> 3) - seep_rate;
        if (dc > upper_threshold) {
          tfv += t.rnd.nextInt(add_rate); // rain on high ground...
        }

        // tfv = (((hc << 3) + hn + hs + hne + hnw + hse + hsw + he + hw) >> 4) + 0x40 - (tfu >> 8);

        if (dc < lower_threshold) {
          tfv -= 0x4000; // seep into low ground...
        }

        tfv = (tfv < 0) ? 0 : ((tfv > 0xFFFF) ? 0xFFFF : tfv);

        // end liquid 2...

        // tfv = (((hc * 52) + (hn * 51) + (hs * 51) + (he * 51) + (hw * 51)) >> 8) //  + hne + hnw + hse + hsw) >> 3) // grounh diffusion
        // hc
        //tfv = ((hn + hs + he + hw + hne + hnw + hse + hsw) >> 3) // ground diffusion
        // dodgy?  might help...
        // tfv = (((hc * 56) + hn + hs + he + hw + hne + hnw + hse + hsw) >> 6) // ground diffusion
        //tfv = (hc) // ground diffusion
        // + ground_rising; // ground rising up 
        // - (0 >> 7); // water corrodes the ground...

        /*
           tfv -= // etching_intensity
                 (  ((rnw > 0) ? rsw : -rsw) +
                    ((rne > 0) ? rne : -rne) +
                    ((rsw > 0) ? rsw : -rsw) +
                    ((rse > 0) ? rse : -rse) +
                    ((rn > 0)  ? rn  : -rn ) +
                    ((rs > 0)  ? rs  : -rs ) +
                    ((re > 0)  ? re  : -re ) +
                    ((rw > 0)  ? rw  : -rw)) >> log_resistance;
        */

        // maximum and minimum check...

        // tfv = (tfv < 0) ? 0 : ((tfv > 0xFFFF) ? 0xFFFF : tfv);

        t.tfu_max = (tfu > t.tfu_max) ? tfu : t.tfu_max;
        t.tfu_min = (tfu < t.tfu_min) ? tfu : t.tfu_min;
        t.tfv_max = (tfv > t.tfv_max) ? tfv : t.tfv_max;
        t.tfv_min = (tfv < t.tfv_min) ? tfv : t.tfv_min;

        return tfu | (tfv << 16);
      }
    };

    intermo = new MooreOperation() {

      int tfu;
      int tfv;
      int frp2;
      int sub_a;

      public void init() {

        sub_a = t.tfv_min;

        t.tfu_max = -1;
        t.tfv_max = -1;
        t.tfu_min = 0x10000;
        t.tfv_min = 0x10000;

        frp2 = (flow_rate + 2);
      }

      // U = LIQUID, V = GROUND.
      // int process(int c, int n, int s, int e, int w) {

      public int process() {

        // does everything twice - a pity...
        int hne, hse, hnw, hsw, hn, hs, he, hw, hc;
        int dne, dse, dnw, dsw, dn, ds, de, dw, dc;
        int sum_ne, sum_se, sum_nw, sum_sw, sum_n, sum_s, sum_e, sum_w, sum_c;
        int rne, rse, rnw, rsw, rn, rs, re, rw;

        // get heights...
        hn = n >>> 16;
        hs = s >>> 16;
        he = e >>> 16;
        hw = w >>> 16;
        hc = c >>> 16;
        hne = ne >>> 16;
        hnw = nw >>> 16;
        hse = se >>> 16;
        hsw = sw >>> 16;

        // get densities...
        dn = n & 0xFFFF;
        ds = s & 0xFFFF;
        de = e & 0xFFFF;
        dw = w & 0xFFFF;
        dc = c & 0xFFFF;
        dne = ne & 0xFFFF;
        dnw = nw & 0xFFFF;
        dse = se & 0xFFFF;
        dsw = sw & 0xFFFF;

        // flow:
        // in proportion...
        // make index variable...
        re = (((he - hc) * de) >> flow_rate);
        rw = (((hw - hc) * dw) >> flow_rate);
        rn = (((hn - hc) * dn) >> flow_rate);
        rs = (((hs - hc) * ds) >> flow_rate);
        rne = ((((hne - hc) * dne) >> frp2)); //  * 11) >> 11);
        rse = ((((hse - hc) * dse) >> frp2)); // * 11) >> 11);
        rnw = ((((hnw - hc) * dnw) >> frp2)); //* 11) >> 11);
        rsw = ((((hsw - hc) * dsw) >> frp2)); // * 11) >> 11);

        int max_liquid_available = dc >> 3;

        if (re > (de >> 3)) {
          re = de >> 3;
        }

        if (-re > (max_liquid_available)) {
          re = -max_liquid_available;
        }

        if (rw > (dw >> 3)) {
          rw = dw >> 3;
        }

        if (-rw > (max_liquid_available)) {
          rw = -max_liquid_available;
        }

        if (rn > (dn >> 3)) {
          rn = dn >> 3;
        }

        if (-rn > (max_liquid_available)) {
          rn = -max_liquid_available;
        }

        if (rs > (ds >> 3)) {
          rs = ds >> 3;
        }

        if (-rs > (max_liquid_available)) {
          rs = -max_liquid_available;
        }

        if (rne > (dne >> 3)) {
          rne = dne >> 3;
        }

        if (-rne > (max_liquid_available)) {
          rne = -max_liquid_available;
        }

        if (rse > (dse >> 3)) {
          rse = dse >> 3;
        }

        if (-rse > (max_liquid_available)) {
          rse = -max_liquid_available;
        }

        if (rnw > (dnw >> 3)) {
          rnw = dnw >> 3;
        }

        if (-rnw > (max_liquid_available)) {
          rnw = -max_liquid_available;
        }

        if (rsw > (dsw >> 3)) {
          rsw = dsw >> 3;
        }

        if (-rsw > (max_liquid_available)) {
          rsw = -max_liquid_available;
        }

        // rain...
        tfu = (((dc << 2) + rn + rs + re + rw + rne + rse + rnw + rsw) >> 2); // - seep_rate;

        if (hc > upper_threshold) {
          tfu -= 0x1000; // seep into high ground...
        }

        if (hc < lower_threshold) {
          tfu += t.rnd.nextInt(add_rate); // rain on low ground...
        }

        //if (dc < lower_threshold) {
        //tfu += t.rnd.nextInt(add_rate); // rain on empty ground...
        //}

        tfu = (tfu < 0) ? 0 : ((tfu > 0xFFFF) ? 0xFFFF : tfu);

          tfv = (((hc * 52) + (hn * 51) + (hs * 51) + (he * 51) + (hw * 51)) >> 8) //  + hne + hnw + hse + hsw) >> 3) // ground diffusion
    // hc
    //tfv = ((hn + hs + he + hw + hne + hnw + hse + hsw) >> 3) // ground diffusion
    // dodgy?  might help...
    // tfv = (((hc * 56) + hn + hs + he + hw + hne + hnw + hse + hsw) >> 6) // ground diffusion
    //tfv = (hc) // ground diffusion
    // - sub_a; // ground_rising; // ground rising up 
  -ground_rising; // ground rising up 
        // - (0 >> 7); // water corrodes the ground...

        if (dc < lower_threshold) {
          tfv -= seep_rate; // dry ground sinks...
        }

        if (dc > upper_threshold) {
          tfv += seep_rate; // watery ground rises...
        }

        tfv += // etching_intensity
         (((rnw > 0) ? rsw : -rsw) + ((rne > 0) ? rne : -rne) + ((rsw > 0) ? rsw : -rsw) + ((rse > 0) ? rse : -rse) + ((rn > 0) ? rn : -rn) + ((rs > 0) ? rs : -rs) + ((re > 0) ? re : -re) + ((rw > 0) ? rw : -rw)) >> log_resistance;

        // maximum and minimum check...
        //if (tfv < 0) tfv = 0xFFFF;
        //if (tfv > 0xFFFF) tfv = 0;

        tfv = (tfv < 0) ? 0 : ((tfv > 0xFFFF) ? 0xFFFF : tfv);

        t.tfu_max = (tfu > t.tfu_max) ? tfu : t.tfu_max;
        t.tfu_min = (tfu < t.tfu_min) ? tfu : t.tfu_min;
        t.tfv_max = (tfv > t.tfv_max) ? tfv : t.tfv_max;
        t.tfv_min = (tfv < t.tfv_min) ? tfv : t.tfv_min;

        return tfu | (tfv << 16);
      }
    };

    Mothershipmo = new MooreOperation() {
      int tfu;
      int tfv;
      int frp2;

      public void init() {
        t.tfu_max = -1;
        t.tfv_max = -1;
        t.tfu_min = 0x10000;
        t.tfv_min = 0x10000;

        frp2 = (flow_rate + 2);
      }

      // U = LIQUID, V = GROUND.
      // int process(int c, int n, int s, int e, int w) {
      public int process() {
        // does everything twice - a pity...
        int hne, hse, hnw, hsw, hn, hs, he, hw, hc;
        int dne, dse, dnw, dsw, dn, ds, de, dw, dc;
        int sum_ne, sum_se, sum_nw, sum_sw, sum_n, sum_s, sum_e, sum_w, sum_c;
        int rne, rse, rnw, rsw, rn, rs, re, rw;

        // get heights...
        hn = n >>> 16;
        hs = s >>> 16;
        he = e >>> 16;
        hw = w >>> 16;
        hc = c >>> 16;
        hne = ne >>> 16;
        hnw = nw >>> 16;
        hse = se >>> 16;
        hsw = sw >>> 16;

        // get densities...
        dn = n & 0xFFFF;
        ds = s & 0xFFFF;
        de = e & 0xFFFF;
        dw = w & 0xFFFF;
        dc = c & 0xFFFF;
        dne = ne & 0xFFFF;
        dnw = nw & 0xFFFF;
        dse = se & 0xFFFF;
        dsw = sw & 0xFFFF;

        // flow:

        // in proportion...
        // make index variable...
        re = (((he - hc) * de) >> flow_rate);
        rw = (((hw - hc) * dw) >> flow_rate);
        rn = (((hn - hc) * dn) >> flow_rate);
        rs = (((hs - hc) * ds) >> flow_rate);
        rne = ((((hne - hc) * dne) >> frp2)); //  * 11) >> 11);
        rse = ((((hse - hc) * dse) >> frp2)); // * 11) >> 11);
        rnw = ((((hnw - hc) * dnw) >> frp2)); //* 11) >> 11);
        rsw = ((((hsw - hc) * dsw) >> frp2)); // * 11) >> 11);

        /*
        if (re > (de >> 3)) {
           re = de >> 3;
        }
        
        if (-re > (dc >> 3)) {
           re = -dc >> 3;
        }
        
        if (rw > (dw >> 3)) {
           rw = dw >> 3;
        }
        
        if (-rw > (dc >> 3)) {
           rw = -dc >> 3;
        }
        
        if (rn > (dn >> 3)) {
           rn = dn >> 3;
        }
        
        if (-rn > (dc >> 3)) {
           rn = -dc >> 3;
        }
        
        if (rs > (ds >> 3)) {
           rs = ds >> 3;
        }
        
        if (-rs > (dc >> 3)) {
           rs = -dc >> 3;
        }
        
        if (rne > (dne >> 3)) {
           rne = dne >> 3;
        }
        
        if (-rne > (dc >> 3)) {
           rne = -dc >> 3;
        }
        
        if (rse > (dse >> 3)) {
           rse = dse >> 3;
        }
        
        if (-rse > (dc >> 3)) {
           rse = -dc >> 3;
        }
        
        if (rnw > (dnw >> 3)) {
           rnw = dnw >> 3;
        }
        
        if (-rnw > (dc >> 3)) {
           rnw = -dc >> 3;
        }
        
        if (rsw > (dsw >> 3)) {
           rsw = dsw >> 3;
        }
        
        if (-rsw > (dc >> 3)) {
           rsw = -dc >> 3;
        }
        */

        //rain...
        // tfu = (dc + dc + dc + dc + rn + rs + re + rw + t.rnd.nextInt(0x800)) >> 3; // tfu = (rn + rs + re + rw + 0x400) >> 2;
        // tfu = (dc + rne + rse + rnw + rsw + re + rw + t.rnd.nextInt(0x700)) / 7; // + t.rnd.nextInt(0x700)) / 7; // tfu = (rn + rs + re + rw + 0x400) >> 2;
        // tfu = ((dc * 2) + rn + rs + rne + rse + rnw + rsw + re + rw + 0x400) / 10;
        // tfu = (rn + rs + rne + rse + rnw + rsw + re + rw + 0x400) / 8;
        // tfu = (rn + rs + rne + rse + rnw + rsw + re + rw + t.rnd.nextInt(0x800)) / 8;
        // tfu = (rn + rs + rne + rse + rnw + rsw + re + rw + t.rnd.nextInt(0x400)) >> 3;
        // tfu = ((dc << 3) + n + rs + rne + rse + rnw + rsw + re + rw) >> 4;
        tfu = (((dc << 3) + rn + rs + re + rw + rne + rse + rnw + rsw) >> 3) - seep_rate;
        if (hc > upper_threshold) {
          tfu += t.rnd.nextInt(add_rate); // rain on high ground...
        }

        // tfv = (((hc << 3) + hn + hs + hne + hnw + hse + hsw + he + hw) >> 4) + 0x40 - (tfu >> 8);

        if (hc < lower_threshold) {
          tfu -= 0x1000; // seep into low ground...
        }

        if (hc < hn) {
          if (hc < hs) {
            if (hc < he) {
              if (hc < hw) {
                tfu -= 0x40; // seep into holes in the ground...
              }
            }
          }
        }

        tfu = (tfu < 0) ? 0 : ((tfu > 0xFFFF) ? 0xFFFF : tfu);
        tfv = ((hn + hs + he + hw + hne + hnw + hse + hsw) >> 3) + 0x20 - (tfu >> 7);

        /*
        if (hc > hn) {
           if (hc > hs) {
              if (hc > he) {
                 if (hc > hw) {
                    tfu += t.rnd.nextInt(add_rate);
                    tfu = (tfu < 0) ? 0 : ((tfu > 0xFFFF) ? 0xFFFF : tfu);
                 }
              }
           }
        }
        */

        // tfv = (hc) + 0x0 - (tfu >> 9);
        // tfv = (((hc * 8) + hn + hs + hne + hnw + hse + hsw + he + hw) >> 4) - 0x100 - (tfu >> 8); // surface etching...
        //tfv = hc - 0x20 - (tfu >> 8);
        // tfv = (((hc * 10) + hne + hnw + hse + hsw + he + hw) >> 4) + t.rnd.nextInt(0xA0) - (tfu >> 6); // surface etching...
        //tfv = ((hc + hc + hc + hc + hn + hs + he + hw) >> 3) + t.rnd.nextInt(0x20) - (tfu >> 8); // surface etching...
        // tfv = ((hn + hs + he + hw) >> 2) + t.rnd.nextInt(0x80) - (tfu >> 11); // surface etching...
        // tfv = 0;

        // maximum and minimum check...

        tfv = (tfv < 0) ? 0 : ((tfv > 0xFFFF) ? 0xFFFF : tfv);

        t.tfu_max = (tfu > t.tfu_max) ? tfu : t.tfu_max;
        t.tfu_min = (tfu < t.tfu_min) ? tfu : t.tfu_min;

        t.tfv_max = (tfv > t.tfv_max) ? tfv : t.tfv_max;
        t.tfv_min = (tfv < t.tfv_min) ? tfv : t.tfv_min;

        return tfu | (tfv << 16);
      }
    };

    vno2 = new VNOperation() {
      int tfu;
      int tfv;
      int frp2;

      public void init() {
        t.tfu_max = -1;
        t.tfv_max = -1;
        t.tfu_min = 0x10000;
        t.tfv_min = 0x10000;

        frp2 = (flow_rate + 2);
      }

      // U = LIQUID, V = GROUND.
      // int process(int c, int n, int s, int e, int w) {

      public int process() {
        // does everything twice - a pity...
        int hne, hse, hnw, hsw, hn, hs, he, hw, hc;
        int dne, dse, dnw, dsw, dn, ds, de, dw, dc;
        int sum_ne, sum_se, sum_nw, sum_sw, sum_n, sum_s, sum_e, sum_w, sum_c;
        int rne, rse, rnw, rsw, rn, rs, re, rw;

        // get heights...
        hn = n >>> 16;
        hs = s >>> 16;
        he = e >>> 16;
        hw = w >>> 16;
        hc = c >>> 16;

        // get densities...
        dn = n & 0xFFFF;
        ds = s & 0xFFFF;
        de = e & 0xFFFF;
        dw = w & 0xFFFF;
        dc = c & 0xFFFF;

        // flow:
        // in proportion...
        // make index variable...
        re = (((he - hc) * de) >> flow_rate);
        rw = (((hw - hc) * dw) >> flow_rate);
        rn = (((hn - hc) * dn) >> flow_rate);
        rs = (((hs - hc) * ds) >> flow_rate);

        if (re > (de >> 3)) {
          re = de >> 3;
        }

        if (-re > (dc >> 3)) {
          re = -dc >> 3;
        }

        if (rw > (dw >> 3)) {
          rw = dw >> 3;
        }

        if (-rw > (dc >> 3)) {
          rw = -dc >> 3;
        }

        if (rn > (dn >> 3)) {
          rn = dn >> 3;
        }

        if (-rn > (dc >> 3)) {
          rn = -dc >> 3;
        }

        if (rs > (ds >> 3)) {
          rs = ds >> 3;
        }

        if (-rs > (dc >> 3)) {
          rs = -dc >> 3;
        }

        // rain...
        tfu = (((dc << 2) + rn + rs + re + rw) >> 2) - seep_rate;
        if (hc > upper_threshold) {
          tfu += t.rnd.nextInt(add_rate); // rain on high ground...
        }

        // tfv = (((hc << 3) + hn + hs + hne + hnw + hse + hsw + he + hw) >> 4) + 0x40 - (tfu >> 8);

        if (hc < lower_threshold) {
          tfu -= 0x4000; // seep into low ground...
        }

        tfu = (tfu < 0) ? 0 : ((tfu > 0xFFFF) ? 0xFFFF : tfu);

          tfv = ((hn + hs + he + hw) >> 2) // ground diffusion
    // dodgy?  might help...
    // tfv = (((hc * 24) + hn + hs + he + hw + hne + hnw + hse + hsw) >> 5) // ground diffusion
    //tfv = (hc) // ground diffusion
  +ground_rising; // ground rising up 
        // - (0 >> 7); // water corrodes the ground...

        // int shi = log_resistance;

        tfv -= // etching_intensity
         (((rn > 0) ? rn : -rn) + ((rs > 0) ? rs : -rs) + ((re > 0) ? re : -re) + ((rw > 0) ? rw : -rw)) >> log_resistance;

        // maximum and minimum check...
        tfv = (tfv < 0) ? 0 : ((tfv > 0xFFFF) ? 0xFFFF : tfv);

        t.tfu_max = (tfu > t.tfu_max) ? tfu : t.tfu_max;
        t.tfu_min = (tfu < t.tfu_min) ? tfu : t.tfu_min;
        t.tfv_max = (tfv > t.tfv_max) ? tfv : t.tfv_max;
        t.tfv_min = (tfv < t.tfv_min) ? tfv : t.tfv_min;

        return tfu | (tfv << 16);
      }
    };

    // Normal usage
    mo = new MooreOperation() {
      int tfu;
      int tfv;
      int frp2;

      public  void init() {
        t.tfu_max = -1;
        t.tfv_max = -1;
        t.tfu_min = 0x10000;
        t.tfv_min = 0x10000;

        frp2 = (flow_rate + 2);
      }

      // U = LIQUID, V = GROUND.
      // int process(int c, int n, int s, int e, int w) {
      public  int process() {
        // does everything twice - a pity...
        int hne, hse, hnw, hsw, hn, hs, he, hw, hc;
        int dne, dse, dnw, dsw, dn, ds, de, dw, dc;
        int sum_ne, sum_se, sum_nw, sum_sw, sum_n, sum_s, sum_e, sum_w, sum_c;
        int rne, rse, rnw, rsw, rn, rs, re, rw;

        // get heights...
        hn = n >>> 16;
        hs = s >>> 16;
        he = e >>> 16;
        hw = w >>> 16;
        hc = c >>> 16;
        hne = ne >>> 16;
        hnw = nw >>> 16;
        hse = se >>> 16;
        hsw = sw >>> 16;

        // get densities...
        dn = n & 0xFFFF;
        ds = s & 0xFFFF;
        de = e & 0xFFFF;
        dw = w & 0xFFFF;
        dc = c & 0xFFFF;
        dne = ne & 0xFFFF;
        dnw = nw & 0xFFFF;
        dse = se & 0xFFFF;
        dsw = sw & 0xFFFF;

        // flow:
        // in proportion...
        // make index variable...
        re = (((he - hc) * de) >> flow_rate);
        rw = (((hw - hc) * dw) >> flow_rate);
        rn = (((hn - hc) * dn) >> flow_rate);
        rs = (((hs - hc) * ds) >> flow_rate);
        rne = (((hne - hc) * dne) >> frp2); //  * 11) >> 11);
        rse = (((hse - hc) * dse) >> frp2); // * 11) >> 11);
        rnw = (((hnw - hc) * dnw) >> frp2); //* 11) >> 11);
        rsw = (((hsw - hc) * dsw) >> frp2); // * 11) >> 11);

        int max_liquid_available = dc >> 3;

        if (re > (de >> 3)) {
          re = de >> 3;
        }

        if (-re > (max_liquid_available)) {
          re = -max_liquid_available;
        }

        if (rw > (dw >> 3)) {
          rw = dw >> 3;
        }

        if (-rw > (max_liquid_available)) {
          rw = -max_liquid_available;
        }

        if (rn > (dn >> 3)) {
          rn = dn >> 3;
        }

        if (-rn > (max_liquid_available)) {
          rn = -max_liquid_available;
        }

        if (rs > (ds >> 3)) {
          rs = ds >> 3;
        }

        if (-rs > (max_liquid_available)) {
          rs = -max_liquid_available;
        }

        if (rne > (dne >> 3)) {
          rne = dne >> 3;
        }

        if (-rne > (max_liquid_available)) {
          rne = -max_liquid_available;
        }

        if (rse > (dse >> 3)) {
          rse = dse >> 3;
        }

        if (-rse > (max_liquid_available)) {
          rse = -max_liquid_available;
        }

        if (rnw > (dnw >> 3)) {
          rnw = dnw >> 3;
        }

        if (-rnw > (max_liquid_available)) {
          rnw = -max_liquid_available;
        }

        if (rsw > (dsw >> 3)) {
          rsw = dsw >> 3;
        }

        if (-rsw > (max_liquid_available)) {
          rsw = -max_liquid_available;
        }

        // rain...
        tfu = (((dc << 3) + rn + rs + re + rw + rne + rse + rnw + rsw) >> 3) - seep_rate;
        if (hc > upper_threshold) {
          tfu += t.rnd.nextInt(add_rate); // rain on high ground...
        }

        // tfv = (((hc << 3) + hn + hs + hne + hnw + hse + hsw + he + hw) >> 4) + 0x40 - (tfu >> 8);

        if (hc < lower_threshold) {
          tfu -= 0x4000; // seep into low ground...
        }

        tfu = (tfu < 0) ? 0 : ((tfu > 0xFFFF) ? 0xFFFF : tfu);

        // diffusion...
        // of some minor interest...
        /*
           int min = 0xFFFFFF;
           if (hn < min) {
              min = hn;
           }
        
           if (hs < min) {
              min = hs;
           }
        
           if (he < min) {
              min = he;
           }
        
           if (hw < min) {
              min = hw;
           }
        
           tfv = ((hc > (min + 0x1000)) ? (min + 0x1000) : hc) + ground_rising;
        */
        // min = (hn < hs) ? (hn < he) ? (hn < hw) ? ((hn < he) ? hn : he) : ((hw < he) ? hw : he)

          tfv = (((hc * 52) + (hn * 51) + (hs * 51) + (he * 51) + (hw * 51)) >> 8) //  + hne + hnw + hse + hsw) >> 3) // ground diffusion
  +ground_rising; // ground rising up 
        //tfv = ((hn + hs + he + hw + hne + hnw + hse + hsw) >> 3) // Moore ground diffusion...

        tfv -= // etching_intensity
         (((rnw > 0) ? rnw : -rnw) + ((rne > 0) ? rne : -rne) + ((rsw > 0) ? rsw : -rsw) + ((rse > 0) ? rse : -rse) + ((rn > 0) ? rn : -rn) + ((rs > 0) ? rs : -rs) + ((re > 0) ? re : -re) + ((rw > 0) ? rw : -rw)) >> log_resistance;

        // maximum and minimum check...

        tfv = (tfv < 0) ? 0 : ((tfv > 0xFFFF) ? 0xFFFF : tfv);

        t.tfu_max = (tfu > t.tfu_max) ? tfu : t.tfu_max;
        t.tfu_min = (tfu < t.tfu_min) ? tfu : t.tfu_min;
        t.tfv_max = (tfv > t.tfv_max) ? tfv : t.tfv_max;
        t.tfv_min = (tfv < t.tfv_min) ? tfv : t.tfv_min;

        return tfu | (tfv << 16);
      }
    };

  }

  public static void animate(Texture _t, int bn) {
    t = _t;

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

    CAProcess2D.generalMooreOperation(t.c_a2D[bn], mo);

    // CAProcess2D.generalMooreOperation(t.c_a2D[0], intermo);

    // CAProcess2D.generalHexOperation(t.c_a, ho);
    // CAProcess2D.generalVNOperation(t.c_a, vno);
    // t.c_a.r.randomise();

    // for (int i = 0; i < t.height; i++) { 
    //t.c_a.r.v[t.c_a.r.offset_x][i] = 0xFFFFFFFF;
    // }
    // CAProcess2D.generalVNOperation(t.c_a, vno2);
  }

  public static void setFlowRate(int fa) {
    flow_rate = fa;
  }

  public static int getFlowRate() {
    return flow_rate;
  }

  public static void setAddRate(int fa) {
    //Log.log("add_rate:" + add_rate);
    add_rate = fa;
    //Log.log("add_rate:" + add_rate);
  }

  public static int getAddRate() {
    return add_rate;
  }

  public static void setSeepRate(int fa) {
    seep_rate = fa;
  }

  public  static int getSeepRate() {
    return seep_rate;
  }

  public static int getUpperThreshold() {
    return upper_threshold;
  }

  public static void setUpperThreshold(int fa) {
    upper_threshold = fa;
  }

  public static int getLowerThreshold() {
    return lower_threshold;
  }

  public static void setLowerThreshold(int fa) {
    lower_threshold = fa;
  }

  public static int getLogResistance() {
    return log_resistance;
  }

  public static void setLogResistance(int fa) {
    log_resistance = fa;
  }

  public static int getGroundRising() {
    return ground_rising;
  }

  public static void setGroundRising(int fa) {
    //Log.log("ground_rising:" + ground_rising);
    ground_rising = fa;
    //Log.log("ground_rising:" + ground_rising);
  }
}
