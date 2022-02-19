package com.texturegarden.arrays;

import com.texturegarden.Texture;
import com.texturegarden.utilities.Log;
import com.texturegarden.utilities.Utils;

public class ComplexArray1D {
  public SimpleArray1D r;
  public  SimpleArray1D i;

  public  ComplexArray1D(int s) {
    r = new SimpleArray1D(s);
    i = new SimpleArray1D(s);
  }

  public void swapRealAndImaginary() {
    SimpleArray1D temp;
    temp = r;
    r = i;
    i = temp;
  }

  public void takeSquareRoots() {
    int r2;
    int i2;
    int[] _r = r.v;
    int[] _i = i.v;

    for (int x = 0; x < r.size; x++) {
      r2 = _r[x];
      i2 = _i[x];

      _r[x] = (int) Math.sqrt((r2 * r2) + (i2 * i2));
      // _r[x] =  SquareRoot.sqrt((r2 * r2) + (i2 * i2));
    }
  }

  public  void setSeed(int s) {
    r.setSeed(s);
    i.setSeed(s);
  }

  public static void copyBuffer(ComplexArray1D from, ComplexArray1D to) {
    if (from.r.size != to.r.size) {
      Log.log("Error - copy buffers of different widths");
    }

    for (int x = 0; x < from.r.size; x++) {
      to.r.v[x] = from.r.v[x];
    }
  }

  void noiseHalf(int intensity, int radius, Texture t) {
    //int io2 = intensity >> 1;

    if (radius > r.size) {
      radius = r.size;
    }

    for (int x = radius; --x >= 0;) {
      // v[x] = (intensity * Utils.simpleSine(rnd.nextInt(0x10000) + t.time)) >> 16;
      int th = t.rnd.nextInt(0x10000) + t.time;
      r.v[x] = Utils.Sine(intensity, th); //  * (Utils.simpleSine(rnd.nextInt(0x10000) + t.time) >> 8);
      i.v[x] = Utils.Cosine(intensity, th); //  * (Utils.simpleSine(rnd.nextInt(0x10000) + t.time) >> 8);
    }
  }

  public void noise(int intensity, int radius, Texture t) {
    //int io2 = intensity >> 1;

    if (radius > r.size) {
      radius = r.size;
    }

    for (int x = radius; --x >= 0;) {
      // v[x] = (intensity * Utils.simpleSine(rnd.nextInt(0x10000) + t.time)) >> 16;
      int th;
      th = t.rnd.nextInt(0x10000) + t.time;
      r.v[x] = Utils.Sine(intensity, th); //  * (Utils.simpleSine(rnd.nextInt(0x10000) + t.time) >> 8);
      i.v[x] = Utils.Cosine(intensity, th); //  * (Utils.simpleSine(rnd.nextInt(0x10000) + t.time) >> 8);
      th = t.rnd.nextInt(0x10000) + t.time;
      r.v[r.size - 1 - x] = Utils.Sine(intensity, th);
      i.v[r.size - 1 - x] = Utils.Cosine(intensity, th);
    }
  }

  public  void clear() {
    r.clear();
    i.clear();
  }
}
