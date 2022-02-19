package com.texturegarden.arrays;
/**
 *  ArrayClearer - Tim Tyler 2000.
 */

public class ArrayClearer {
  static int[] empty_array = new int[256];

  /** Clears integer arrays with zeros;
   *  @param x the 1D integer array in question
   *  @author Tim Tyler tt@iname.com
   */
  public static void clear(int[] a) {
    int l = a.length;
    if (l > empty_array.length) {
      empty_array = new int[l];
    }

    System.arraycopy(empty_array, 0, a, 0, l);
  }

  /** Clears integer arrays with zeros;
   *  @param x the 2D integer array in question
   *  @author Tim Tyler tt@iname.com
   */
  public static void clear(int[][] a) {
    for (int i = a.length; --i >= 0;) {
      clear(a[i]);
    }
  }
}
