package com.texturegarden.effects.ca.waves;

import com.texturegarden.arrays.ComplexArray2D;
import com.texturegarden.arrays.SimpleArray2D;
import com.texturegarden.effects.ca.CAProcess2D;
import com.texturegarden.effects.ca.VNOperation;
import com.texturegarden.utilities.Log;

public class WaterReversible {
  static int old_speed;
  
  public static void animate(ComplexArray2D c_a, int type, int speed) {
    if (speed < 0 && old_speed >= 0) {
      reverseAll(c_a.r);
    } 
    
    if (speed >= 0 && old_speed < 0) {
      reverseAll(c_a.r);
    } 
    
    old_speed = speed;
    
    VNOperation vn_op = new VNOperation() {

      public int process() {
        int nc = n & 0xFFFF;
        int sc = s & 0xFFFF;
        int ec = e & 0xFFFF;
        int wc = w & 0xFFFF;
        int cc = c & 0xFFFF;
        
        int nd = ((nc - cc) << 16) >> 16;
        int sd  = ((sc - cc) << 16) >> 16;
        int ed  = ((ec - cc) << 16) >> 16;
        int wd  = ((wc - cc) << 16) >> 16;

        int vc = nd + sd + ed + wd;

        int new_v = ((vc >> 2) + cc + cc - (c >> 16)) & 0xFFFF;

        return new_v | (c << 16);
      }
    };
    CAProcess2D.generalVNOperation(c_a, vn_op);

    c_a.r.compensationForCA();
  }

  private static void reverseAll(SimpleArray2D a) {
    int[][] v = a.v;
    for(int x = a.width; --x>=0; ) {
      for(int y = a.height; --y>=0; ) {
        int c = v[x][y];
        v[x][y] = (c >>> 16) | ( c << 16);
      }
    }
  }

}
