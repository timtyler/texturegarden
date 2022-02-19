package com.texturegarden.arrays;

import com.texturegarden.Texture;
import com.texturegarden.utilities.JUR;
import com.texturegarden.utilities.Log;
import com.texturegarden.utilities.Utils;

/**
 * ComplexArray2D - Tim Tyler 2000-2003
 */
public class ComplexArray2D {
  public SimpleArray2D r;
  public SimpleArray2D i;

  //int width;
  //int height;

  //int width_log2;
  //int height_log2;

  //int width  = 1 << width_log2;
  //int height = 1 << height_log2;

  //int x_offset;
  //int y_offset;

  static JUR rnd = new JUR();

  public ComplexArray2D(int w, int h) {
    //width_log2  = wl2;
    //height_log2 = hl2;

    //width  = w;
    //height = 1 << h;

    r = new SimpleArray2D(w, h);

    i = new SimpleArray2D(w, h);

    //x_offset = 0;
    //y_offset = 0;
  }

  public void swapRealAndImaginary() {
    SimpleArray2D temp;
    temp = r;
    r = i;
    i = temp;
  }

  public void takeSquareRoots() {
    int _r;
    int _i;

    for (int x = 0; x < r.width; x++) {
      for (int y = 0; y < r.height; y++) {
        _r = r.v[x][y];
        _i = i.v[x][y];
        // r[x][y] = SquareRoot.sqrt((_r * _r) + (_i * _i));
        r.v[x][y] = (int) Math.sqrt((_r * _r) + (_i * _i));

        // Log.log("r:" + Integer.toHexString(r) + " i:" + Integer.toHexString(i));
        //Log.log("SOS:" + Integer.toHexString((r * r) + (i * i)));
      }
    }
  }

  public void averageComponents() {
    for (int x = 0; x < r.width; x++) {
      for (int y = 0; y < r.height; y++) {
        // _r = r.v[x][y];
        //_i = i.v[x][y];
        // r[x][y] = SquareRoot.sqrt((_r * _r) + (_i * _i));
        r.v[x][y] = (r.v[x][y] + i.v[x][y]) >> 1;

        // Log.log("r:" + Integer.toHexString(r) + " i:" + Integer.toHexString(i));
        //Log.log("SOS:" + Integer.toHexString((r * r) + (i * i)));
      }
    }
  }

  public void setSeed(int s) {
    r.setSeed(s);
    i.setSeed(s);
  }

  public static void copyBuffer(ComplexArray2D from, ComplexArray2D to) {
    if (from.r.width != to.r.width) {
      Log.log("Error - copy buffers of different widths");
    } else {
      if (from.r.height != to.r.height) {
        Log.log("Error - copy buffers of different heights");
      } else {
        for (int x = 0; x < from.r.width; x++) {
          // arraycopy...
          // System.arraycopy(from.r.v[x],to.r.v[x],0,from.r.height); // 2D array - argh!

          for (int y = 0; y < from.r.height; y++) {
            to.r.v[x][y] = from.r.v[x][y];
          }
        }
      }
    }
  }

  public void clear() {
    r.clear();
    i.clear();
  }

  // *** RANGE OF NOISE FUNCTIONS is MADNESS!!!
  // Actual functions...

  public void noiseSquareSection(int intensity, int start_x, int start_y, int size_x, int size_y, Texture t) {
    if (size_x > 0) {
      if (size_y > 0) {
        int actual_start_x = start_x - size_x >> 1;
        int actual_end_x = start_x + size_x >> 1;
        int actual_start_y = start_y - size_y >> 1;
        int actual_end_y = start_y + size_y >> 1;

        while (actual_start_x < 0) {
          actual_start_x += r.width;
        }

        while (actual_start_y < 0) {
          actual_start_y += r.height;
        }

        while (actual_end_x < actual_start_x) {
          actual_end_x += r.width;
        }

        while (actual_end_y < actual_start_y) {
          actual_end_y += r.height;
        }

        for (int x = actual_start_x; x < actual_end_x; x++) {
          for (int y = actual_start_y; y < actual_end_y; y++) {
            int _x = x % r.width;
            int _y = y % r.height;

            int th = t.rnd.nextInt(0x10000) + t.time;
            r.v[_x][_y] = Utils.Sine(intensity, th);
            i.v[_x][_y] = Utils.Cosine(intensity, th);
          }
        }
      }
    }
  }

  void noiseHalf(int intensity, int radius, Texture t) {
    if (radius > r.width) {
      radius = r.width;
    }

    if (radius > r.height) {
      radius = r.height;
    }

    int r_squared = radius * radius;
    for (int x = radius; --x >= 0;) {
      for (int y = radius; --y >= 0;) {
        if ((x * x + y * y) < r_squared) { // crude and slow...
          int th;
          th = t.rnd.nextInt(0x10000) + t.time;
          r.v[x][y] = Utils.Sine(intensity, th); //  * (Utils.simpleSine(rnd.nextInt(0x10000) + t.time) >> 8);
          r.v[r.width - 1 - x][y] = Utils.Sine(intensity, th); //  * (U(Utils.simpleSine(rnd.nextInt(0x10000) + t.time) >> 8);
          th = t.rnd.nextInt(0x10000) + t.time;
          i.v[x][y] = Utils.Cosine(intensity, th); //  * (Utils.simpleSine(rnd.nextInt(0x10000) + t.time) >> 8);
          i.v[i.width - 1 - x][y] = Utils.Cosine(intensity, th); //  * (U(Utils.simpleSine(rnd.nextInt(0x10000) + t.time) >> 8);
        }
      }
    }
  }

  public void noise(int intensity, int radius, Texture t) {
    if (radius > r.width) {
      radius = r.width;
    }

    if (radius > r.height) {
      radius = r.height;
    }

    int r_squared = radius * radius;
    for (int x = radius; --x >= 0;) {
      for (int y = radius; --y >= 0;) {
        if ((x * x + y * y) < r_squared) { // crude and slow...
          int th;
          th = t.rnd.nextInt(0x10000) + t.time;
          r.v[x][y] = Utils.Sine(intensity, th); //  * (Utils.simpleSine(rnd.nextInt(0x10000) + t.time) >> 8);
          r.v[r.width - 1 - x][y] = Utils.Sine(intensity, th); //  * (U(Utils.simpleSine(rnd.nextInt(0x10000) + t.time) >> 8);
          th = t.rnd.nextInt(0x10000) + t.time;
          i.v[x][y] = Utils.Cosine(intensity, th); //  * (Utils.simpleSine(rnd.nextInt(0x10000) + t.time) >> 8);
          i.v[i.width - 1 - x][y] = Utils.Cosine(intensity, th); //  * (U(Utils.simpleSine(rnd.nextInt(0x10000) + t.time) >> 8);
          th = t.rnd.nextInt(0x10000) + t.time;
          r.v[x][r.height - 1 - y] = Utils.Sine(intensity, th); //  * (Utils.simpleSine(rnd.nextInt(0x10000) + t.time) >> 8);
          r.v[r.width - 1 - x][r.height - 1 - y] = Utils.Sine(intensity, th); //  * (U(Utils.simpleSine(rnd.nextInt(0x10000) + t.time) >> 8);
          th = t.rnd.nextInt(0x10000) + t.time;
          i.v[x][r.height - 1 - y] = Utils.Cosine(intensity, th); //  * (Utils.simpleSine(rnd.nextInt(0x10000) + t.time) >> 8);
          i.v[i.width - 1 - x][r.height - 1 - y] = Utils.Cosine(intensity, th); //  * (U(Utils.simpleSine(rnd.nextInt(0x10000) + t.time) >> 8);
        }
      }
    }
  }

  public void noiseBandwidthLimitedHalf(int intensity, int internal_radius, int external_radius, Texture t) {
    if (external_radius > r.width) {
      external_radius = r.width;
    }

    if (external_radius > r.height) {
      external_radius = r.height;
    }

    int ir_squared = internal_radius * internal_radius;
    int er_squared = external_radius * external_radius;
    int a_r;
    for (int x = external_radius; --x >= 0;) {
      for (int y = external_radius; --y >= 0;) {
        a_r = (x * x + y * y);
        if (a_r <= er_squared) { // crude and slow...
          if (a_r >= ir_squared) { // crude and slow...
            int th = t.rnd.nextInt(0x10000) + t.time;

            r.v[x][y] = Utils.Sine(intensity, th); //  * (Utils.simpleSine(rnd.nextInt(0x10000) + t.time) >> 8);
            r.v[r.width - 1 - x][y] = Utils.Sine(intensity, th); //  * (U(Utils.simpleSine(rnd.nextInt(0x10000) + t.time) >> 8);
            i.v[x][y] = Utils.Cosine(intensity, th); //  * (Utils.simpleSine(rnd.nextInt(0x10000) + t.time) >> 8);
            i.v[i.width - 1 - x][y] = Utils.Cosine(intensity, th); //  * (U(Utils.simpleSine(rnd.nextInt(0x10000) + t.time) >> 8);
          }
        }
      }
    }
  }

  public void noiseBandwidthLimited(int intensity, int internal_radius, int external_radius, Texture t) {
    if (external_radius > r.width) {
      external_radius = r.width;
    }

    if (external_radius > r.height) {
      external_radius = r.height;
    }

    int ir_squared = internal_radius * internal_radius;
    int er_squared = external_radius * external_radius;
    int a_r;
    for (int x = external_radius; --x >= 0;) {
      for (int y = external_radius; --y >= 0;) {
        a_r = (x * x + y * y);
        if (a_r <= er_squared) { // crude and slow...
          if (a_r >= ir_squared) { // crude and slow...
            int th;
            th = t.rnd.nextInt(0x10000) + t.time;
            r.v[x][y] = Utils.Sine(intensity, th); //  * (Utils.simpleSine(rnd.nextInt(0x10000) + t.time) >> 8);
            r.v[r.width - 1 - x][y] = Utils.Sine(intensity, th); //  * (U(Utils.simpleSine(rnd.nextInt(0x10000) + t.time) >> 8);
            th = t.rnd.nextInt(0x10000) + t.time;
            i.v[x][y] = Utils.Cosine(intensity, th); //  * (Utils.simpleSine(rnd.nextInt(0x10000) + t.time) >> 8);
            i.v[i.width - 1 - x][y] = Utils.Cosine(intensity, th); //  * (U(Utils.simpleSine(rnd.nextInt(0x10000) + t.time) >> 8);
            th = t.rnd.nextInt(0x10000) + t.time;
            r.v[x][r.height - 1 - y] = Utils.Sine(intensity, th); //  * (Utils.simpleSine(rnd.nextInt(0x10000) + t.time) >> 8);
            r.v[r.width - 1 - x][r.height - 1 - y] = Utils.Sine(intensity, th); //  * (U(Utils.simpleSine(rnd.nextInt(0x10000) + t.time) >> 8);
            th = t.rnd.nextInt(0x10000) + t.time;
            i.v[x][r.height - 1 - y] = Utils.Cosine(intensity, th); //  * (Utils.simpleSine(rnd.nextInt(0x10000) + t.time) >> 8);
            i.v[i.width - 1 - x][r.height - 1 - y] = Utils.Cosine(intensity, th); //  * (U(Utils.simpleSine(rnd.nextInt(0x10000) + t.time) >> 8);
          }
        }
      }
    }
  }

  public void simpleFractalNoiseHalf(int intensity, int minimum_radius, Texture t) {
    int maxd = (r.width >> 1);

    if (maxd > (r.height >> 1)) {
      maxd = (r.height >> 1);
    }

    int a_r = minimum_radius * minimum_radius;
    int newi = 0x1000 * intensity;
    int val;
    int temp_r;
    for (int x = maxd; --x >= 0;) {
      for (int y = maxd; --y >= 0;) {
        temp_r = x * x + y * y;
        if (temp_r > a_r) {
          val = (newi / (temp_r + 1)); //  * 256;
          int th;
          th = t.rnd.nextInt(0x10000) + t.time;
          r.v[x][y] = Utils.Sine(val >> 8, th); //  * (Utils.simpleSine(rnd.nextInt(0x10000) + t.time) >> 8);
          r.v[r.width - 1 - x][y] = Utils.Sine(val >> 8, th); //  * (U(Utils.simpleSine(rnd.nextInt(0x10000) + t.time) >> 8);
          th = t.rnd.nextInt(0x10000) + t.time;
          i.v[x][y] = Utils.Cosine(val >> 8, th); //  * (Utils.simpleSine(rnd.nextInt(0x10000) + t.time) >> 8);
          i.v[i.width - 1 - x][y] = Utils.Cosine(val >> 8, th); //  * (U(Utils.simpleSine(rnd.nextInt(0x10000) + t.time) >> 8);
        }
      }
    }
  }

  public void simpleFractalNoise(int intensity, int minimum_radius, Texture t) {
    int maxd = (r.width >> 1);

    if (maxd > (r.height >> 1)) {
      maxd = (r.height >> 1);
    }

    int a_r = minimum_radius * minimum_radius;
    int newi = 0x1000 * intensity;
    int val;
    int temp_r;
    for (int x = maxd; --x >= 0;) {
      for (int y = maxd; --y >= 0;) {
        temp_r = x * x + y * y;
        if (temp_r > a_r) {
          val = (newi / (temp_r + 1)); //  * 256;

          int th;
          th = t.rnd.nextInt(0x10000) + t.time;
          r.v[x][y] = Utils.Sine(val >> 8, th); //  * (Utils.simpleSine(rnd.nextInt(0x10000) + t.time) >> 8);
          r.v[r.width - 1 - x][y] = Utils.Sine(val >> 8, th); //  * (U(Utils.simpleSine(rnd.nextInt(0x10000) + t.time) >> 8);
          th = t.rnd.nextInt(0x10000) + t.time;
          i.v[x][y] = Utils.Cosine(val >> 8, th); //  * (Utils.simpleSine(rnd.nextInt(0x10000) + t.time) >> 8);
          i.v[i.width - 1 - x][y] = Utils.Cosine(val >> 8, th); //  * (U(Utils.simpleSine(rnd.nextInt(0x10000) + t.time) >> 8);
          th = t.rnd.nextInt(0x10000) + t.time;
          r.v[x][r.height - 1 - y] = Utils.Sine(val >> 8, th); //  * (Utils.simpleSine(rnd.nextInt(0x10000) + t.time) >> 8);
          r.v[r.width - 1 - x][r.height - 1 - y] = Utils.Sine(val >> 8, th); //  * (U(Utils.simpleSine(rnd.nextInt(0x10000) + t.time) >> 8);
          th = t.rnd.nextInt(0x10000) + t.time;
          i.v[x][r.height - 1 - y] = Utils.Cosine(val >> 8, th); //  * (Utils.simpleSine(rnd.nextInt(0x10000) + t.time) >> 8);
          i.v[i.width - 1 - x][r.height - 1 - y] = Utils.Cosine(val >> 8, th); //  * (U(Utils.simpleSine(rnd.nextInt(0x10000) + t.time) >> 8);
        }
      }
    }
  }
}
