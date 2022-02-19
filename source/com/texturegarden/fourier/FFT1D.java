package com.texturegarden.fourier;

import com.texturegarden.arrays.ComplexArray1D;
import com.texturegarden.arrays.ComplexArray2D;

/**
  * FFT1D - Tim Tyler 2000-2003
  */

public class FFT1D {
  static int n_wave_log2; /* log2(n_wave) */
  static int n_wave; /* dimension of sine_lut[] */

  // longs if going for full size...
  static int amp_log2 = 11; //* log2(n_wave) *
  static int amp = 1 << amp_log2; //* dimension of sine_lut[]
  static int amppo2 = amp >>> 1;

  static int[] sine_lut;

  final static int M = 6;
  final static int N = (1 << M);

  //static {
  //init_sin_table()
  //}

  // public static void main(String[] args) {
  /*
  int temp;
  int max = (int)((1L << 15) - 0);
  // Log.log("X:" + (int)(Math.round(0.7)));
  
  int[] real = new int[N];
  int[] imag = new int[N]; 
  
  for (int i=0; i<N; i++){
    real[i] = (int)(Math.round((max*Math.cos(i * 2 * Math.PI/N))));
    imag[i] = 0;
  }
  
  Log.log("ORIGINAL:");
  
  for (int i= 0; i < N; i++) {
    Log.put(" " + real[i]);
  }
  
  // do inverse first...
  
  fix_fft(real, imag, M, true);
  // Log.log("\nTEMP :" + temp);
  
  Log.log("\nNEW:");
  
  for (int i= 0; i < N; i++) {
    Log.put(" " + real[i]);
  }
  
  fix_fft(real, imag, M, false);
  // Log.log("\nTEMP :" + temp);
  
  Log.log("\nORIGINAL:");
  
  for (int i= 0; i < N; i++) {
    Log.put(" " + real[i]);
  }
  
  Log.log("\nEND");
  
  do {} while (0==0);
  */
  //}

  static void copyToFP(int[] integer, float[] fp) {
    int size = fp.length;
    for (int i = 0; i < size; i++) {
      fp[i] = integer[i];
    }
  }

  static void copyFromFP(float[] fp, int[] integer) {
    int size = fp.length;
    for (int i = 0; i < size; i++) {
      integer[i] = (int) ((fp[i] * 4) / size); // * 4
    }
  }

  static void copyToFP(int[][] integer, float[] fp, int y) {
    int size = fp.length;
    for (int i = 0; i < size; i++) {
      fp[i] = integer[i][y];
    }
  }

  static void copyFromFP(float[] fp, int[][] integer, int y) {
    int size = fp.length;
    for (int i = 0; i < size; i++) {
      integer[i][y] = (int) ((fp[i] * 4) / size); //  * 4
    }
  }

  // 1D FFT
  public static ComplexArray1D fft(ComplexArray1D c_a) {
    int size = c_a.r.size;


    //if (BitUtils.isAPowerOfTwo(size)) {
    //fix_fft(c_a.r.v, c_a.i.v, true);
    //}
    //else
    //{
    float[] _real = new float[size];
    float[] _imag = new float[size];

    copyToFP(c_a.r.v, _real);
    copyToFP(c_a.i.v, _imag);

    FFTNFP.sing(_real, _imag, size, size, size, true);

    copyFromFP(_real, c_a.r.v);
    copyFromFP(_imag, c_a.i.v);
    //}

    return c_a;
  }

  // 1D inverse FFT
  public static ComplexArray1D fft_inv(ComplexArray1D c_a) {
    int size = c_a.r.size;

    //if (BitUtils.isAPowerOfTwo(size)) {
    //fix_fft(c_a.r.v, c_a.i.v, false);
    //}
    //else
    //{
    float[] _real = new float[size];
    float[] _imag = new float[size];

    copyToFP(c_a.r.v, _real);
    copyToFP(c_a.i.v, _imag);

    FFTNFP.sing(_real, _imag, size, size, size, false);

    copyFromFP(_real, c_a.r.v);
    copyFromFP(_imag, c_a.i.v);
    //}

    return c_a;
  }

  // 2D FFT(x)
  public static ComplexArray2D fft(ComplexArray2D c_a, int x) {
    int size = c_a.r.height;

    //if (BitUtils.isAPowerOfTwo(size)) {
    //fix_fft(c_a.r.v[x], c_a.i.v[x], true);
    //}
    //else
    //{
    float[] _real = new float[size];
    float[] _imag = new float[size];

    copyToFP(c_a.r.v[x], _real);
    copyToFP(c_a.i.v[x], _imag);

    FFTNFP.sing(_real, _imag, size, size, size, true);

    copyFromFP(_real, c_a.r.v[x]);
    copyFromFP(_imag, c_a.i.v[x]);
    //}

    return c_a;
  }

  // 2D inverse FFT(x)
  public static ComplexArray2D fft_inv(ComplexArray2D c_a, int x) {
    int size = c_a.r.height;

    //if (BitUtils.isAPowerOfTwo(size)) {
    //fix_fft(c_a.r.v[x], c_a.i.v[x], false);
    //}
    //else
    //{
    float[] _real = new float[size];
    float[] _imag = new float[size];

    copyToFP(c_a.r.v[x], _real);
    copyToFP(c_a.i.v[x], _imag);

    FFTNFP.sing(_real, _imag, size, size, size, false);

    copyFromFP(_real, c_a.r.v[x]);
    copyFromFP(_imag, c_a.i.v[x]);
    //}

    return c_a;
  }

  // 2D FFT(Y)
  static ComplexArray2D fft_y(ComplexArray2D c_a, int y) {
    int size = c_a.r.width;

    //if (BitUtils.isAPowerOfTwo(size)) {
    //fix_fft_y(c_a.r.v, c_a.i.v, y, true);
    //}
    //else
    //{
    float[] _real = new float[size];
    float[] _imag = new float[size];

    copyToFP(c_a.r.v, _real, y);
    copyToFP(c_a.i.v, _imag, y);

    FFTNFP.sing(_real, _imag, size, size, size, true);

    copyFromFP(_real, c_a.r.v, y);
    copyFromFP(_imag, c_a.i.v, y);
    // }

    return c_a;
  }

  // 2D inverse FFT(Y)
  static ComplexArray2D fft_y_inv(ComplexArray2D c_a, int y) {
    int size = c_a.r.width;

    //if (BitUtils.isAPowerOfTwo(size)) {
    //fix_fft_y(c_a.r.v, c_a.i.v, y, false);
    //}
    //else
    //{
    float[] _real = new float[size];
    float[] _imag = new float[size];

    copyToFP(c_a.r.v, _real, y);
    copyToFP(c_a.i.v, _imag, y);

    FFTNFP.sing(_real, _imag, size, size, size, false);

    copyFromFP(_real, c_a.r.v, y);
    copyFromFP(_imag, c_a.i.v, y);

    // }
    return c_a;
  }

  /*
  	// 1D FFT
     static ComplexArray1D fftX(ComplexArray1D c_a) {
        fix_fft(c_a.r.v, c_a.i.v, true);
     
        return c_a;
     }
  
  
  // 1D inverse FFT
     static ComplexArray1D fft_invX(ComplexArray1D c_a) {
        fix_fft(c_a.r.v, c_a.i.v, false);
     
        return c_a;
     }
  
  
  // 2D FFT(x)
     static ComplexArray2D fftXX(ComplexArray2D c_a, int x) { // , int log_n) {
        fix_fft(c_a.r.v[x], c_a.i.v[x], true);
     
        return c_a;
     }
  
  
  // 2D inverse FFT(x)
     static ComplexArray2D fft_invXX(ComplexArray2D c_a, int x) { // , int log_n) {
        fix_fft(c_a.r.v[x], c_a.i.v[x], false);
     
        return c_a;
     }
  
  
  // 2D FFT(y)
     static ComplexArray2D fft_yXX(ComplexArray2D c_a, int y) { // , int log_n) {
  
        fix_fft_y(c_a.r.v, c_a.i.v, y, true);
     
        return c_a;
     }
  
  
  // 2D inverse FFT(y)
     static ComplexArray2D fft_y_invXX(ComplexArray2D c_a, int y) { // , int log_n) {
     fix_fft_y(c_a.r.v, c_a.i.v, y, false);
     
        return c_a;
     }
  */

  // final static int N_LOUD = 100; /* dimension of Loudampl[]
  // extern fixed sine_lut[n_wave]; /* placed at end of this file for clarity */
  // extern fixed Loudampl[N_LOUD];

  //int db_from_ampl(fixed re, fixed im);

  // fixed fix_mpy(fixed a, fixed b);
  static void init_sin_table(int n) {
    sine_lut = new int[n];
    long temp;

    //Log.log("\nAMP:" + amp);

    for (int i = 0; i < n; i++) {
      temp = (Math.round((amp * Math.sin((i * 2 * Math.PI) / n))));
      if (temp >= (1L << 31)) {
        temp = (1L << 31) - 1;
      }

      sine_lut[i] = (int) temp;

      //sine_lut[i] = (int)Math.round((amp * Math.sin((i * 2 * Math.PI)/n)));
    }

    /*
    Log.log("\nSIN:");
    
    for (int i= 0; i < n; i++) {
       Log.put(" " + Integer.toHexString(sine_lut[i]));
    }
    */
  }

  /**  fix_fft() - perform Fast Fourier Transform.
       if n>0 FFT is done, if n<0 inverse FFT is done
       fr[n],fi[n] are real,imaginary arrays, INPUT AND RESULT.
       size of data = 2**m
       set inverse to false=dft, true=idft
  */
  static void fix_fft(int fr[], int fi[], boolean inverse) {
    int mr, nn, i, j, l, k, istep;
    boolean shift;
    int qr, qi, tr, ti, wr, wi; // fixed - like the arrays...

    int n = fr.length;
    int m = log2(n);

    // n = 1 << m;

    n_wave_log2 = m;
    n_wave = n;

    init_sin_table(n_wave);

    mr = 0;
    nn = n - 1;
   // scale = 0;

    /* decimation in time - re-order data */
    for (m = 1; m <= nn; ++m) {
      l = n;

      do {
        l >>= 1;
      } while (mr + l > nn);

      mr = (mr & (l - 1)) + l;

      if (mr <= m) {
        continue;
      }

      tr = fr[m];
      fr[m] = fr[mr];
      fr[mr] = tr;
      ti = fi[m];
      fi[m] = fi[mr];
      fi[mr] = ti;
    }

    l = 1;
    k = n_wave_log2 - 1;
    while (l < n) {
      shift = !inverse;

      // shift = true;

      /*
         if (inverse) {
            // variable scaling, depending upon data 
            shift = false;
            for (i=0; i < n; ++i) {
               j = fr[i];
            
               if (j < 0) {
                  j = -j;
               }
            
               m = fi[i];
               if (m < 0) {
                  m = -m;
               }
            
               // if (j > 16383 || m > 16383) {
               if ((j >= amppo2) || (m >= amppo2)) {
                  shift = true;
                  break;
               }
            }
         
            if (shift) {
               ++scale;
            }
         } 
         else {
                     // fixed scaling, for proper normalization -
                     // there will be log2(n) passes, so this
                     // results in an overall factor of 1/n,
                     // distributed to maximize arithmetic accuracy.
            shift = true;
         }
      	*/

      //shift = false;
      // shift = true;

      /* it may not be obvious, but the shift will be performed
         on each data point exactly once, during this pass. */
      istep = l << 1;
      for (m = 0; m < l; ++m) {
        j = m << k;
        /* 0 <= j < n_wave/2 */
        wr = sine_lut[j + (n_wave >> 2)];

        //wi = -sine_lut[j];

        //if (inverse) {
        //wi = -wi;
        //}

        wi = inverse ? sine_lut[j] : -sine_lut[j];

        if (shift) {
          wr >>= 1;
          wi >>= 1;
        }

        for (i = m; i < n; i += istep) {
          j = i + l;
          tr = fix_mpy(wr, fr[j]) - fix_mpy(wi, fi[j]);
          ti = fix_mpy(wr, fi[j]) + fix_mpy(wi, fr[j]);
          qr = fr[i];
          qi = fi[i];

          if (shift) {
            qr >>= 1;
            qi >>= 1;
          }

          fr[j] = qr - tr;
          fi[j] = qi - ti;
          fr[i] = qr + tr;
          fi[i] = qi + ti;
        }
      }

      --k;
      l = istep;
    }
  }

  public static int log2(int z) {
    int ret_val = 0;

    while (z > 0) {
      ret_val++;
      z = z >>> 1;
    }

    return ret_val - 1;
  }

  /**  fix_fft() - perform Fast Fourier Transform.
       if n>0 FFT is done, if n<0 inverse FFT is done
       fr[n],fi[n] are real,imaginary arrays, INPUT AND RESULT.
       size of data = 2**m
       set inverse to false=dft, true=idft
  */

  /*
     static void fix_fft_y(int fr[][], int fi[][], int y, boolean inverse) {
        int mr,nn,i,j,l,k,istep, scale;
        boolean shift;
        int qr,qi,tr,ti,wr,wi,t; // fixed - like the arrays...
        int n = fr[0].length;
        int m = log2(n);
     
        n_wave_log2 = m;
        n_wave = n;
     
        init_sin_table(n_wave);
     
        mr = 0;
        nn = n - 1;
        scale = 0;
     
       // decimation in time - re-order data
        for(m = 1; m <= nn; ++m) {
           l = n;
        
           do {
              l >>= 1;
           } while (mr + l > nn);
        
           mr = (mr & (l - 1)) + l;
        
           if (mr <= m) {
              continue;
           }
        
           tr = fr[m][y];
           fr[m][y] = fr[mr][y];
           fr[mr][y] = tr;
           ti = fi[m][y];
           fi[m][y] = fi[mr][y];
           fi[mr][y] = ti;
        }
     
        l = 1;
        k = n_wave_log2 - 1;
        while (l < n) {
        
           shift = !inverse;
        
           // it may not be obvious, but the shift will be performed on each data
           // point exactly once, during this pass.
           istep = l << 1;
           for (m=0; m < l; ++m) {
              j = m << k;
              // 0 <= j < n_wave/2
              wr =  sine_lut[j + (n_wave >> 2)];
           
              wi = inverse ? sine_lut[j] : -sine_lut[j];
           
              if (shift) {
                 wr >>= 1;
                 wi >>= 1;
              }
           
              for(i = m; i < n; i += istep) {
                 j = i + l;
                 tr = fix_mpy(wr,fr[j][y]) - fix_mpy(wi,fi[j][y]);
                 ti = fix_mpy(wr,fi[j][y]) + fix_mpy(wi,fr[j][y]);
                 qr = fr[i][y];
                 qi = fi[i][y];
              
                 if (shift) {
                    qr >>= 1;
                    qi >>= 1;
                 }
              
                 fr[j][y] = qr - tr;
                 fi[j][y] = qi - ti;
                 fr[i][y] = qr + tr;
                 fi[i][y] = qi + ti;
              }
           }
        
           --k;
           l = istep;
        }
     
        // return scale;
     }
  */

  /*
       fix_mpy() - fixed-point multiplication
  */
  static int fix_mpy(int a, int b) {
    return (a * b) >> amp_log2;
    // return (int)(((long)(a) * (long)(b)) >>> amp_log2);
  }
}

  /*
       iscale() - scale an integer value by (numer/denom)
  */
  //static int iscale(int value, int numer, int denom) {
  //return (int)((long)value * (long)numer/(long)denom);
  //}

  /*      window() - apply a Hanning window       */
  /*
     static void window(int fr[], int n) {
        int i,j,k;
     
        j = n_wave/n;
        n >>= 1;
        for(i = 0, k = n_wave/4; i < n; ++i,k += j) {
           fr[i] = fix_mpy(fr[i],16384-(sine_lut[k]>>1));
        }
     
        n <<= 1;
     
        for(k-=j; i<n; ++i,k-=j) {
           fr[i] = fix_mpy(fr[i],16384-(sine_lut[k]>>1));
        }
     }
  	*/

  /*   fix_loud() - compute loudness of freq-spectrum components.
       n should be ntot/2, where ntot was passed to fix_fft();
       6 dB is added to account for the omitted alias components.
       scale_shift should be the result of fix_fft(), if the time-series
       was obtained from an inverse FFT, 0 otherwise.
       loud[] is the loudness, in dB wrt 32767; will be +10 to -N_LOUD.
  */
  /*
     static void fix_loud(int loud[], int fr[], int fi[], int n, int scale_shift)
     {
        int i, max;
     
        max = 0;
        if(scale_shift > 0)
           max = 10;
        scale_shift = (scale_shift+1) * 6;
     
        for(i=0; i<n; ++i) {
           loud[i] = db_from_ampl(fr[i],fi[i]) + scale_shift;
           if(loud[i] > max)
              loud[i] = max;
        }
     }
  */

  /*      db_from_ampl() - find loudness (in dB) from
       the complex amplitude.
  */
  /*
     static int db_from_ampl(int re, int im) {
        long loud2[] = new long[1];
        long v;
        int i;
     
        if(loud2[0] == 0) {
           loud2[0] = (long)Loudampl[0] * (long)Loudampl[0];
           for(i=1; i<N_LOUD; ++i) {
              v = (long)Loudampl[i] * (long)Loudampl[i];
              loud2[i] = v;
              loud2[i-1] = (loud2[i-1]+v) / 2;
           }
        }
     
        v = (long)re * (long)re + (long)im * (long)im;
     
        for(i=0; i<N_LOUD; ++i) {
           if(loud2[i] <= v) {
              break;
           }
        }
     
        return (-i);
     }
  */
