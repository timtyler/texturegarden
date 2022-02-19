package com.texturegarden.utilities;
/*
// Integer Square Root function
// Contributors include Arne Steinarson for the basic approximation idea, Dann 
// Corbit and Mathew Hendry for the first cut at the algorithm, Lawrence Kirby 
// for the rearrangement, improvments and range optimization, Paul Hsieh 
// for the round-then-adjust idea, and Tim Tyler, for the Java port.
*/

public class SquareRoot {
  private static int xn;

  final static int[] sqq_table = {
     0,    16,  22,  27,  32,  35,  39,  42,  45,  48,  50,  53,  55,  57,
     59,   61,  64,  65,  67,  69,  71,  73,  75,  76,  78,  80,  81,  83,
     84,   86,  87,  89,  90,  91,  93,  94,  96,  97,  98,  99, 101, 102,
     103, 104, 106, 107, 108, 109, 110, 112, 113, 114, 115, 116, 117, 118,
     119, 120, 121, 122, 123, 124, 125, 126, 128, 128, 129, 130, 131, 132,
     133, 134, 135, 136, 137, 138, 139, 140, 141, 142, 143, 144, 144, 145,
     146, 147, 148, 149, 150, 150, 151, 152, 153, 154, 155, 155, 156, 157,
     158, 159, 160, 160, 161, 162, 163, 163, 164, 165, 166, 167, 167, 168,
     169, 170, 170, 171, 172, 173, 173, 174, 175, 176, 176, 177, 178, 178,
     179, 180, 181, 181, 182, 183, 183, 184, 185, 185, 186, 187, 187, 188,
     189, 189, 190, 191, 192, 192, 193, 193, 194, 195, 195, 196, 197, 197,
     198, 199, 199, 200, 201, 201, 202, 203, 203, 204, 204, 205, 206, 206,
     207, 208, 208, 209, 209, 210, 211, 211, 212, 212, 213, 214, 214, 215,
     215, 216, 217, 217, 218, 218, 219, 219, 220, 221, 221, 222, 222, 223,
     224, 224, 225, 225, 226, 226, 227, 227, 228, 229, 229, 230, 230, 231,
     231, 232, 232, 233, 234, 234, 235, 235, 236, 236, 237, 237, 238, 238,
     239, 240, 240, 241, 241, 242, 242, 243, 243, 244, 244, 245, 245, 246,
     246, 247, 247, 248, 248, 249, 249, 250, 250, 251, 251, 252, 252, 253,
     253, 254, 254, 255
  };

  public static int sqrt(int x) {
    if (x >= 0x10000) {
      if (x >= 0x1000000) {
        if (x >= 0x10000000) {
          if (x >= 0x40000000) {
            if (x >= 65535 * 65535) {
              return 65535;
            }
            xn = sqq_table[x >> 24] << 8;
          } else {
            xn = sqq_table[x >> 22] << 7;
          }
        } else {
          if (x >= 0x4000000) {
            xn = sqq_table[x >> 20] << 6;
          } else {
            xn = sqq_table[x >> 18] << 5;
          }
        }
      } else {
        if (x >= 0x100000) {
          if (x >= 0x400000) {
            xn = sqq_table[x >> 16] << 4;
          } else {
            xn = sqq_table[x >> 14] << 3;
          }
        } else {
          if (x >= 0x40000) {
            xn = sqq_table[x >> 12] << 2;
          } else {
            xn = sqq_table[x >> 10] << 1;
          }
        }

        xn = (xn + 1 + (x / xn)) >> 1;

        if ((xn * xn) > x) {
          xn--;
        }

        return xn;
      }
    } else {
      if (x >= 0x100) {
        if (x >= 0x1000)
          if (x >= 0x4000) {
            xn = (sqq_table[x >> 8]) + 1;
          } else {
            xn = (sqq_table[x >> 6] >> 1) + 1;
          }
        else {
          if (x >= 0x400) {
            xn = (sqq_table[x >> 4] >> 2) + 1;
          } else {
            xn = (sqq_table[x >> 2] >> 3) + 1;
          }
        }
        if (xn * xn > x) {
          xn--;
        }

        return xn;
      } else {
        if (x >= 0) {
          return sqq_table[x] >> 4;
        }
      }
    }

    return -1; // error - or -ve arguments...
  }

  // Really fast, but not at all accurate...
  static int approx_sqrt(int n) {
    if (n >= 0x10000)
      if (n >= 0x1000000)
        if (n >= 0x10000000)
          if (n >= 0x40000000)
            return (sqq_table[n >> 24] << 8);
          else
            return (sqq_table[n >> 22] << 7);
        else if (n >= 0x4000000)
          return (sqq_table[n >> 20] << 6);
        else
          return (sqq_table[n >> 18] << 5);
      else if (n >= 0x100000)
        if (n >= 0x400000)
          return (sqq_table[n >> 16] << 4);
        else
          return (sqq_table[n >> 14] << 3);
      else if (n >= 0x40000)
        return (sqq_table[n >> 12] << 2);
      else
        return (sqq_table[n >> 10] << 1);
    else if (n >= 0x100)
      if (n >= 0x1000)
        if (n >= 0x4000)
          return (sqq_table[n >> 8]);
        else
          return (sqq_table[n >> 6] >> 1);
      else if (n >= 0x400)
        return (sqq_table[n >> 4] >> 2);
      else
        return (sqq_table[n >> 2] >> 3);
    else if (n >= 0x10)
      if (n >= 0x40)
        return (sqq_table[n] >> 4);
      else
        return (sqq_table[n << 2] << 5);
    else if (n >= 0x4)
      return (sqq_table[n >> 4] << 6);
    else
      return (sqq_table[n >> 6] << 7);
  }

  /*
  // totally hopeless...
     static int test_sqrt(int n) {
        float r = 2.0F;
        float s = 0.0F;
        for(; r < (float)n / r; r *= 2.0F);
        for(s = (r + (float)n / r) / 2.0F; r - s > 1.0F; s = (r + (float)n / r) / 2.0F) {
           r = s;
        }
     
        return (int)s;
     }
  	*/

  /*
     public static void main(String args[]) {
        int i;
        int temp;
        long newtime;
        long oldtime;
        int N = 8000000;
     
        oldtime = System.currentTimeMillis();
     
        for (i = 0; i < N; i++) {
           temp = sqrt(i & 1023);
        }
     
        newtime = System.currentTimeMillis();
        debug("sqrt:" + (newtime - oldtime));
     
     
        oldtime = System.currentTimeMillis();
     
        for (i = 0; i < N; i++) {
           temp = (int)(java.lang.Math.sqrt(i & 1023));
        }
     
        newtime = System.currentTimeMillis();
        debug("java.lang.Math.sqrt():" + (newtime - oldtime));
     
     
        oldtime = System.currentTimeMillis();
     
        for (i = 0; i < N; i++) {
           temp = approx_sqrt(i & 1023);
        }
     
        newtime = System.currentTimeMillis();
        debug("approx_sqrt:" + (newtime - oldtime));
     
        oldtime = System.currentTimeMillis();
        for (i = 0; i < N; i++) {
           temp = test_sqrt(i & 1023);
        }
     
        newtime = System.currentTimeMillis();
        debug("test_sqrt:" + (newtime - oldtime));
     
     
        // for (i = 0; i < 20; i++) {
          // debug("sqrt(" + i + ")=" + sqrt(i));
        // }
     
        do {
        } while (true);
     
     }
  */

  /*
     final static void debug(String o) {
        System.out.println(o);
     }
  
     public String returnName() {
        return "SquareRoot";
     }
  	*/
}
