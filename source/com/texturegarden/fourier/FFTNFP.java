package com.texturegarden.fourier;

import com.texturegarden.utilities.Log;

/*	Computes the DISCRETE FOURIER TRANSFORM of very int data series.
 *   Restriction: the data has to fit in conventional memory.
 *
 * Java version for Texture Garden - Tim Tyler
 *
 */

/* Defines */
class FFTNFP {
  final static int MAXF = 511;
  final static int MAXP = 1024;

  /* Globals */
  static int nn, flag, jf, jc, kspan, ks, number_of_square_factors, nt, kk, i;
  static float c72, s72, s120, cd, sd, rad, radf;
  static float[] at = new float[MAXF];
  static float[] bt = new float[MAXF];

  static int[] nfac = new int[MAXF];

  static int inc;
  static int[] np = new int[MAXP];
  static int number_of_factors;
  static int factor_count;

  public static void main(String[] args) {
    int N = 8 * 5 * 3;

    int max = (int) ((1L << 15) - 0);
    // Log.log("X:" + (int)(Math.round(0.7)));

    float[] real = new float[N];
    float[] imag = new float[N];

    for (int i = 0; i < N; i++) {
      real[i] = (float) (Math.round((max * Math.cos(i * 2 * Math.PI / N))));
      imag[i] = 0;
    }

    Log.log("ORIGINAL:");

    for (int i = 0; i < N; i++) {
      Log.put(" " + real[i]);
    }

    // do inverse first...

    sing(real, imag, N, N, N, false);
    // Log.log("\nTEMP :" + temp);

    Log.log("\nNEW:");

    for (int i = 0; i < N; i++) {
      Log.put(" " + real[i]);
    }

    sing(real, imag, N, N, N, true);

    //fix_fft(real, imag, M, false);
    // Log.log("\nTEMP :" + temp);

    Log.log("\nORIGINAL:");

    for (int i = 0; i < N; i++) {
      Log.put(" " + (real[i] / N));
    }

    Log.log("\nEND");

    // do {} while (0==0);
  }

  /* The functions */

  static void radix_2(float[] a, float[] b) {
    int k1, k2;
    float ak, bk, c1, s1;
    kspan >>= 1;
    k1 = kspan + 2;

    do {
      do {
        k2 = kk + kspan;
        ak = a[k2 - 1];
        bk = b[k2 - 1];
        a[k2 - 1] = a[kk - 1] - ak;
        b[k2 - 1] = b[kk - 1] - bk;
        a[kk - 1] += ak;
        b[kk - 1] += bk;
        kk = k2 + kspan;
      } while (kk <= nn);
      kk = kk - nn;
    }
    while (kk <= jc);

    if (kk > kspan) {
      flag = 1;
    } else {
      do {
        c1 = 1.0f - cd;
        s1 = sd;
        do {
          do {
            do {
              k2 = kk + kspan;
              ak = a[kk - 1] - a[k2 - 1];
              bk = b[kk - 1] - b[k2 - 1];
              a[kk - 1] += a[k2 - 1];
              b[kk - 1] += b[k2 - 1];
              a[k2 - 1] = c1 * ak - s1 * bk;
              b[k2 - 1] = s1 * ak + c1 * bk;
              kk = k2 + kspan;
            } while (kk < nt);
            k2 = kk - nt;
            c1 = -c1;
            kk = k1 - k2;
          }
          while (kk > k2);
          ak = c1 - (cd * c1 + sd * s1);
          s1 = (sd * c1 - cd * s1) + s1;

          /***** Compensate for truncation errors   *****/
          c1 = 0.5f / (ak * ak + s1 * s1) + 0.5f;
          s1 *= c1;
          c1 *= ak;
          kk += jc;
        }
        while (kk < k2);
        k1 = k1 + inc + inc;
        kk = (k1 - kspan) / 2 + jc;
      }
      while (kk <= jc + jc);
    }
  }

  /// problems start here...
  static void radix_4(boolean inverse, float[] a, float[] b) {
    int k1, k2, k3;
    float akp, akm, ajm, ajp, bkm, bkp, bjm, bjp;
    float c1, s1, c2, s2, c3, s3;
    kspan /= 4;
    boolean cuatro_1_flag;
    boolean cuatro_3_flag = false;
    boolean cuatro_4_flag = false;
    boolean cuatro_5_flag = false;
    boolean cuatro_6_flag = false;
    boolean out_a_flag;

    k1 = 0;
    k2 = 0;
    k3 = 0;

    c1 = 0.0f;
    c2 = 0.0f;
    c3 = 0.0f;

    s1 = 0.0f;
    s2 = 0.0f;
    s3 = 0.0f;

    akp = 0.0f;
    bkp = 0.0f;
    ajp = 0.0f;
    bjp = 0.0f;

    akm = 0.0f;
    bkm = 0.0f;
    ajm = 0.0f;
    bjm = 0.0f;

    // out_a:
    do {
      out_a : do {
        out_a_flag = false;
        // cuatro_1: 
        cuatro_1a : {
          cuatro_1_flag = false;

          if (!cuatro_4_flag) {
            if (!cuatro_3_flag) {
              c1 = 1.0f;
              s1 = 0;
            }
          }
          do {
            cuatro_5a : do {
              if (!cuatro_4_flag) {
                do {
                  if (!cuatro_3_flag) {
                    k1 = kk + kspan;
                    k2 = k1 + kspan;
                    k3 = k2 + kspan;
                    // Log.log("kk:" + kk + " k2: " + k2); 
                    akp = a[kk - 1] + a[k2 - 1];
                    akm = a[kk - 1] - a[k2 - 1];
                    ajp = a[k1 - 1] + a[k3 - 1];
                    ajm = a[k1 - 1] - a[k3 - 1];
                    a[kk - 1] = akp + ajp;
                    ajp = akp - ajp;
                    bkp = b[kk - 1] + b[k2 - 1];
                    bkm = b[kk - 1] - b[k2 - 1];
                    bjp = b[k1 - 1] + b[k3 - 1];
                    bjm = b[k1 - 1] - b[k3 - 1];
                    b[kk - 1] = bkp + bjp;
                    bjp = bkp - bjp;

                    if (!inverse) { // inverse?
                      cuatro_5_flag = true;
                      break cuatro_5a; // goto cuatro_5;
                    }

                    akp = akm - bjm;
                    akm = akm + bjm;
                    bkp = bkm + ajm;
                    bkm = bkm - ajm;

                    if (s1 == 0.0f) {
                      cuatro_6_flag = true;
                      break cuatro_5a; // goto cuatro_6;
                    }
                  }

                  // cuatro_3:
                  // Log.log("got to cuatro_3"); 
                  cuatro_3_flag = false;

                  a[k1 - 1] = akp * c1 - bkp * s1;
                  b[k1 - 1] = akp * s1 + bkp * c1;
                  a[k2 - 1] = ajp * c2 - bjp * s2;
                  b[k2 - 1] = ajp * s2 + bjp * c2;
                  a[k3 - 1] = akm * c3 - bkm * s3;
                  b[k3 - 1] = akm * s3 + bkm * c3;
                  kk = k3 + kspan;
                } while (kk <= nt);
              }

              // cuatro_4: 
              cuatro_4_flag = false;
              c2 = c1 - (cd * c1 + sd * s1);
              s1 = (sd * c1 - cd * s1) + s1;

              /***** Compensate for truncation errors *****/

              c1 = 0.5f / (c2 * c2 + s1 * s1) + 0.5f;
              s1 = c1 * s1;
              c1 = c1 * c2;
              c2 = c1 * c1 - s1 * s1;
              s2 = 2.0f * c1 * s1;
              c3 = c2 * c1 - s2 * s1;
              s3 = c2 * s1 + s2 * c1;
              kk = kk - nt + jc;
            }
            while (kk <= kspan);

            if (!cuatro_6_flag) {
              if (!cuatro_5_flag) {
                kk = kk - kspan + inc;
                if (kk <= jc) {
                  cuatro_1_flag = true;
                  break cuatro_1a; /// goto cuatro_1;
                }

                if (kspan == jc) {
                  flag = 1;
                }

                out_a_flag = true;
                break out_a; // goto out;
              }

              //cuatro_5:
              cuatro_5_flag = false;
              akp = akm + bjm;
              akm = akm - bjm;
              bkp = bkm - ajm;
              bkm = bkm + ajm;
              if (s1 != 0.0f) {
                cuatro_3_flag = true;
                cuatro_1_flag = true; // !
                // Log.log("goto cuatro_3"); 
                break cuatro_1a; // goto cuatro_3;
              }
            }

            // cuatro_6:
            cuatro_6_flag = false;
            a[k1 - 1] = akp;
            b[k1 - 1] = bkp;
            b[k2 - 1] = bjp;
            a[k2 - 1] = ajp;
            a[k3 - 1] = akm;
            b[k3 - 1] = bkm;
            kk = k3 + kspan;
          }
          while (kk <= nt);
        }
      }
      while (cuatro_1_flag); //  != cuatro_1_flag);
      cuatro_4_flag = true;
    }
    while (!out_a_flag); // goto cuatro_4;

    // out: 
    // s1 = s1 + 0.0f; // ?!?
  }

  static void factorise(int n) {
    fac_des(n);

    if ((number_of_factors - (number_of_square_factors << 1)) > 1) {
      number_of_square_factors = 0;
      number_of_factors = 0;
      factor_count = 0;
      fac_desX(n);
    }
  }

  // insane code...
  /* Find prime factors of n */
  static void fac_des(int n) {
    boolean divisible_by_eight_flag = false;
    int k, j, jj;
    k = n;
    factor_count = 0;

    // divisibility by 16...
    // while ( k-(k / 16)*16 == 0 ) {
    while ((k & 0xf) == 0) {
      nfac[factor_count++] = 4;
      k >>= 4;
    }

    if ((k & 0x7) == 0) { // divisible by 8.
      divisible_by_eight_flag = true;
    }

    j = 3;
    jj = 9;
    do {
      while (k % jj == 0) {
        nfac[factor_count++] = j;
        k /= jj;
      }
      j += 2;
      jj = j * j;
    }
    while (jj <= k);

    if (k <= 4) { // deals with the lone "4"...
      number_of_square_factors = factor_count;
      nfac[factor_count] = k;
      if (k != 1)
        factor_count++;
    } else {
      if (!divisible_by_eight_flag) {
        if ((k & 0x3) == 0) {
          nfac[factor_count++] = 2;
          k >>= 2;
        }

        number_of_square_factors = factor_count; // signals how many squared 2 or 4 factors exist...
      } else {
        number_of_square_factors = factor_count;
        nfac[factor_count++] = 2;
        k >>= 1;
      }

      j = 2;
      if (divisible_by_eight_flag) {
        j++;
      }

      do {
        if (k % j == 0) {
          nfac[factor_count++] = j;
          k /= j;
        }
        j = (((j + 1) >> 1) << 1) + 1;
      } while (j <= k);
    }

    if (divisible_by_eight_flag) {
      nfac[factor_count++] = 4;
      // Log.log("added in that four!");
    }

    // add in square factors in symmetrical fashion...
    if (number_of_square_factors != 0) {
      j = number_of_square_factors;
      do {
        nfac[factor_count++] = nfac[--j];
      } while (j != 0);
    }

    // Log.log("divisible_by_eight_flag:" + divisible_by_eight_flag);

    number_of_factors = factor_count;
  }

  /* Find prime factors of n */
  static void fac_desX(int n) {
    int k, j;
    k = n;
    factor_count = 0;

    while ((k & 0x3) == 0) {
      nfac[factor_count++] = 4;
      //Log.log("Found a four!:");
      k = k >> 2;
    }

    //Log.log("k is now:" + k);

    if ((k & 0x1) == 0) {
      nfac[factor_count++] = 2;
      //Log.log("Found a two!:");
      k = k >> 1;
    }

    // find odd factors...
    j = 3;
   // jj = 9;

    while (j <= k) {
      while (k % j == 0) {
        nfac[factor_count++] = j;
        k /= j;
      }

      j += 2;
      //jj = j * j;
      // Log.log("J:" + j);
    }

    number_of_square_factors = 0;
    number_of_factors = factor_count;
    //Log.log("\nFactoring:" + n);
    //Log.log("NOF:" + number_of_factors);
    //for (int i = 0; i < number_of_factors; i++) {
    //Log.log("FactorSX:" + nfac[i]);
    //}
  }

  /* Permute the results to normal order  */
  static void permute(int ntot, int n, float[] a, float[] b) {
    int k, j, k1, k2, k3, kspnn, maxf;

    float ak, bk;
    int ii, jj;

    maxf = MAXF;
    np[0] = ks;

    ii = 0;
    k = 0;
    k2 = 0;
    k3 = 0;

    if (number_of_square_factors != 0) {
      k = number_of_square_factors + number_of_square_factors + 1;
      if (factor_count < k)
        k--;
      j = 1;
      np[k] = jc;
      do {
        np[j] = np[j - 1] / nfac[j - 1];
        np[k - 1] = np[k] * nfac[j - 1];
        j++;
        k--;
      } while (j < k);
      k3 = np[k];
      kspan = np[1];
      kk = jc + 1;
      k2 = kspan + 1;
      j = 1;

      /***** Permutation of one dimensional transform *****/
      if (n == ntot) {
        boolean ocho_30_flag;
        boolean ocho_40_flag;

        ocho_30_flag = false;
        ocho_40_flag = false;

        do {
          do {
            do {
              if (!ocho_40_flag) {
                if (!ocho_30_flag) {
                  do {
                    ak = a[kk - 1];
                    a[kk - 1] = a[k2 - 1];
                    a[k2 - 1] = ak;
                    bk = b[kk - 1];
                    b[kk - 1] = b[k2 - 1];
                    b[k2 - 1] = bk;
                    kk += inc;
                    k2 += kspan;
                  } while (k2 < ks);
                }
                // ocho_30:
                ocho_30_flag = false;
                do {
                  k2 -= np[j - 1];
                  j++;
                  k2 += np[j];
                } while (k2 > np[j - 1]);
                j = 1;
              }
              // ocho_40:
              ocho_40_flag = false;
              j = j + 0;
            }
            while (kk < k2);

            kk += inc;
            k2 += kspan;
            ocho_40_flag = true;
          }
          while (k2 < ks);

          //if (k2 < ks) {
          //break ocho_40; // goto ocho_40;
          //}
          ocho_40_flag = false;
          ocho_30_flag = true;
        }
        while (kk < ks);

        //if (kk < ks) {
        //break ocho_30; // goto ocho_30;
        //}

        jc = k3;
      } else { /* Permutation for multiple transform  */
        boolean ocho_50a_flag;
        ocho_50a_flag = false;
        // ocho_50a:
        do {
          if (!ocho_50a_flag) {
            do {
              do {
                k = kk + jc;
                do {
                  ak = a[kk - 1];
                  a[kk - 1] = a[k2 - 1];
                  a[k2 - 1] = ak;
                  bk = b[kk - 1];
                  b[kk - 1] = b[k2 - 1];
                  b[k2 - 1] = bk;
                  kk += inc;
                  k2 += inc;
                } while (kk < k);
                kk = kk + ks - jc;
                k2 = k2 + ks - jc;
              }
              while (kk < nt);
              k2 = k2 - nt + kspan;
              kk = kk - nt + jc;
            }
            while (k2 < ks);
          }

          ocho_50 : {
            ocho_50a_flag = false;
            do {
              do {
                k2 -= np[j - 1];
                j++;
                k2 += np[j];
              } while (k2 > np[j - 1]);
              j = 1;
              do {
                if (kk < k2) {
                  ocho_50a_flag = true;
                  break ocho_50; // goto ocho_50;
                }
                kk += jc;
                k2 += kspan;
              } while (k2 < ks);
            }
            while (kk < ks);
          }
        }
        while (ocho_50a_flag);

        jc = k3;
      }
    }

    if ((2 * number_of_square_factors + 1) < factor_count) {
      kspnn = np[number_of_square_factors];
      /* Permutation of square-free factors of n */
      j = factor_count - number_of_square_factors;
      nfac[j] = 1;
      do {
        nfac[j - 1] *= nfac[j];
        j--;
      } while (j != number_of_square_factors);
      number_of_square_factors++;
      nn = nfac[number_of_square_factors - 1] - 1;
      if (nn > MAXP) {
        Log.log("Product of square free factors exceeds allowed limit\n");
        System.exit(1);
      }
      jj = 0;
      j = 0;
      // break nueve_06; // goto nueve_06;
      boolean first_time = true;

      boolean nueve_02_flag; //  = true;

      do {
        // nueve_02:
        nueve_02_flag = false;
        if (!first_time) {
          jj -= k2;
          k2 = kk;
          k++;
          kk = nfac[k - 1];
        }
        nueve_02a : do {
          if (!first_time) {
            jj += kk;
            if (jj >= k2) {
              nueve_02_flag = true;
              break nueve_02a; // goto nueve_02;
            }

            np[j - 1] = jj;
          }
          // nueve_06:
          first_time = false;
          k2 = nfac[number_of_square_factors - 1];
          k = number_of_square_factors + 1;
          kk = nfac[k - 1];
          j++;
        } while (j <= nn);
      }
      while (nueve_02_flag);
      /* determine the permutation cycles  of length greater then 1 */
      j = 0;

      do {
        // break nueve_14; // goto nueve_14;
        first_time = true;
        do {
          if (!first_time) {
            do {
              k = kk;
              kk = np[k - 1];
              np[k - 1] = -kk;
            } while (kk != j);
            k3 = kk;
          }

          // nueve_14:
          first_time = false;
          do {
            j++;
            kk = np[j - 1];
          } while (kk < 0);
        }
        while (kk != j);
        np[j - 1] = -j;
      }
      while (j != nn);

      //if (j != nn) {
      //break nueve_14;
      //}

      maxf *= inc;
      /* Reorder a and b following the permutation cycles */
      //break nueve_50; // goto nueve_50;
      first_time = true;
      do {
        if (first_time) {
          do {
            do {
              j--;
            } while (np[j - 1] < 0);
            jj = jc;
            do {
              kspan = jj;
              if (jj > maxf)
                kspan = maxf;
              jj -= kspan;
              k = np[j - 1];
              kk = jc * k + ii + jj;
              k1 = kk + kspan;
              k2 = 0;
              do {
                k2++;
                at[k2 - 1] = a[k1 - 1];
                bt[k2 - 1] = b[k1 - 1];
                k1 -= inc;
              } while (k1 != kk);
              do {
                k1 = kk + kspan;
                k2 = k1 - jc * (k + np[k - 1]);
                k = -np[k - 1];
                do {
                  a[k1 - 1] = a[k2 - 1];
                  b[k1 - 1] = b[k2 - 1];
                  k1 -= inc;
                  k2 -= inc;
                } while (k1 != kk);
                kk = k2;
              }
              while (k != j);
              k1 = kk + kspan;
              k2 = 0;
              do {
                k2++;
                a[k1 - 1] = at[k2 - 1];
                b[k1 - 1] = bt[k2 - 1];
                k1 -= inc;
              } while (k1 != kk);
            }
            while (jj != 0);
          }
          while (j != 1);
        }

        first_time = false;
        // nueve_50:
        j = k3 + 1;
        nt -= kspnn;
        ii = nt - inc + 1;
      }
      while (nt >= 0);
      // k = k + 0;
    }
  }

  /**************************************************************************
  Functions for prime factor radix
  ***************************************************************************/
  static void radix_3(float[] a, float[] b) {
    int k1, k2;
    float ak, bk, aj, bj;

    do {
      do {
        k1 = kk + kspan;
        k2 = k1 + kspan;
        ak = a[kk - 1];
        bk = b[kk - 1];
        aj = a[k1 - 1] + a[k2 - 1];
        bj = b[k1 - 1] + b[k2 - 1];
        a[kk - 1] = ak + aj;
        b[kk - 1] = bk + bj;
        ak = -0.5f * aj + ak;
        bk = -0.5f * bj + bk;
        aj = (a[k1 - 1] - a[k2 - 1]) * s120;
        bj = (b[k1 - 1] - b[k2 - 1]) * s120;
        a[k1 - 1] = ak - bj;
        b[k1 - 1] = bk + aj;
        a[k2 - 1] = ak + bj;
        b[k2 - 1] = bk - aj;
        kk = k2 + kspan;
      } while (kk < nn);
      kk = kk - nn;
    }
    while (kk <= kspan);
  }

  static void radix_5(float[] a, float[] b) {
    int k1, k2, k3, k4;
    float ak, aj, bk, bj, akp, akm, ajm, ajp, aa, bkp, bkm, bjm, bjp, bb;
    float c2, s2;
    c2 = c72 * c72 - s72 * s72;
    s2 = 2 * c72 * s72;
    do {
      do {
        k1 = kk + kspan;
        k2 = k1 + kspan;
        k3 = k2 + kspan;
        k4 = k3 + kspan;
        akp = a[k1 - 1] + a[k4 - 1];
        akm = a[k1 - 1] - a[k4 - 1];
        bkp = b[k1 - 1] + b[k4 - 1];
        bkm = b[k1 - 1] - b[k4 - 1];
        ajp = a[k2 - 1] + a[k3 - 1];
        ajm = a[k2 - 1] - a[k3 - 1];
        bjp = b[k2 - 1] + b[k3 - 1];
        bjm = b[k2 - 1] - b[k3 - 1];
        aa = a[kk - 1];
        bb = b[kk - 1];
        a[kk - 1] = aa + akp + ajp;
        b[kk - 1] = bb + bkp + bjp;
        ak = akp * c72 + ajp * c2 + aa;
        bk = bkp * c72 + bjp * c2 + bb;
        aj = akm * s72 + ajm * s2;
        bj = bkm * s72 + bjm * s2;
        a[k1 - 1] = ak - bj;
        a[k4 - 1] = ak + bj;
        b[k1 - 1] = bk + aj;
        b[k4 - 1] = bk - aj;
        ak = akp * c2 + ajp * c72 + aa;
        bk = bkp * c2 + bjp * c72 + bb;
        aj = akm * s2 - ajm * s72;
        bj = bkm * s2 - bjm * s72;
        a[k2 - 1] = ak - bj;
        a[k3 - 1] = ak + bj;
        b[k2 - 1] = bk + aj;
        b[k3 - 1] = bk - aj;
        kk = k4 + kspan;
      } while (kk < nn);
      kk -= nn;
    }
    while (kk <= kspan);
  }

  static void fac_imp(float[] a, float[] b) {
    int k, kspnn, j, k1, k2, jj;
    float ak, bk, aa, bb, aj, bj;
    float c1, s1, c2, s2;
    // float  ck[23], sk[23];
    float[] ck = new float[MAXF];
    float[] sk = new float[MAXF];

    k = nfac[i - 1];
    kspnn = kspan;
    kspan /= k;
    if (k == 3) {
      radix_3(a, b);
    } else {
      if (k == 5) {
        radix_5(a, b);
      } else {
        if (k != jf) {
          jf = k;
          s1 = rad / k;
          c1 = (float) Math.cos(s1);
          s1 = (float) Math.sin(s1);
          ck[jf - 1] = 1.0f;
          sk[jf - 1] = 0.0f;
          j = 1;
          do {
            ck[j - 1] = ck[k - 1] * c1 + sk[k - 1] * s1;
            sk[j - 1] = ck[k - 1] * s1 - sk[k - 1] * c1;
            k--;
            ck[k - 1] = ck[j - 1];
            sk[k - 1] = -sk[j - 1];
            j++;
          } while (j < k);
        }
        do {
          do {
            k1 = kk;
            k2 = kk + kspnn;
            aa = a[kk - 1];
            bb = b[kk - 1];
            ak = aa;
            bk = bb;
            j = 1;
            k1 = k1 + kspan;
            do {
              k2 -= kspan;
              j++;
              at[j - 1] = a[k1 - 1] + a[k2 - 1];
              ak += at[j - 1];
              bt[j - 1] = b[k1 - 1] + b[k2 - 1];
              bk += bt[j - 1];
              j++;
              at[j - 1] = a[k1 - 1] - a[k2 - 1];
              bt[j - 1] = b[k1 - 1] - b[k2 - 1];
              k1 += kspan;
            } while (k1 < k2);
            a[kk - 1] = ak;
            b[kk - 1] = bk;
            k1 = kk;
            k2 = kk + kspnn;
            j = 1;
            do {
              k1 += kspan;
              k2 -= kspan;
              jj = j;
              ak = aa;
              bk = bb;
              aj = 0.0f;
              bj = 0.0f;
              k = 1;
              do {
                k++;
                ak = at[k - 1] * ck[jj - 1] + ak;
                bk = bt[k - 1] * ck[jj - 1] + bk;
                k++;
                aj = at[k - 1] * sk[jj - 1] + aj;
                bj = bt[k - 1] * sk[jj - 1] + bj;
                jj += j;
                if (jj > jf)
                  jj -= jf;
              } while (k < jf);
              k = jf - j;
              a[k1 - 1] = ak - bj;
              b[k1 - 1] = bk + aj;
              a[k2 - 1] = ak + bj;
              b[k2 - 1] = bk - aj;
              j++;
            }
            while (j < k);
            kk += kspnn;
          }
          while (kk <= nn);
          kk -= nn;
        }
        while (kk <= kspan);
      }
    }

    /***** Multiply by twiddle factors  *****/
    twi : if (i == factor_count) {
      flag = 1;
    } else {
      kk = jc + 1;
      do {
        c2 = 1.0f - cd;
        s1 = sd;
        do {
          c1 = c2;
          s2 = s1;
          kk += kspan;
          do {
            do {
              ak = a[kk - 1];
              a[kk - 1] = c2 * ak - s2 * b[kk - 1];
              b[kk - 1] = s2 * ak + c2 * b[kk - 1];
              kk += kspnn;
            } while (kk <= nt);
            ak = s1 * s2;
            s2 = s1 * c2 + c1 * s2;
            c2 = c1 * c2 - ak;
            kk = kk - nt + kspan;
          }
          while (kk <= kspnn);
          c2 = c1 - (cd * c1 + sd * s1);
          s1 = s1 + (sd * c1 - cd * s1);

          /***** Compensate for truncation errors *****/

          c1 = 0.5f / (c2 * c2 + s1 * s1) + 0.5f;
          s1 *= c1;
          c2 *= c1;
          kk = kk - kspnn + jc;
        }
        while (kk <= kspan);
        kk = kk - kspan + jc + inc;
      }
      while (kk <= (jc + jc));
    }
  }

  static void sing(float[] a, float[] b, int ntot, int n, int nspan, boolean inverse) {
    if (n < 2)
      System.exit(1);
    inc = inverse ? 1 : -1;
    rad = (float) (2.0f * Math.PI);
    s72 = rad / 5.0f;
    c72 = (float) Math.cos(s72);
    s72 = (float) Math.sin(s72);
    s120 = (float) Math.sqrt(0.75f);

    if (!inverse) {
      s72 = -s72;
      s120 = -s120;
      rad = -rad;
      inc = -inc;
    }

    nt = inc * ntot;
    ks = inc * nspan;
    kspan = ks;
    nn = nt - inc;
    jc = ks / n;
    radf = rad * jc * 0.5f;
    i = 0;
    jf = 0;
    flag = 0;
    factorise(n);
    // number_of_factors = 3;
    // Log.log("NOF:" + number_of_factors + "number_of_square_factors:" + number_of_square_factors);
    //nfac[0] = 4;
    //nfac[0] = 4;
    //nfac[1] = 11;
    //nfac[2] = 3;
    // nfac[3] = 4;

    do {
      // Log.log(""); // Main loop:" + flag);
      sd = radf / kspan;
      cd = 2.0f * (float) Math.sin(sd) * (float) Math.sin(sd);
      sd = (float) Math.sin(sd + sd);
      kk = 1;
      i = i + 1;

      int fac = nfac[i - 1];
      // Log.log("Factor:" +fac);

      switch (fac) {
        case 4 :
          // Log.log("radix_4 started");
          radix_4(inverse, a, b);
          break;

        case 2 :
          //Log.log("radix_2 started");
          radix_2(a, b);
          break;

        default :
          // Log.log("Odd factor started");
          fac_imp(a, b);
          break;
      }
    } while (i < number_of_factors);
    // } while (flag != 1);

    //Log.log("Permute:" + n + " - " + ntot);

    permute(ntot, n, a, b);
  }

  /* Calculates the the Fourier transform of 2*half_length real values.
  Original data values are stored alternately in arrays a and b.
  The cosine coefficients are in a[0], a[1] .........a[half_length} and
  the sine coefficients are in b[0], b[1] .........b[half_length}.
  The coeffcients must be scaled by 1/(4*half_length) in the calling
  procedure.  */

  /* April/1/92 Tried Singleton's subroutine and it does not seem to work.
  I am modifying it and folowing the procedure of Cooley, Lewis and Welch
  J. Sound Vib., vol. 12, pp 315-337, July  1970. Will extend the 
  procedure so half_length can be odd also. */

  /*	Assume we have the transform A(n) of x(even) + i x(odd)
  1-)  A1(n) = (1/2) [ Ac(-n) + A(n)] =(1/2) [Ac(N-n) + A(n)]  
  	(Ac = complex of A)    (for N even or odd)
  2-)  A2(n) = (i/2)[Ac(-n)-A(n)] =(i/2) [Ac(N-n)-A(n)]  
  	(for N even or odd)
  3-)  C(n) = (1/2)[A1(n) + A2(n)*W2N**(-n)]   (1)
  	0,1,2,3..... N   N even
  	0,1,2,3..... N-1 N odd						    
  	Use the simmetry of the A1 and A2 sequences
  	C(N-n) = (1/2) [A1(N-n) + A2(N-n)*W2N**(-N+n)] 
  		  = (1/2) [A1c(n) - A2c(n)*W2N**(n)]   (2)
  	Evaluate (1) and (2) for n=0,1,2,...N/2 -1 and (1) for N/2
  	if N is even  ((2) is also good
  	Evaluate (1) for n =0 and
  		    (1) and  (2) for n=1,2,...(N-1)/2  
  	if N is odd
  			
  	Let the factors of two be taken care in the normalization
  	outside this procedure, ie, the coefficients will be
  	four times larger. */

  /* 4/april/1992 Everything is working fine. Singleton's Realtr works ok. */
  static void realtr(float[] a, float[] b, int half_length, boolean inverse) {
    int nh, j, k;
    float sd, cd, sn, cn, a1r, a1i, a2r, a2i, re, im;
    nh = half_length >> 1; /* Should work for even and odd */
    sd = (float) ((2 * Math.PI) / half_length);
    cd = 2.0f * (float) Math.sin(sd) * (float) Math.sin(sd);
    sd = (float) Math.sin(sd + sd);
    sn = 0;
    if (!inverse) {
      cn = 1;
      a[half_length] = a[0];
      b[half_length] = b[0];
    } else {
      cn = -1;
      sd = -sd;
    }

    /* For nh odd the j = nh value is meaningless (and harmless to calculate).
    Also, the value nh /2 might be calculated twice. */
    for (j = 0; j <= nh; j++) {
      k = half_length - j;
      a1r = a[j] + a[k];
      a2r = b[j] + b[k];
      a1i = b[j] - b[k];
      a2i = -a[j] + a[k];
      re = cn * a2r + sn * a2i;
      im = -sn * a2r + cn * a2i;
      b[k] = +im - a1i;
      b[j] = im + a1i;
      a[k] = a1r - re;
      a[j] = a1r + re;
      a1r = cn - (cd * cn + sd * sn);
      sn = (sd * cn - cd * sn) + sn;
      /* compensate for truncation error */
      cn = 0.5f / (a1r * a1r + sn * sn) + 0.5f;
      sn *= cn;
      cn *= a1r;
    }
  }
}
