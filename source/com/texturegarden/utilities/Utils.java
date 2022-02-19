package com.texturegarden.utilities;
/**
 * Utils - Tim Tyler 2000-2001
 */

public class Utils {
  public  static int simpleSine(int x) {
    x = (x & 0xFFFF) << 1;
    // return (x > 0xFFFF) ? (0x1FFFF - x) : x;

    return (x > 0xFFFF) ? (0x17FFF - x) : -0x8000 + x;
  }

  public  static int Sine(int intensity, int theta) {
    return (int) (intensity * Math.sin((2 * Math.PI * (theta & 0xFFFF)) / 0x10000));
  }

  public static int Cosine(int intensity, int theta) {
    return (int) (intensity * Math.cos((2 * Math.PI * (theta & 0xFFFF)) / 0x10000));
  }

  public  static int bound(int x, int max) {
    while (x >= max) {
      x -= max;
    }

    while (x < 0) {
      x += max;
    }

    return x;
  }

  //public static int max(int arg1, int arg2) {
   // return (arg1 > arg2) ? arg1 : arg2;
  //}

  //public static int min(int arg1, int arg2) {
    //return (arg1 < arg2) ? arg1 : arg2;
 // }
}
