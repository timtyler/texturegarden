package com.texturegarden.effects.ca;

import java.util.Random;

import com.texturegarden.arrays.ComplexArray2D;
import com.texturegarden.arrays.SimpleArray2D;
import com.texturegarden.utilities.JUR;

/**
 * CAProcess2D - Tim Tyler 2000-2003
 */

public class CAProcess2D {
  static JUR rnd = new JUR();

  // totalistic...
  public static void rugVN(ComplexArray2D c_a, int i) {
    VNOperation vn_op = new VNOperation() {
      int increment;
      public void init(int inc) {
        increment = inc;
      }

      public int process() {
        return (((n + s + e + w) >> 2) + increment) & 0xFFFF;
      }
    };

    vn_op.init(i);
    generalVNOperation(c_a, vn_op);
    c_a.r.compensationForCA();
  }

  public static void rugVNSlow(ComplexArray2D c_a, int i, int f) {
    VNOperation vn_op = new VNOperation() {
      int increment;
      int shift;
      int c_mult;
      public void init(int inc, int z) {
        increment = inc;
        shift = z;
        c_mult = (1 << shift) - 4;
      }

      public int process() {
        return ((((c * c_mult) + n + s + e + w) >> shift) + increment) & 0xFFFF;
      }
    };

    vn_op.init(i, f);
    generalVNOperation(c_a, vn_op);
    c_a.r.compensationForCA();
  }

  /// boringly unoriginal...
  static void rugMoore(ComplexArray2D c_a, int i) {
    MooreOperation moore_op = new MooreOperation() {
      int increment;
      public void init(int inc) {
        increment = inc;
      }

      public int process() {
        return (((n + s + e + w + nw + ne + sw + se) >> 3) + increment) & 0xFFFF;
      }
    };

    moore_op.init(i);
    generalMooreOperation(c_a, moore_op);
    c_a.r.compensationForCA();
  }

  static void ridgeFinderVN(ComplexArray2D c_a) {
    VNOperation vn_op = new VNOperation() {
        public int process() {// int c, int n, int s, int e, int w) {
  return ((c > n) ? 16383 : 0) + ((c > s) ? 16383 : 0) + ((c > e) ? 16383 : 0) + ((c > w) ? 16383 : 0);
      }
    };

    generalVNOperation(c_a, vn_op);

    /*
       int c,n,s,w,e,vel,vc;
       int count;
       SimpleArray2D _r = c_a.r;
       SimpleArray2D _i = c_a.i;
    
       for (int x = 0; x < _r.width; x++) {
          for (int y = 0; y < _r.height; y++) {
             n = _r.safeRead(x    , y - 1);
             s = _r.safeRead(x    , y + 1);
             e = _r.safeRead(x + 1, y    );
             w = _r.safeRead(x - 1, y    );
             c = _r.safeRead(x    , y    );
          
             count = 0;
             if (c > n) {
                count += 16383;
             }
             if (c > s) {
                count += 16383;
             }
             if (c > e) {
                count += 16383;
             }
             if (c > w) {
                count += 16383;
             }
          
             _i.v[x][y] = count;
          }
       }
    
       c_a.swapRealAndImaginary();
    	*/
  }

  public static void ridgeFinderMoore(ComplexArray2D c_a) {
    MooreOperation moore_op = new MooreOperation() {
      int NMX = 0xC000;
      int NMN = 0x4000;

      public int process() {
        if ((c > NMX) || (n > NMX) || (s > NMX) || (e > NMX) || (w > NMX) || (nw > NMX) || (ne > NMX) || (sw > NMX) || (se > NMX)) {

          int cc = ((c < NMN) ? c + 0x10000 : c);

          return ((cc > ((n < NMN) ? n + 0x10000 : n)) ? 9598 : 0) + ((cc > ((s < NMN) ? s + 0x10000 : s)) ? 9598 : 0) + ((cc > ((e < NMN) ? e + 0x10000 : e)) ? 9598 : 0) + ((cc > ((w < NMN) ? w + 0x10000 : w)) ? 9598 : 0) + ((cc > ((nw < NMN) ? nw + 0x10000 : nw)) ? 6786 : 0) + ((cc > ((ne < NMN) ? ne + 0x10000 : ne)) ? 6786 : 0) + ((cc > ((sw < NMN) ? sw + 0x10000 : sw)) ? 6786 : 0) + ((cc > ((se < NMN) ? se + 0x10000 : se)) ? 6785 : 0);
        }

        return ((c > n) ? 9598 : 0) + ((c > s) ? 9598 : 0) + ((c > e) ? 9598 : 0) + ((c > w) ? 9598 : 0) + ((c > ne) ? 6786 : 0) + ((c > nw) ? 6786 : 0) + ((c > se) ? 6786 : 0) + ((c > sw) ? 6785 : 0);
      }
    };

    generalMooreOperation(c_a, moore_op);
  }

  // /slightly/ wasteful use of safeRead :-(
  public static void generalMooreOperation(ComplexArray2D c_a, MooreOperation moore_op) {
    SimpleArray2D _r = c_a.r;

    int[] tempx0 = new int[_r.height];
    int[] tempy0 = new int[_r.width];
    int[] tempx1 = new int[_r.height];
    int[] tempy1 = new int[_r.width];

    moore_op.init();

    // x = 0 case...
    for (int y = 0; y < _r.height; y++) {
      moore_op.c = _r.safeRead(1, y + 1);
      moore_op.n = _r.safeRead(1, y);
      moore_op.s = _r.safeRead(1, y + 2);
      moore_op.e = _r.safeRead(2, y + 1);
      moore_op.w = _r.safeRead(0, y + 1);
      moore_op.nw = _r.safeRead(0, y);
      moore_op.ne = _r.safeRead(2, y);
      moore_op.sw = _r.safeRead(0, y + 2);
      moore_op.se = _r.safeRead(2, y + 2);

      tempx0[y] = moore_op.process();
    }

    // y = 0 case...
    for (int x = 0; x < _r.width; x++) {
      moore_op.c = _r.safeRead(x + 1, 1);
      moore_op.n = _r.safeRead(x + 1, 0);
      moore_op.s = _r.safeRead(x + 1, 2);
      moore_op.e = _r.safeRead(x + 2, 1);
      moore_op.w = _r.safeRead(x, 1);
      moore_op.nw = _r.safeRead(x, 0);
      moore_op.ne = _r.safeRead(x + 2, 0);
      moore_op.sw = _r.safeRead(x, 2);
      moore_op.se = _r.safeRead(x + 2, 2);

      tempy0[x] = moore_op.process();
    }

    // x = 1 case...
    for (int y = 0; y < _r.height; y++) {
      moore_op.c = _r.safeRead(2, y + 1);
      moore_op.n = _r.safeRead(2, y);
      moore_op.s = _r.safeRead(2, y + 2);
      moore_op.e = _r.safeRead(3, y + 1);
      moore_op.w = _r.safeRead(1, y + 1);
      moore_op.nw = _r.safeRead(1, y);
      moore_op.ne = _r.safeRead(3, y);
      moore_op.sw = _r.safeRead(1, y + 2);
      moore_op.se = _r.safeRead(3, y + 2);

      tempx1[y] = moore_op.process();
    }

    // y = 1 case...
    for (int x = 0; x < _r.width; x++) {
      moore_op.c = _r.safeRead(x + 1, 2);
      moore_op.n = _r.safeRead(x + 1, 1);
      moore_op.s = _r.safeRead(x + 1, 3);
      moore_op.e = _r.safeRead(x + 2, 2);
      moore_op.w = _r.safeRead(x, 2);
      moore_op.nw = _r.safeRead(x, 1);
      moore_op.ne = _r.safeRead(x + 2, 1);
      moore_op.sw = _r.safeRead(x, 3);
      moore_op.se = _r.safeRead(x + 2, 3);

      tempy1[x] = moore_op.process();
    }

    int x_max = _r.width - 2;
    int y_max = _r.height - 2;

    // 4 x points at SE corner...
    moore_op.c = _r.safeRead(x_max + 1, y_max + 1);
    moore_op.n = _r.safeRead(x_max + 1, y_max);
    moore_op.s = _r.safeRead(x_max + 1, y_max + 2);
    moore_op.e = _r.safeRead(x_max + 2, y_max + 1);
    moore_op.w = _r.safeRead(x_max, y_max + 1);
    moore_op.nw = _r.safeRead(x_max, y_max);
    moore_op.ne = _r.safeRead(x_max + 2, y_max);
    moore_op.sw = _r.safeRead(x_max, y_max + 2);
    moore_op.se = _r.safeRead(x_max + 2, y_max + 2);
    int temp_00 = moore_op.process();

    x_max++;
    moore_op.c = _r.safeRead(x_max + 1, y_max + 1);
    moore_op.n = _r.safeRead(x_max + 1, y_max);
    moore_op.s = _r.safeRead(x_max + 1, y_max + 2);
    moore_op.e = _r.safeRead(x_max + 2, y_max + 1);
    moore_op.w = _r.safeRead(x_max, y_max + 1);
    moore_op.nw = _r.safeRead(x_max, y_max);
    moore_op.ne = _r.safeRead(x_max + 2, y_max);
    moore_op.sw = _r.safeRead(x_max, y_max + 2);
    moore_op.se = _r.safeRead(x_max + 2, y_max + 2);
    int temp_10 = moore_op.process();
    y_max++;
    moore_op.c = _r.safeRead(x_max + 1, y_max + 1);
    moore_op.n = _r.safeRead(x_max + 1, y_max);
    moore_op.s = _r.safeRead(x_max + 1, y_max + 2);
    moore_op.e = _r.safeRead(x_max + 2, y_max + 1);
    moore_op.w = _r.safeRead(x_max, y_max + 1);
    moore_op.nw = _r.safeRead(x_max, y_max);
    moore_op.ne = _r.safeRead(x_max + 2, y_max);
    moore_op.sw = _r.safeRead(x_max, y_max + 2);
    moore_op.se = _r.safeRead(x_max + 2, y_max + 2);
    int temp_11 = moore_op.process();
    x_max--;
    moore_op.c = _r.safeRead(x_max + 1, y_max + 1);
    moore_op.n = _r.safeRead(x_max + 1, y_max);
    moore_op.s = _r.safeRead(x_max + 1, y_max + 2);
    moore_op.e = _r.safeRead(x_max + 2, y_max + 1);
    moore_op.w = _r.safeRead(x_max, y_max + 1);
    moore_op.nw = _r.safeRead(x_max, y_max);
    moore_op.ne = _r.safeRead(x_max + 2, y_max);
    moore_op.sw = _r.safeRead(x_max, y_max + 2);
    moore_op.se = _r.safeRead(x_max + 2, y_max + 2);
    int temp_01 = moore_op.process();
    y_max--;

    for (int y = 2; y < y_max; y++) {
      moore_op.c = _r.v[2 + 1][y + 1];
      moore_op.n = _r.v[2 + 1][y];
      moore_op.s = _r.v[2 + 1][y + 2];
      moore_op.e = _r.v[2 + 2][y + 1];
      moore_op.w = _r.v[2][y + 1];
      moore_op.nw = _r.v[2][y];
      moore_op.ne = _r.v[2 + 2][y];
      moore_op.sw = _r.v[2][y + 2];
      moore_op.se = _r.v[2 + 2][y + 2];

      _r.v[2][y] = moore_op.process();

      for (int x = 3; x < x_max; x++) {
        moore_op.nw = moore_op.n;
        moore_op.w = moore_op.c;
        moore_op.sw = moore_op.s;

        moore_op.n = moore_op.ne;
        moore_op.c = moore_op.e;
        moore_op.s = moore_op.se;

        moore_op.ne = _r.v[x + 2][y];
        moore_op.e = _r.v[x + 2][y + 1];
        moore_op.se = _r.v[x + 2][y + 2];

        _r.v[x][y] = moore_op.process();
      }
    }

    // 4 x ends...
    for (int y = 2; y < _r.height - 2; y++) {
      moore_op.c = _r.safeRead(x_max + 1, y + 1);
      moore_op.n = _r.safeRead(x_max + 1, y);
      moore_op.s = _r.safeRead(x_max + 1, y + 2);
      moore_op.e = _r.safeRead(x_max + 2, y + 1);
      moore_op.w = _r.safeRead(x_max, y + 1);
      moore_op.nw = _r.safeRead(x_max, y);
      moore_op.ne = _r.safeRead(x_max + 2, y);
      moore_op.sw = _r.safeRead(x_max, y + 2);
      moore_op.se = _r.safeRead(x_max + 2, y + 2);

      _r.v[x_max][y] = moore_op.process();
    }

    x_max++;
    for (int y = 2; y < _r.height - 2; y++) {
      moore_op.c = _r.safeRead(x_max + 1, y + 1);
      moore_op.n = _r.safeRead(x_max + 1, y);
      moore_op.s = _r.safeRead(x_max + 1, y + 2);
      moore_op.e = _r.safeRead(x_max + 2, y + 1);
      moore_op.w = _r.safeRead(x_max, y + 1);
      moore_op.nw = _r.safeRead(x_max, y);
      moore_op.ne = _r.safeRead(x_max + 2, y);
      moore_op.sw = _r.safeRead(x_max, y + 2);
      moore_op.se = _r.safeRead(x_max + 2, y + 2);

      _r.v[x_max][y] = moore_op.process();
    }

    for (int x = 2; x < _r.width - 2; x++) {
      moore_op.c = _r.safeRead(x + 1, y_max + 1);
      moore_op.n = _r.safeRead(x + 1, y_max);
      moore_op.s = _r.safeRead(x + 1, y_max + 2);
      moore_op.e = _r.safeRead(x + 2, y_max + 1);
      moore_op.w = _r.safeRead(x, y_max + 1);
      moore_op.nw = _r.safeRead(x, y_max);
      moore_op.ne = _r.safeRead(x + 2, y_max);
      moore_op.sw = _r.safeRead(x, y_max + 2);
      moore_op.se = _r.safeRead(x + 2, y_max + 2);

      _r.v[x][y_max] = moore_op.process();
    }

    y_max++;
    for (int x = 2; x < _r.width - 2; x++) {
      moore_op.c = _r.safeRead(x + 1, y_max + 1);
      moore_op.n = _r.safeRead(x + 1, y_max);
      moore_op.s = _r.safeRead(x + 1, y_max + 2);
      moore_op.e = _r.safeRead(x + 2, y_max + 1);
      moore_op.w = _r.safeRead(x, y_max + 1);
      moore_op.nw = _r.safeRead(x, y_max);
      moore_op.ne = _r.safeRead(x + 2, y_max);
      moore_op.sw = _r.safeRead(x, y_max + 2);
      moore_op.se = _r.safeRead(x + 2, y_max + 2);

      _r.v[x][y_max] = moore_op.process();
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

  //Random rnd = new Random();
  // /slightly/ wasteful use of safeRead :-(
  public static void generalVNOperation(ComplexArray2D c_a, VNOperation vno) {
    SimpleArray2D _r = c_a.r;

    int[] tempx0 = new int[_r.height];
    int[] tempy0 = new int[_r.width];
    int[] tempx1 = new int[_r.height];
    int[] tempy1 = new int[_r.width];

    vno.init();

    // x = 0 case...
    for (int y = 0; y < _r.height; y++) {
      vno.c = _r.safeRead(1, y + 1);
      vno.n = _r.safeRead(1, y);
      vno.s = _r.safeRead(1, y + 2);
      vno.e = _r.safeRead(2, y + 1);
      vno.w = _r.safeRead(0, y + 1);

      tempx0[y] = vno.process();
    }

    // y = 0 case...
    for (int x = 0; x < _r.width; x++) {
      vno.c = _r.safeRead(x + 1, 1);
      vno.n = _r.safeRead(x + 1, 0);
      vno.s = _r.safeRead(x + 1, 2);
      vno.e = _r.safeRead(x + 2, 1);
      vno.w = _r.safeRead(x, 1);

      tempy0[x] = vno.process();
    }

    // x = 1 case...
    for (int y = 0; y < _r.height; y++) {
      vno.c = _r.safeRead(2, y + 1);
      vno.n = _r.safeRead(2, y);
      vno.s = _r.safeRead(2, y + 2);
      vno.e = _r.safeRead(3, y + 1);
      vno.w = _r.safeRead(1, y + 1);

      tempx1[y] = vno.process();
    }

    // y = 1 case...
    for (int x = 0; x < _r.width; x++) {
      vno.c = _r.safeRead(x + 1, 2);
      vno.n = _r.safeRead(x + 1, 1);
      vno.s = _r.safeRead(x + 1, 3);
      vno.e = _r.safeRead(x + 2, 2);
      vno.w = _r.safeRead(x, 2);

      tempy1[x] = vno.process();
    }

    int x_max = _r.width - 2;
    int y_max = _r.height - 2;

    // 4 x points at SE corner...
    vno.c = _r.safeRead(x_max + 1, y_max + 1);
    vno.n = _r.safeRead(x_max + 1, y_max);
    vno.s = _r.safeRead(x_max + 1, y_max + 2);
    vno.e = _r.safeRead(x_max + 2, y_max + 1);
    vno.w = _r.safeRead(x_max, y_max + 1);
    int temp_00 = vno.process();
    x_max++;
    vno.c = _r.safeRead(x_max + 1, y_max + 1);
    vno.n = _r.safeRead(x_max + 1, y_max);
    vno.s = _r.safeRead(x_max + 1, y_max + 2);
    vno.e = _r.safeRead(x_max + 2, y_max + 1);
    vno.w = _r.safeRead(x_max, y_max + 1);
    int temp_10 = vno.process();
    y_max++;
    vno.c = _r.safeRead(x_max + 1, y_max + 1);
    vno.n = _r.safeRead(x_max + 1, y_max);
    vno.s = _r.safeRead(x_max + 1, y_max + 2);
    vno.e = _r.safeRead(x_max + 2, y_max + 1);
    vno.w = _r.safeRead(x_max, y_max + 1);
    int temp_11 = vno.process();
    x_max--;
    vno.c = _r.safeRead(x_max + 1, y_max + 1);
    vno.n = _r.safeRead(x_max + 1, y_max);
    vno.s = _r.safeRead(x_max + 1, y_max + 2);
    vno.e = _r.safeRead(x_max + 2, y_max + 1);
    vno.w = _r.safeRead(x_max, y_max + 1);
    int temp_01 = vno.process();
    y_max--;

    for (int y = 2; y < y_max; y++) {
      vno.c = _r.v[2 + 1][y + 1];
      vno.n = _r.v[2 + 1][y];
      vno.s = _r.v[2 + 1][y + 2];
      vno.e = _r.v[2 + 2][y + 1];
      vno.w = _r.v[2][y + 1];

      _r.v[2][y] = vno.process();

      for (int x = 3; x < x_max; x++) {
        vno.w = vno.c;
        vno.c = vno.e;
        vno.n = _r.v[x + 1][y];
        vno.s = _r.v[x + 1][y + 2];
        vno.e = _r.v[x + 2][y + 1];

        //if (rnd.nextInt() > 0x10000) {
          _r.v[x][y] = vno.process();
        //}
      }
    }

    // 4 x ends...
    for (int y = 2; y < _r.height - 2; y++) {
      vno.c = _r.safeRead(x_max + 1, y + 1);
      vno.n = _r.safeRead(x_max + 1, y);
      vno.s = _r.safeRead(x_max + 1, y + 2);
      vno.e = _r.safeRead(x_max + 2, y + 1);
      vno.w = _r.safeRead(x_max, y + 1);

      _r.v[x_max][y] = vno.process();
    }

    x_max++;
    for (int y = 2; y < _r.height - 2; y++) {
      vno.c = _r.safeRead(x_max + 1, y + 1);
      vno.n = _r.safeRead(x_max + 1, y);
      vno.s = _r.safeRead(x_max + 1, y + 2);
      vno.e = _r.safeRead(x_max + 2, y + 1);
      vno.w = _r.safeRead(x_max, y + 1);

      _r.v[x_max][y] = vno.process();
    }

    for (int x = 2; x < _r.width - 2; x++) {
      vno.c = _r.safeRead(x + 1, y_max + 1);
      vno.n = _r.safeRead(x + 1, y_max);
      vno.s = _r.safeRead(x + 1, y_max + 2);
      vno.e = _r.safeRead(x + 2, y_max + 1);
      vno.w = _r.safeRead(x, y_max + 1);

      _r.v[x][y_max] = vno.process();
    }

    y_max++;
    for (int x = 2; x < _r.width - 2; x++) {
      vno.c = _r.safeRead(x + 1, y_max + 1);
      vno.n = _r.safeRead(x + 1, y_max);
      vno.s = _r.safeRead(x + 1, y_max + 2);
      vno.e = _r.safeRead(x + 2, y_max + 1);
      vno.w = _r.safeRead(x, y_max + 1);

      _r.v[x][y_max] = vno.process();
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

  // /slightly/ wasteful use of safeRead :-(

  static void generalHexOperation(ComplexArray2D c_a, MooreOperation moore_op) {
    SimpleArray2D _r = c_a.r;

    int yob1 = _r.offset_y & 1; // ?!?!?!!?!?!?!??!?!?!?!??! Old code !!!!

    //Log.log("" + yob1);

    int[] tempx0 = new int[_r.height];
    int[] tempy0 = new int[_r.width];
    int[] tempx1 = new int[_r.height];
    int[] tempy1 = new int[_r.width];

    // uses c, w, e, nw, ne, sw, se as neighbourhood...

    moore_op.init();

    // x = 0 case...
    for (int y = 0; y < _r.height; y++) {
      if ((y & 1) == yob1) { // /
        moore_op.nw = _r.safeRead(1, y);
        moore_op.ne = _r.safeRead(2, y); // same
        moore_op.sw = _r.safeRead(0, y + 2); // same
        moore_op.se = _r.safeRead(1, y + 2);
      } else {
        moore_op.nw = _r.safeRead(0, y); // same
        moore_op.ne = _r.safeRead(1, y);
        moore_op.sw = _r.safeRead(1, y + 2);
        moore_op.se = _r.safeRead(2, y + 2); // same
      }

      moore_op.w = _r.safeRead(0, y + 1);
      moore_op.c = _r.safeRead(1, y + 1);
      moore_op.e = _r.safeRead(2, y + 1);

      tempx0[y] = moore_op.process();
    }

    // y = 0 case...
    for (int x = 0; x < _r.width; x++) {
      if (0 == yob1) { // /
        moore_op.nw = _r.safeRead(x + 1, 0); // (n)
        moore_op.ne = _r.safeRead(x + 2, 0); // (ne) same 
        moore_op.sw = _r.safeRead(x + 0, 2); // (sw) same
        moore_op.se = _r.safeRead(x + 1, 2); // (s)
      } else {
        moore_op.nw = _r.safeRead(x, 0); // (nw) same
        moore_op.ne = _r.safeRead(x + 1, 0); // (n)
        moore_op.sw = _r.safeRead(x + 1, 2); // (s)
        moore_op.se = _r.safeRead(x + 2, 2); // (se) same
      }

      moore_op.w = _r.safeRead(x, 1);
      moore_op.c = _r.safeRead(x + 1, 1);
      moore_op.e = _r.safeRead(x + 2, 1);

      tempy0[x] = moore_op.process();
    }

    // x = 1 case...
    for (int y = 0; y < _r.height; y++) {
      if ((y & 1) == yob1) { // /
        moore_op.nw = _r.safeRead(2, y); // (n)
        moore_op.ne = _r.safeRead(3, y); // (ne) same 
        moore_op.sw = _r.safeRead(1, y + 2); // (sw) same
        moore_op.se = _r.safeRead(2, y + 2); // (s)
      } else {
        moore_op.nw = _r.safeRead(1, y); // (nw) same
        moore_op.ne = _r.safeRead(2, y); // (n)
        moore_op.sw = _r.safeRead(2, y + 2); // (s)
        moore_op.se = _r.safeRead(3, y + 2); // (se) same
      }

      moore_op.w = _r.safeRead(1, y + 1);
      moore_op.c = _r.safeRead(2, y + 1);
      moore_op.e = _r.safeRead(3, y + 1);

      tempx1[y] = moore_op.process();
    }

    // y = 1 case...
    for (int x = 0; x < _r.width; x++) {
      if (1 == yob1) { // /
        moore_op.nw = _r.safeRead(x + 1, 1); // (n)
        moore_op.ne = _r.safeRead(x + 2, 1); // (ne) same 
        moore_op.sw = _r.safeRead(x + 0, 3); // (sw) same
        moore_op.se = _r.safeRead(x + 1, 3); // (s)
      } else {
        moore_op.nw = _r.safeRead(x, 1); // (nw) same
        moore_op.ne = _r.safeRead(x + 1, 1); // (n)
        moore_op.sw = _r.safeRead(x + 1, 3); // (s)
        moore_op.se = _r.safeRead(x + 2, 3); // (se) same
      }

      moore_op.w = _r.safeRead(x, 2);
      moore_op.c = _r.safeRead(x + 1, 2);
      moore_op.e = _r.safeRead(x + 2, 2);

      tempy1[x] = moore_op.process();
    }

    int x_max = _r.width - 2;
    int y_max = _r.height - 2;

    // 4 x points at SE corner...
    if ((y_max & 1) == yob1) { // /
      moore_op.nw = _r.safeRead(x_max + 1, y_max); // (n)
      moore_op.ne = _r.safeRead(x_max + 2, y_max); // (ne) same 
      moore_op.sw = _r.safeRead(x_max, y_max + 2); // (sw) same
      moore_op.se = _r.safeRead(x_max + 1, y_max + 2); // (s)
    } else {
      moore_op.nw = _r.safeRead(x_max, y_max); // (nw) same
      moore_op.ne = _r.safeRead(x_max + 1, y_max); // (n)
      moore_op.sw = _r.safeRead(x_max + 1, y_max + 2); // (s)
      moore_op.se = _r.safeRead(x_max + 2, y_max + 2); // (se) same
    }

    moore_op.c = _r.safeRead(x_max + 1, y_max + 1);
    moore_op.e = _r.safeRead(x_max + 2, y_max + 1);
    moore_op.w = _r.safeRead(x_max, y_max + 1);
    int temp_00 = moore_op.process();

    x_max++;
    if ((y_max & 1) == yob1) { // /
      moore_op.nw = _r.safeRead(x_max + 1, y_max); // (n)
      moore_op.ne = _r.safeRead(x_max + 2, y_max); // (ne) same 
      moore_op.sw = _r.safeRead(x_max, y_max + 2); // (sw) same
      moore_op.se = _r.safeRead(x_max + 1, y_max + 2); // (s)
    } else {
      moore_op.nw = _r.safeRead(x_max, y_max); // (nw) same
      moore_op.ne = _r.safeRead(x_max + 1, y_max); // (n)
      moore_op.sw = _r.safeRead(x_max + 1, y_max + 2); // (s)
      moore_op.se = _r.safeRead(x_max + 2, y_max + 2); // (se) same
    }

    moore_op.c = _r.safeRead(x_max + 1, y_max + 1);
    moore_op.e = _r.safeRead(x_max + 2, y_max + 1);
    moore_op.w = _r.safeRead(x_max, y_max + 1);
    int temp_10 = moore_op.process();
    y_max++;
    if ((y_max & 1) == yob1) { // /
      moore_op.nw = _r.safeRead(x_max + 1, y_max); // (n)
      moore_op.ne = _r.safeRead(x_max + 2, y_max); // (ne) same 
      moore_op.sw = _r.safeRead(x_max, y_max + 2); // (sw) same
      moore_op.se = _r.safeRead(x_max + 1, y_max + 2); // (s)
    } else {
      moore_op.nw = _r.safeRead(x_max, y_max); // (nw) same
      moore_op.ne = _r.safeRead(x_max + 1, y_max); // (n)
      moore_op.sw = _r.safeRead(x_max + 1, y_max + 2); // (s)
      moore_op.se = _r.safeRead(x_max + 2, y_max + 2); // (se) same
    }

    moore_op.c = _r.safeRead(x_max + 1, y_max + 1);
    moore_op.e = _r.safeRead(x_max + 2, y_max + 1);
    moore_op.w = _r.safeRead(x_max, y_max + 1);
    int temp_11 = moore_op.process();
    x_max--;
    if ((y_max & 1) == yob1) { // /
      moore_op.nw = _r.safeRead(x_max + 1, y_max); // (n)
      moore_op.ne = _r.safeRead(x_max + 2, y_max); // (ne) same 
      moore_op.sw = _r.safeRead(x_max, y_max + 2); // (sw) same
      moore_op.se = _r.safeRead(x_max + 1, y_max + 2); // (s)
    } else {
      moore_op.nw = _r.safeRead(x_max, y_max); // (nw) same
      moore_op.ne = _r.safeRead(x_max + 1, y_max); // (n)
      moore_op.sw = _r.safeRead(x_max + 1, y_max + 2); // (s)
      moore_op.se = _r.safeRead(x_max + 2, y_max + 2); // (se) same
    }

    moore_op.c = _r.safeRead(x_max + 1, y_max + 1);
    moore_op.e = _r.safeRead(x_max + 2, y_max + 1);
    moore_op.w = _r.safeRead(x_max, y_max + 1);
    int temp_01 = moore_op.process();
    y_max--;

    for (int y = 2; y < y_max; y++) {
      if ((y & 1) == yob1) { // /
        moore_op.nw = _r.v[2 + 1][y]; // (n)
        moore_op.ne = _r.v[2 + 2][y]; // (ne) same 
        moore_op.sw = _r.v[2][y + 2]; // (sw) same
        moore_op.se = _r.v[2 + 1][y + 2]; // (s)
      } else {
        moore_op.nw = _r.v[2][y]; // (nw) same
        moore_op.ne = _r.v[2 + 1][y]; // (n)
        moore_op.sw = _r.v[2 + 1][y + 2]; // (s)
        moore_op.se = _r.v[2 + 2][y + 2]; // (se) same
      }

      moore_op.c = _r.v[2 + 1][y + 1];
      moore_op.e = _r.v[2 + 2][y + 1];
      moore_op.w = _r.v[2][y + 1];

      _r.v[2][y] = moore_op.process();

      for (int x = 3; x < x_max; x++) {
        moore_op.nw = moore_op.ne;
        moore_op.w = moore_op.c;
        moore_op.sw = moore_op.se;

        moore_op.c = moore_op.e;

        moore_op.e = _r.v[x + 2][y + 1];

        if ((y & 1) == yob1) { // /
          moore_op.ne = _r.v[x + 2][y]; // (ne) same 
          moore_op.se = _r.v[x + 1][y + 2]; // (s)
        } else {
          moore_op.ne = _r.v[x + 1][y]; // (n)
          moore_op.se = _r.v[x + 2][y + 2]; // (se) same
        }

        _r.v[x][y] = moore_op.process();
      }
    }

    // 4 x ends...
    for (int y = 2; y < _r.height - 2; y++) {
      if ((y & 1) == yob1) { // /
        moore_op.nw = _r.safeRead(x_max + 1, y); // (n)
        moore_op.ne = _r.safeRead(x_max + 2, y); // (ne) same 
        moore_op.sw = _r.safeRead(x_max, y + 2); // (sw) same
        moore_op.se = _r.safeRead(x_max + 1, y + 2); // (s)
      } else {
        moore_op.nw = _r.safeRead(x_max, y); // (nw) same
        moore_op.ne = _r.safeRead(x_max + 1, y); // (n)
        moore_op.sw = _r.safeRead(x_max + 1, y + 2); // (s)
        moore_op.se = _r.safeRead(x_max + 2, y + 2); // (se) same
      }

      moore_op.c = _r.safeRead(x_max + 1, y + 1);
      moore_op.e = _r.safeRead(x_max + 2, y + 1);
      moore_op.w = _r.safeRead(x_max, y + 1);

      _r.v[x_max][y] = moore_op.process();
    }

    x_max++;
    for (int y = 2; y < _r.height - 2; y++) {
      if ((y & 1) == yob1) { // /
        moore_op.nw = _r.safeRead(x_max + 1, y); // (n)
        moore_op.ne = _r.safeRead(x_max + 2, y); // (ne) same 
        moore_op.sw = _r.safeRead(x_max, y + 2); // (sw) same
        moore_op.se = _r.safeRead(x_max + 1, y + 2); // (s)
      } else {
        moore_op.nw = _r.safeRead(x_max, y); // (nw) same
        moore_op.ne = _r.safeRead(x_max + 1, y); // (n)
        moore_op.sw = _r.safeRead(x_max + 1, y + 2); // (s)
        moore_op.se = _r.safeRead(x_max + 2, y + 2); // (se) same
      }

      moore_op.c = _r.safeRead(x_max + 1, y + 1);
      moore_op.e = _r.safeRead(x_max + 2, y + 1);
      moore_op.w = _r.safeRead(x_max, y + 1);

      _r.v[x_max][y] = moore_op.process();
    }

    for (int x = 2; x < _r.width - 2; x++) {
      if ((y_max & 1) == yob1) { // /
        moore_op.nw = _r.safeRead(x + 1, y_max); // (n)
        moore_op.ne = _r.safeRead(x + 2, y_max); // (ne) same 
        moore_op.sw = _r.safeRead(x, y_max + 2); // (sw) same
        moore_op.se = _r.safeRead(x + 1, y_max + 2); // (s)
      } else {
        moore_op.nw = _r.safeRead(x, y_max); // (nw) same
        moore_op.ne = _r.safeRead(x + 1, y_max); // (n)
        moore_op.sw = _r.safeRead(x + 1, y_max + 2); // (s)
        moore_op.se = _r.safeRead(x + 2, y_max + 2); // (se) same
      }

      moore_op.w = _r.safeRead(x, y_max + 1);
      moore_op.c = _r.safeRead(x + 1, y_max + 1);
      moore_op.e = _r.safeRead(x + 2, y_max + 1);

      _r.v[x][y_max] = moore_op.process();
    }

    y_max++;
    for (int x = 2; x < _r.width - 2; x++) {
      if ((y_max & 1) == yob1) { // /
        moore_op.nw = _r.safeRead(x + 1, y_max); // (n)
        moore_op.ne = _r.safeRead(x + 2, y_max); // (ne) same 
        moore_op.sw = _r.safeRead(x, y_max + 2); // (sw) same
        moore_op.se = _r.safeRead(x + 1, y_max + 2); // (s)
      } else {
        moore_op.nw = _r.safeRead(x, y_max); // (nw) same
        moore_op.ne = _r.safeRead(x + 1, y_max); // (n)
        moore_op.sw = _r.safeRead(x + 1, y_max + 2); // (s)
        moore_op.se = _r.safeRead(x + 2, y_max + 2); // (se) same
      }

      moore_op.w = _r.safeRead(x, y_max + 1);
      moore_op.c = _r.safeRead(x + 1, y_max + 1);
      moore_op.e = _r.safeRead(x + 2, y_max + 1);

      _r.v[x][y_max] = moore_op.process();
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

  // assumes top 16 bits are all zero...
  // wasteful use of safeRead :-(
  static void weightedSumMoore(ComplexArray2D c_a, int w_c, int w_n, int w_s, int w_e, int w_w, int w_nw, int w_ne, int w_sw, int w_se) {
    MooreOperation moore_op = new MooreOperation() {
      int w_c;
      int w_n;
      int w_s;
      int w_e;
      int w_w;
      int w_ne;
      int w_nw;
      int w_se;
      int w_sw;

      public void init(int w_c, int w_n, int w_s, int w_e, int w_w, int w_ne, int w_nw, int w_se, int w_sw) {
        this.w_c = w_c;
        this.w_n = w_n;
        this.w_s = w_s;
        this.w_e = w_e;
        this.w_w = w_w;
        this.w_ne = w_ne;
        this.w_nw = w_nw;
        this.w_se = w_se;
        this.w_sw = w_sw;
      }

      public int process() {
        int count;
        count = (c * w_c) + (n * w_n) + (s * w_s) + (e * w_e) + (w * w_w) + (ne * w_ne) + (nw * w_nw) + (se * w_se) + (sw * w_sw);

        if (count < 0) {
          count = 0;
        }

        return count >>> 15;
      }
    };

    moore_op.init(w_c, w_n, w_s, w_e, w_w, w_ne, w_nw, w_se, w_sw);

    generalMooreOperation(c_a, moore_op);
  }

  static void compensatedWeightedSumMoore(ComplexArray2D c_a, int w_c, int w_n, int w_s, int w_e, int w_w, int w_nw, int w_ne, int w_sw, int w_se) {
    MooreOperation moore_op = new MooreOperation() {
      int w_c;
      int w_n;
      int w_s;
      int w_e;
      int w_w;
      int w_ne;
      int w_nw;
      int w_se;
      int w_sw;
      int NMX = 0xC000;
      int NMN = 0x4000;

      public void init(int w_c, int w_n, int w_s, int w_e, int w_w, int w_ne, int w_nw, int w_se, int w_sw) {
        this.w_c = w_c;
        this.w_n = w_n;
        this.w_s = w_s;
        this.w_e = w_e;
        this.w_w = w_w;
        this.w_ne = w_ne;
        this.w_nw = w_nw;
        this.w_se = w_se;
        this.w_sw = w_sw;
      }

      public int process() {
        int count;
        if ((c > NMX) || (n > NMX) || (s > NMX) || (e > NMX) || (w > NMX) || (nw > NMX) || (ne > NMX) || (sw > NMX) || (se > NMX)) {
            count = // 0x2000 + 
   (((c < NMN) ? c + 0x10000 : c) * w_c) + (((n < NMN) ? n + 0x10000 : n) * w_n) + (((s < NMN) ? s + 0x10000 : s) * w_s) + (((e < NMN) ? e + 0x10000 : e) * w_e) + (((w < NMN) ? w + 0x10000 : w) * w_w) + (((nw < NMN) ? nw + 0x10000 : nw) * w_nw) + (((sw < NMN) ? sw + 0x10000 : sw) * w_sw) + (((ne < NMN) ? ne + 0x10000 : ne) * w_ne) + (((se < NMN) ? se + 0x10000 : se) * w_se);
        } else {
          count = (c * w_c) + (n * w_n) + (s * w_s) + (e * w_e) + (w * w_w) + (ne * w_ne) + (nw * w_nw) + (se * w_se) + (sw * w_sw);
        }

        if (count < 0) {
          count = 0;
        }

        return (count >>> 15);
      }
    };

    moore_op.init(w_c, w_n, w_s, w_e, w_w, w_ne, w_nw, w_se, w_sw);

    generalMooreOperation(c_a, moore_op);
  }

  public static void boundedCompensatedWeightedSumMoore(ComplexArray2D c_a, int w_c, int w_n, int w_s, int w_e, int w_w, int w_nw, int w_ne, int w_sw, int w_se) {
    MooreOperation moore_op = new MooreOperation() {
      int w_c;
      int w_n;
      int w_s;
      int w_e;
      int w_w;
      int w_ne;
      int w_nw;
      int w_se;
      int w_sw;
      int NMX = 0xC000;
      int NMN = 0x4000;

      public void init(int w_c, int w_n, int w_s, int w_e, int w_w, int w_ne, int w_nw, int w_se, int w_sw) {
        this.w_c = w_c;
        this.w_n = w_n;
        this.w_s = w_s;
        this.w_e = w_e;
        this.w_w = w_w;
        this.w_ne = w_ne;
        this.w_nw = w_nw;
        this.w_se = w_se;
        this.w_sw = w_sw;
      }

      public int process() {
        int count;
        if ((c > NMX) || (n > NMX) || (s > NMX) || (e > NMX) || (w > NMX) || (nw > NMX) || (ne > NMX) || (sw > NMX) || (se > NMX)) {
            count = // 0x2000 + 
   (((c < NMN) ? c + 0x10000 : c) * w_c) + (((n < NMN) ? n + 0x10000 : n) * w_n) + (((s < NMN) ? s + 0x10000 : s) * w_s) + (((e < NMN) ? e + 0x10000 : e) * w_e) + (((w < NMN) ? w + 0x10000 : w) * w_w) + (((nw < NMN) ? nw + 0x10000 : nw) * w_nw) + (((ne < NMN) ? ne + 0x10000 : ne) * w_ne) + (((sw < NMN) ? sw + 0x10000 : sw) * w_sw) + (((se < NMN) ? se + 0x10000 : se) * w_se);
        } else {
          count = (c * w_c) + (n * w_n) + (s * w_s) + (e * w_e) + (w * w_w) + (ne * w_ne) + (nw * w_nw) + (se * w_se) + (sw * w_sw);
        }

        return (count >>> 15) & 0xFFFF;
      }
    };

    moore_op.init(w_c, w_n, w_s, w_e, w_w, w_ne, w_nw, w_se, w_sw);

    generalMooreOperation(c_a, moore_op);
  }

  public static void blurUsingWeightedSumMoore(ComplexArray2D c_a) {
    boundedCompensatedWeightedSumMoore(c_a, 4999, 4000, 4000, 4000, 4000, 2942, 2942, 2942, 2942); // ?
  }

  public static void mixUsingWeightedSumMoore(ComplexArray2D c_a) {
    compensatedWeightedSumMoore(c_a, 0, 8000, 8000, 8000, 8000, -8000, -8000, -8000, -8000);
  }

  public static void angularUsingWeightedSumMoore(ComplexArray2D c_a) {
    boundedCompensatedWeightedSumMoore(c_a, 0, 8000, -8000, -8000, 8000, 6000, 0, 0, -6000);
  }
}
