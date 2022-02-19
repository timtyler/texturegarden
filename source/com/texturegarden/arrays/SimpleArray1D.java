package com.texturegarden.arrays;

import com.texturegarden.utilities.JUR;

public class SimpleArray1D {
  public int[] v;
  public  int size;
  public  int offset;

  JUR rnd = new JUR();
  JUR rnd2 = new JUR();

  public SimpleArray1D(int n) {
    v = new int[n];
    size = n;
    offset = 0;
  }

  public void setSeed(int s) {
    rnd.setSeed(s);
    rnd2.setSeed(s);
  }

  void equaliseFast() {
    int max = 0;
    int max_range = 0xFFFFFF;

    for (int x = 0; x < size; x++) {

      if (v[x] > max) {
        max = v[x];
      }
    }

    if (max <= 0) {
      max_range = 0;
      max = 1;
    }

    int scale_factor = max_range / max;
    for (int x = 0; x < size; x++) {
      v[x] = (v[x] * scale_factor) >>> 8;
    }
  }

  void equaliseFully() {
    int max = 0x80000000;
    int min = 0x7FFFFFFF;
    int max_range = 0xFFFFFF;
    int _v;

    for (int x = 0; x < size; x++) {
      _v = v[x];
      if (_v > max) {
        max = _v;
      } else {
        if (_v < min) {
          min = _v;
        }
      }
    }

    if (max == min) {
      max_range = 0;
      max++;
    }

    int scale_factor = max_range / (max - min);
    for (int x = 0; x < size; x++) {
      v[x] = ((v[x] - min) * scale_factor) >>> 8;
    }
  }

  // Leaves things in the range [0 - max_range]...
  public void equaliseScaled(int max_range) {
    int max = 0x80000000;
    int min = 0x7FFFFFFF;
    int _v;

    for (int x = 0; x < size; x++) {
      _v = v[x];
      if (_v > max) {
        max = _v;
      } else {
        if (_v < min) {
          min = _v;
        }
      }
    }

    if (max == min) {
      max_range = 0;
      max++;
    }

    int scale_factor = (max_range << 8) / (max - min);
    for (int x = 0; x < size; x++) {
      v[x] = ((v[x] - min) * scale_factor) >>> 8;
    }
  }

  void equaliseFullyWithValleys() {
    int max = 0x80000000;
    int min = 0x7FFFFFFF;
    int max_range = 0x1FFFFFF;
    int _v;
    int middle;

    for (int x = 0; x < size; x++) {
      _v = v[x];
      if (_v > max) {
        max = _v;
      } else {
        if (_v < min) {
          min = _v;
        }
      }
    }

    if (max == min) {
      max_range = 0;
      max++;
    }

    middle = (max - min) >> 1;

    int scale_factor = max_range / (max - min);
    for (int x = 0; x < size; x++) {
      _v = v[x] - min;
      if (_v >= middle) {
        v[x] = ((_v - middle) * scale_factor) >>> 8;
      } else {
        v[x] = ((middle - _v) * scale_factor) >>> 8;
      }
    }
  }

  // should be called after scaling...
  void addSlopingField(int n) {
    //int max = 0x80000000;
   // int min = 0x7FFFFFFF;
   // int max_range = 0x1FFFFFF;
   // int middle;

    int scale_factor = (0x10000 * n) / size;

    for (int x = 0; x < size; x++) {
      v[x] = (v[x] + (x * scale_factor)) & 0xFFFF;
    }
  }

  // should be called after scaling...
  void subtractSlopingField(int n) {
//    int max = 0x80000000;
//    int min = 0x7FFFFFFF;
//    int max_range = 0x1FFFFFF;
//    int middle;

    int scale_factor = (0x10000 * n) / size;

    for (int x = 0; x < size; x++) {
      v[x] = (v[x] - (x * scale_factor)) & 0xFFFF;
    }
  }

  /*
     void noiseHalf(int intensity, int radius, Texture t) {
        int io2 = intensity >> 1;
     
        if (radius > size) {
           radius = size;
        }
     
        for (int x = radius; --x >= 0; ) {
           v[x] = (intensity * Utils.simpleSine(rnd.nextInt(0x10000) + t.time)) >> 16;
        }
     }
  	*/

  public void clear() {
    ArrayClearer.clear(v);
  }
}
