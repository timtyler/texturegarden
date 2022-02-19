package com.texturegarden.effects.ca;

import com.texturegarden.Texture;
import com.texturegarden.arrays.SimpleArray2D;
import com.texturegarden.image.ImageWrapper;
import com.texturegarden.maps.colour.ColourMap;
import com.texturegarden.utilities.BitUtils;
import com.texturegarden.utilities.JUR;

/**
 * IsingModel
 */

/*
 * ToDo
 * ====
 * 1) Check if all gas
 * 2) move gas particles
 * 3) attach gas particles
 * 4) decrease snake tails
 *
 */

public class IsingModel {
  static Texture t;

  //static int[] temp;

  static int density = 45; // should match

  // static int frame_number = 0;

  static JUR rnd = new JUR();

  public static void animate(Texture _t) {
    t = _t;

    //if (temp == null) {
    //temp = new int[t.height]; // ?
    //}

    SimpleArray2D _r = t.c_a2D[0].r;

    _r.offset_x = _r.offset_x - 1;
    if (_r.offset_x < 0) {
      _r.offset_x += _r.width;
    }

    _r.offset_y = _r.offset_y - 1;
    if (_r.offset_y < 0) {
      _r.offset_y += _r.height;
    }

    // Log.log("!!!");
    // only do this once!!!!
    VNOperation ising_operation1 = new IsingOperation() {
      public void init() {}

        // 0001 0002 0004 0008
    // 0010 0020 0040 0080
    // 0100 0020 0400 0800
    // 1000 2000 4000 8000
    public int process() {
          //boolean c00 = (c & 0x0001) != 0;
  boolean c10 = (c & 0x0002) != 0;
        //boolean c20 = (c & 0x0004) != 0;
        boolean c30 = (c & 0x0008) != 0;

        boolean c01 = (c & 0x0010) != 0;
        //boolean c11 = (c & 0x0020) != 0;
        boolean c21 = (c & 0x0040) != 0;
        //boolean c31 = (c & 0x0080) != 0;

        //boolean c02 = (c & 0x0100) != 0;
        boolean c12 = (c & 0x0200) != 0;
        //boolean c22 = (c & 0x0400) != 0;
        boolean c32 = (c & 0x0800) != 0;

        boolean c03 = (c & 0x1000) != 0;
        //boolean c13 = (c & 0x2000) != 0;
        boolean c23 = (c & 0x4000) != 0;
        //boolean c33 = (c & 0x8000) != 0;

        boolean n03 = (n & 0x1000) != 0;
        //boolean n13 = (n & 0x2000) != 0;
        boolean n23 = (n & 0x4000) != 0;
        //boolean n33 = (n & 0x8000) != 0;

        //boolean s00 = (s & 0x0001) != 0;
        boolean s10 = (s & 0x0002) != 0;
        //boolean s20 = (s & 0x0004) != 0;
        boolean s30 = (s & 0x0008) != 0;

        boolean w30 = (w & 0x0008) != 0;
        // boolean w31 = (w & 0x0080) != 0;
        boolean w32 = (w & 0x0800) != 0;
        // boolean w33 = (w & 0x8000) != 0;

        // boolean e00 = (e & 0x0001) != 0;
        boolean e01 = (e & 0x0010) != 0;
        // boolean e02 = (e & 0x0100) != 0;
        boolean e03 = (e & 0x1000) != 0;

        // N, W, E, S) != 0;

        return c ^ ((onlyTwoSet(n03, w30, c10, c01) ? 0x0001 : 0) |
        //(onlyTwoSet(n13, c00, c20, c11) ? 0x0002 : 0) |
         (onlyTwoSet(n23, c10, c30, c21) ? 0x0004 : 0) |
        //(onlyTwoSet(n33, c20, e00, c31) ? 0x0008 : 0) |

        //(onlyTwoSet(c00, w31, c11, c02) ? 0x0010 : 0) |
         (onlyTwoSet(c10, c01, c21, c12) ? 0x0020 : 0) |
        //(onlyTwoSet(c20, c11, c31, c22) ? 0x0040 : 0) |
         (onlyTwoSet(c30, c21, e01, c32) ? 0x0080 : 0) | (onlyTwoSet(c01, w32, c12, c03) ? 0x0100 : 0) |
        //(onlyTwoSet(c11, c02, c22, c13) ? 0x0200 : 0) |
         (onlyTwoSet(c21, c12, c32, c23) ? 0x0400 : 0) |
        //(onlyTwoSet(c31, c22, e02, c33) ? 0x0800 : 0) |

        //(onlyTwoSet(c02, w33, c13, s00) ? 0x1000 : 0) |
         (onlyTwoSet(c12, c03, c23, s10) ? 0x2000 : 0) |
        //(onlyTwoSet(c22, c13, c33, s20) ? 0x4000 : 0) |
         (onlyTwoSet(c32, c23, e03, s30) ? 0x8000 : 0));
      }
    };

    VNOperation ising_operation2 = new IsingOperation() {
        // 0001 0002 0004 0008
    // 0010 0020 0040 0080
    // 0100 0020 0400 0800
    // 1000 2000 4000 8000
    public  int process() {
        boolean c00 = (c & 0x0001) != 0;
        //boolean c10 = (c & 0x0002) != 0;
        boolean c20 = (c & 0x0004) != 0;
        //boolean c30 = (c & 0x0008) != 0;

        //boolean c01 = (c & 0x0010) != 0;
        boolean c11 = (c & 0x0020) != 0;
        //boolean c21 = (c & 0x0040) != 0;
        boolean c31 = (c & 0x0080) != 0;

        boolean c02 = (c & 0x0100) != 0;
        //boolean c12 = (c & 0x0200) != 0;
        boolean c22 = (c & 0x0400) != 0;
        //boolean c32 = (c & 0x0800) != 0;

        //boolean c03 = (c & 0x1000) != 0;
        boolean c13 = (c & 0x2000) != 0;
        //boolean c23 = (c & 0x4000) != 0;
        boolean c33 = (c & 0x8000) != 0;

        //boolean n03 = (n & 0x1000) != 0;
        boolean n13 = (n & 0x2000) != 0;
        //boolean n23 = (n & 0x4000) != 0;
        boolean n33 = (n & 0x8000) != 0;

        boolean s00 = (s & 0x0001) != 0;
        //boolean s10 = (s & 0x0002) != 0;
        boolean s20 = (s & 0x0004) != 0;
        //boolean s30 = (s & 0x0008) != 0;

        //boolean w30 = (w & 0x0008) != 0;
        boolean w31 = (w & 0x0080) != 0;
        //boolean w32 = (w & 0x0800) != 0;
        boolean w33 = (w & 0x8000) != 0;

        boolean e00 = (e & 0x0001) != 0;
        //boolean e01 = (e & 0x0010) != 0;
        boolean e02 = (e & 0x0100) != 0;
        //boolean e03 = (e & 0x1000) != 0;

        // N, W, E, S;

        return c ^ (
        //(onlyTwoSet(n03, w30, c10, c01) ? 0x0001 : 0) |
         (onlyTwoSet(n13, c00, c20, c11) ? 0x0002 : 0) |
        //(onlyTwoSet(n23, c10, c30, c21) ? 0x0004 : 0) |
         (onlyTwoSet(n33, c20, e00, c31) ? 0x0008 : 0) | (onlyTwoSet(c00, w31, c11, c02) ? 0x0010 : 0) |
        //(onlyTwoSet(c10, c01, c21, c12) ? 0x0020 : 0) |
         (onlyTwoSet(c20, c11, c31, c22) ? 0x0040 : 0) |
        //(onlyTwoSet(c30, c21, e01, c32) ? 0x0080 : 0) |

        //(onlyTwoSet(c01, w32, c12, c03) ? 0x0100 : 0) |
         (onlyTwoSet(c11, c02, c22, c13) ? 0x0200 : 0) |
        //(onlyTwoSet(c21, c12, c32, c23) ? 0x0400 : 0) |
         (onlyTwoSet(c31, c22, e02, c33) ? 0x0800 : 0) | (onlyTwoSet(c02, w33, c13, s00) ? 0x1000 : 0) |
        //(onlyTwoSet(c12, c03, c23, s10) ? 0x2000 : 0) |
         (onlyTwoSet(c22, c13, c33, s20) ? 0x4000 : 0));
        //(onlyTwoSet(c32, c23, e03, s30) ? 0x8000 : 0));
      }
    };

    if ((t.generation++ & 1) == 0) {
      CAProcess2D.generalVNOperation(t.c_a2D[0], ising_operation1);
    } else {
      CAProcess2D.generalVNOperation(t.c_a2D[0], ising_operation2);
    }
  }

  static boolean onlyTwoSet(boolean b1, boolean b2, boolean b3, boolean b4) {
    if (b1 && b2 && !b3 && !b4) {
      return true;
    }

    if (!b1 && !b2 && b3 && b4) {
      return true;
    }

    if (!b1 && b2 && !b3 && b4) {
      return true;
    }

    if (b1 && !b2 && b3 && !b4) {
      return true;
    }

    if (!b1 && b2 && b3 && !b4) {
      return true;
    }

    if (b1 && !b2 && !b3 && b4) {
      return true;
    }

    return false;
  }

  public static void render(Texture _t) {
    // void makeImageUsingColourMapMargolus(TTImage ttimage, ColourMap colour_map) {
    ImageWrapper ttimage = _t.texture_image;
    ColourMap colour_map = _t.main_colour_map;
    SimpleArray2D _r = _t.c_a2D[0].r;

    int c = 0;
    int _v; // , _w;
    int _w1;
    int inverting = _t.applet.global_settings.colour_invert ? 0xFFFFFF : 0;

    int[] int_array = ttimage.getSource();

    // arraycopy?
    //if (_r.int_array == null) {
    //_r.int_array = new int[_r.width * _r.height]; // new array not needed - it needs to be zero'd!
    //}

    for (int y = _r.offset_y; y < _r.offset_y + _r.height; y++) {
      for (int x = _r.offset_x; x < _r.offset_x + _r.width; x++) {
        _v = _r.v[(x < _r.width) ? x : x - _r.width][(y < _r.height) ? y : y - _r.height];
        // _w1 = BitUtils.bitCount((short)(_v & 0x0F)) * 0x30;
        _w1 = BitUtils.bitCount((short) (_v)) * 0xF;
        int_array[c++] = (colour_map.colour[_w1]) ^ inverting;
      }
    }
  }

  public static void mess(Texture _t) {
    SimpleArray2D _r = _t.c_a2D[0].r;
    _r.resetOffsets();

    for (int x = 0; x < _r.width; x++) {
      for (int y = 0; y < _r.height; y++) {
        if (x < y) {
          _r.v[x][y] = (rnd.nextInt(100) < density) ? 0xFFFF : 0;
        } else {
          _r.v[x][y] = (rnd.nextInt(100) < density) ? 0 : 0xFFFF;
        }
      }
    }
  }

  public static void randomise(Texture _t) {
    SimpleArray2D _r = _t.c_a2D[0].r;
    _r.resetOffsets();

    for (int x = 0; x < _r.width; x++) {
      for (int y = 0; y < _r.height; y++) {
        _r.v[x][y] = (rnd.nextInt(100) < density) ? 0xFFFF : 0;
      }
    }
  }

  public static void setDensity(int fa) {
    density = fa;
  }

  public static int getDensity() {
    return density;
  }
}
