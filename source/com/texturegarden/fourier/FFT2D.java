package com.texturegarden.fourier;

import com.texturegarden.arrays.ComplexArray2D;

/**
  * FFT2D - Tim Tyler 2000-2003
  */

public class FFT2D {
  //int width;
  //int height;
  // static JUR rnd = new JUR();

  public  static ComplexArray2D fft_inv(ComplexArray2D c_a) {
    // computationally intensive...
    for (int y = 0; y < c_a.r.height; y++) {
      int or = 0;
      for (int x = 0; x < c_a.r.width; x++) {
        or |= c_a.r.v[x][y] | c_a.i.v[x][y];
      }

      if (or != 0) {
        FFT1D.fft_y_inv(c_a, y);
      }
    }

    for (int x = 0; x < c_a.r.width; x++) {
      FFT1D.fft_inv(c_a, x);
    }

    return c_a;
  }

  public  static ComplexArray2D fft(ComplexArray2D c_a) {
    // computationally intensive...
    for (int y = 0; y < c_a.r.height; y++) {
      int or = 0;
      for (int x = 0; x < c_a.r.width; x++) {
        or |= c_a.r.v[x][y] | c_a.i.v[x][y];
      }

      if (or != 0) {
        FFT1D.fft_y(c_a, y);
      }
    }

    for (int x = 0; x < c_a.r.width; x++) {
      FFT1D.fft(c_a, x);
    }

    return c_a;
  }
}
