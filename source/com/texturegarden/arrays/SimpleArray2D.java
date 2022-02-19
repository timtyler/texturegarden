package com.texturegarden.arrays;

import com.texturegarden.Texture;
import com.texturegarden.TG;
import com.texturegarden.effects.ca.DiffusionLimitedAggregation;
import com.texturegarden.image.ImageWrapper;
import com.texturegarden.maps.colour.ColourFunction;
import com.texturegarden.maps.colour.ColourMap;
import com.texturegarden.program.Program;
import com.texturegarden.utilities.JUR;

public class SimpleArray2D {
  public int[][] v;

  public int width;
  public int height;

  public int offset_x;
  public int offset_y;

  public JUR rnd = new JUR();
  public JUR rnd2 = new JUR();

  // TextureGarden applet;
  public SimpleArray2D(int w, int h) {
    width = w;
    height = h;

    v = new int[width][height];
  }

  public void setSeed(int s) {
    rnd.setSeed(s);
    rnd2.setSeed(s);
  }

  public void equaliseFast() {
    int max = 0;
    int max_range = 0xFFFFFF;

    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        if (v[x][y] > max) {
          max = v[x][y];
        }
      }
    }

    if (max <= 0) {
      max = 1;
      max_range = 0;
    }

    int scale_factor = max_range / max;
    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        v[x][y] = (v[x][y] * scale_factor) >>> 8;
      }
    }
  }

  public void equaliseFully() {
    int max = 0x80000000;
    int min = 0x7FFFFFFF;
    int max_range = 0xFFFFFF;
    int _v;

    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        _v = v[x][y];
        if (_v > max) {
          max = _v;
        } else {
          if (_v < min) {
            min = _v;
          }
        }
      }
    }

    if (max == min) {
      max++;
      max_range = 0;
    }

    int scale_factor = max_range / (max - min);
    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        v[x][y] = ((v[x][y] - min) * scale_factor) >>> 8;
      }
    }
  }

  // use "sawtooth" instead...
  // concertina
  /*
     void concertina(int mul) {
        int o;
     
        for (int x = 0; x < width; x++) {
           for (int y = 0; y < height; y++) {
              o = (v[x][y] * mul) >>> 10;
              if ((o & 0x10000) != 0) {
                 v[x][y] = (o & 0xFFFF) ^ 0xFFFF;
              }
              else
              {
                 v[x][y] = o & 0xFFFF;
              }
           }
        }
     }
  	*/

  // slow?
  public int safeRead(int x, int y) {
    while (y < 0) {
      y += height;
    }
    while (x < 0) {
      x += width;
    }

    while (y >= height) {
      y -= height;
    }

    while (x >= width) {
      x -= width;
    }

    return v[x][y];
  }

  public void safeWrite(int x, int y, int _r) {
    while (y < 0) {
      y += height;
    }
    while (x < 0) {
      x += width;
    }

    while (y >= height) {
      y -= height;
    }

    while (x >= width) {
      x -= width;
    }

    v[x][y] = _r;
  }

  public int safeReadWithOffset(int x, int y) {
    x += (offset_x * width) >>> 16;
    y += (offset_y * height) >>> 16;

    while (y < 0) {
      y += height;
    }
    while (x < 0) {
      x += width;
    }

    while (y >= height) {
      y -= height;
    }

    while (x >= width) {
      x -= width;
    }

    return v[x][y];
  }

  public void safeWriteWithOffset(int x, int y, int _r) {
    x += (offset_x * width) >>> 16;
    y += (offset_y * height) >>> 16;

    while (y < 0) {
      y += height;
    }
    while (x < 0) {
      x += width;
    }

    while (y >= height) {
      y -= height;
    }

    while (x >= width) {
      x -= width;
    }

    v[x][y] = _r;
  }

  // x,y,w,h = 0-FFFF;
  public void fillRect(int x, int y, int w, int h, int v) {
    int ax = (x * width) >> 16;
    int ay = (y * height) >> 16;
    int aw = (w * width) >> 16;
    int ah = (h * height) >> 16;
    for (int cx = 0; cx < aw; cx++) {
      for (int cy = 0; cy < ah; cy++) {
        safeWriteWithOffset(ax + cx, ay + cy, v);
      }
    }
  }

  public void fillCircle(int x, int y, int w, int h, int v) {
    int ax = (x * width) >> 16;
    int ay = (y * height) >> 16;
    int aw = (w * width) >> 16;
    //int ah = (h * height) >> 16;
    int cr = aw >> 1;
    int crr = cr * cr;

    for (int cx = -cr; cx < cr; cx++) {
      for (int cy = -cr; cy < cr; cy++) {
        int ss = cx * cx + cy * cy;

        if (ss < crr) {
          int av = v;
          //if (crr - ss < 512) {
            //av = (av * (crr - ss)) >> 9;
          //}
          int ev = av; //  + (safeReadWithOffset(ax + cx + cr, ay + cy) & 0xFFFF);
          if (ev > 0xFFFF) {
            ev = 0xFFFF;
          }
         if (ev < 0) {
            ev = 0;
         }
          safeWriteWithOffset(ax + cx + cr, ay + cy + cr, ev | ev << 16);
        }
      }
    }
  }

  public void clear() {
    ArrayClearer.clear(v);
  }

  public void fill(int k) {
    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        v[x][y] = k;
      }
    }
  }

  public void randomise() {
    if (TG.diffusion_limited_aggregation) {
      diffusionLimitedAggregationBasicSetup();
      for (int x = 0; x < width; x++) {
        for (int y = 0; y < 16; y++) {
          v[x][y] = (y << 4) | (y << 12) | (y << 20) | (y << 28);
        }
      }
    } else {
      for (int x = 0; x < width; x++) {
        for (int y = 0; y < height; y++) {
          int n = (y * 8) / height;
          int r = 0xFFFFFFFF;
          for (int i = 0; i < n; i++) {
            r = r & rnd.nextInt();
          }

          switch ((x * 8) / width) {
            case 0 :
              r |= 0xFFFF;
              break;
            case 1 :
              r = 0;
              break;
            case 2 :
              r &= 0xFFFF0000;
              break;
            case 3 :
              break;
            case 4 :
              r &= 0xFFFF;
              break;
            case 5 :
              r |= 0xFFFFFFFF;
              break;
            case 6 :
              r = 0x0;
              break;
            case 7 :
              break;
          }

          //if (TextureGarden.fractal_drainage) {
          // r = ((x << 16) / width) << 16;
          // r = r | 0x8000;
          //}

          v[x][y] = r;
        }
      }
    }

    resetOffsets();
  }

  public void resetOffsets() {
    offset_x = 0;
    offset_y = 0;
  }

  public void rdSquare() {
    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        if ((x > (width >> 2)) && (x < 3 * (width >> 2)) && (y > (height >> 2)) && (y < 3 * (height >> 2))) {
          v[x][y] = 0xFFFF0000;
        } else {
          v[x][y] = 0xFFFF;
        }
      }
    }

    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        if ((x > (width >> 2)) && (x < (width >> 1)) && (y > (height >> 2)) && (y < 3 * (height >> 3))) {
          v[x][y] = 0xFFFF;
        }
      }
    }

    resetOffsets();
  }

  public void drainagePatternSetup() {
    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        if ((x > (width >> 3)) && (x < 7 * (width >> 3)) && (y > (height >> 3)) && (y < 7 * (height >> 3))) {
          v[x][y] = rnd.nextInt();
        } else {
          // v[x][y] = rnd.nextInt() << 16;
        }
      }
    }

    resetOffsets();
  }

  public void diffusionLimitedAggregationSetup() {
    diffusionLimitedAggregationBasicSetup();

    v[width >> 1][height >> 1] = 0xFFFFFFFF;
  }

  void diffusionLimitedAggregationBasicSetup() {
    //Log.log("diffusionLimitedAggregationSetup");
    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        if (rnd.nextInt(100) < DiffusionLimitedAggregation.density) {
          // if ((x > (width >> 3)) && (x < 7 * (width >> 3)) && (y > (height >> 3)) && (y < 7 * (height >> 3))) {
          v[x][y] = 0x01010101; // rnd.nextInt() & 
        } else {
          v[x][y] = 0;
        }
      }
    }

    resetOffsets();
  }

  void clearRealUpper() {
    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        v[x][y] &= 0xFFFF;
      }
    }
  }

  void invertUpper() {
    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        v[x][y] = (v[x][y] & 0xFFFF) | (((v[x][y] ^ 0xFFFF0000) + 0x10000) & 0xFFFF0000);
      }
    }
  }

  void wavifyUpper() {
    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        v[x][y] = (v[x][y] & 0xFFFF) | (v[x][y] << 16);
      }
    }
  }

  // crudely checked for +ve overflow only...
  int getPointAAX(int x, int y) {
    int rv_0;
    int rv_1;
    int _x_0;
    int _x_1;

    if (y >= height) {
      y -= height;
    }

    _x_0 = x >> 8;

    while (_x_0 >= width) {
      _x_0 -= width;
    }

    while (_x_0 < 0) {
      _x_0 += width;
    }

    _x_1 = _x_0 + 1;

    if (_x_1 >= width) {
      _x_1 = 0;
    }

    rv_0 = v[_x_0][y];
    rv_1 = v[_x_1][y];

    x &= 0xff;

    if ((rv_0 - rv_1 > 0xC000) || (rv_1 - rv_0 > 0xC000)) {
      if (rv_0 > 0xC000) {
        return ((((rv_1 + 0x10000) * x) + (rv_0 * (0xff - x))) >> 8) & 0xFFFF;
      } else {
        return (((rv_1 * x) + ((rv_0 + 0x10000) * (0xff - x))) >> 8) & 0xFFFF;
      }
    }

    return ((rv_1 * x) + (rv_0 * (0xff - x))) >> 8;
  }

  // crudely checked for +ve overflow only...
  int getPointAAY(int x, int y) {
    int rv_0;
    int rv_1;
    int _y_0;
    int _y_1;

    if (x > width - 1) {
      x = 0;
    }

    _y_0 = y >> 8;

    while (_y_0 >= height) {
      _y_0 -= height;
    }

    while (_y_0 < 0) {
      _y_0 += height;
    }

    _y_1 = _y_0 + 1;

    if (_y_1 >= height) {
      _y_1 -= height;
    }

    rv_0 = v[x][_y_0];
    rv_1 = v[x][_y_1];

    y &= 0xff;

    if ((rv_0 - rv_1 > 0xC000) || (rv_1 - rv_0 > 0xC000)) {
      if (rv_0 > 0xC000) {
        return ((((rv_1 + 0x10000) * y) + (rv_0 * (0xff - y))) >> 8) & 0xFFFF;
      } else {
        return (((rv_1 * y) + ((rv_0 + 0x10000) * (0xff - y))) >> 8) & 0xFFFF;
      }
    }

    return ((rv_1 * y) + (rv_0 * (0xff - y))) >> 8;
  }

  // modulo performed...
  // offset_x considered...
  public int getPointAA(int x, int y) {
    int rv_0;
    int rv_s;
    int rv_e;
    int rv_se;

    int _x_0;
    int _x_1;
    int _y_0;
    int _y_1;

    // x += offset_x << 8;
    // y += offset_y << 8;

    /*
       while (y < 0) {
          y += height << 8;
       }
    
       while (x < 0) {
          x += width << 8;
       }
    
       while (y >= height << 8) {
          y -= height << 8;
       }
    
       while (x >= width << 8) {
          x -= width << 8;
       }
    	*/

    x = (x * width) >> 8;
    y = (y * height) >> 8;

    _y_0 = y >> 8;

    _y_1 = _y_0 + 1;

    if (_y_1 >= height) {
      _y_1 -= height;
    }

    _x_0 = x >> 8;

    _x_1 = _x_0 + 1;

    if (_x_1 >= width) {
      _x_1 = 0;
    }

    rv_0 = v[_x_0][_y_0];
    rv_s = v[_x_0][_y_1];
    rv_e = v[_x_1][_y_0];
    rv_se = v[_x_1][_y_1];

    x &= 0xff;
    y &= 0xff;

    int cnt_low = 0;
    int cnt_high = 0;

    if (rv_0 < 0x4000) {
      cnt_low++;
    }

    if (rv_0 > 0xC000) {
      cnt_high++;
    }

    if (rv_se < 0x4000) {
      cnt_low++;
    }

    if (rv_se > 0xC000) {
      cnt_high++;
    }

    if (x > y) {
      if (rv_e < 0x4000) {
        cnt_low++;
      }
      if (rv_e > 0xC000) {
        cnt_high++;
      }

      if ((cnt_low != 0) && (cnt_high != 0)) {
        if (rv_0 < 0x4000) {
          rv_0 += 0x10000;
        }

        if (rv_se < 0x4000) {
          rv_se += 0x10000;
        }

        if (rv_e < 0x4000) {
          rv_e += 0x10000;
        }

        return (rv_0 + (((y * (rv_se - rv_e)) - (x * (rv_0 - rv_e))) >> 8)) & 0xFFFF;
      } else {
        return rv_0 + (((y * (rv_se - rv_e)) - (x * (rv_0 - rv_e))) >> 8);
      }
    }

    if (rv_s < 0x4000) {
      cnt_low++;
    }
    if (rv_s > 0xC000) {
      cnt_high++;
    }

    if ((cnt_low != 0) && (cnt_high != 0)) {
      if (rv_0 < 0x4000) {
        rv_0 += 0x10000;
      }

      if (rv_se < 0x4000) {
        rv_se += 0x10000;
      }

      if (rv_s < 0x4000) {
        rv_s += 0x10000;
      }

      return (rv_0 + (((x * (rv_se - rv_s)) - (y * (rv_0 - rv_s))) >> 8)) & 0xFFFF;
    } else {
      return rv_0 + (((x * (rv_se - rv_s)) - (y * (rv_0 - rv_s))) >> 8);
    }
  }

  // shift - the bigger the shift, the less warping...
  void displacementWarp(SimpleArray2D underlying_c_a, SimpleArray2D displacement_c_a, int shift) {
    int dx;
    int dy;

    int disp_x = 8;
    int disp_y = 8;

    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        dx = displacement_c_a.safeReadWithOffset(x + disp_x, y + disp_y) & 0xffff;
        dy = displacement_c_a.safeReadWithOffset(x - disp_x, y - disp_y) & 0xffff;

        v[x][y] = underlying_c_a.getPointAA((x << 8) + (dx >> shift), (y << 8) + (dy >> shift));
      }
    }
  }

  public void makeImageUsingColourMap(ImageWrapper ttimage, ColourMap colour_map, boolean hflip, boolean vflip, int xor) {
    ColourFunction f = new ColourFunction(colour_map, xor) {
      public int getColour(int v) {
        return this.colour_map.colour[(v >> 8) & 0xFF] ^ this.xor;
      }
    };

    makeImageUsingColourMap(ttimage, colour_map, f, hflip, vflip);
  }

  public void makeImageUsingColourMapMargolus(ImageWrapper ttimage, ColourMap colour_map, boolean hflip, boolean vflip, int xor) {
    ColourFunction f = new ColourFunction(colour_map, xor) {
      public int getColour(int v) {
          // return v & 0xFFFF;
  int _w1 = v & 0xFF;
        int _w2 = ((v >> 8) & 0xFF);
        int _w3 = ((v >> 16) & 0xFF);
        int _w4 = (v >>> 24);
        return (((this.colour_map.colour[_w1] >> 2) & 0x3F3F3F) + ((this.colour_map.colour[_w2] >> 2) & 0x3F3F3F) + ((this.colour_map.colour[_w3] >> 2) & 0x3F3F3F) + ((this.colour_map.colour[_w4] >> 2) & 0x3F3F3F)) ^ this.xor;
      }
    };

    makeImageUsingColourMap(ttimage, colour_map, f, hflip, vflip);
  }

  // considers "offset" values..
  public void makeImageUsingColourMap(ImageWrapper ttimage, ColourMap colour_map, ColourFunction mf, boolean hflip, boolean vflip) {
    int c = 0;
    //int _v, _w;

    int ox = (offset_x * width) >>> 16;
    int oy = (offset_y * height) >>> 16;

    int x_start = hflip ? ox + width : ox;
    int x_end = hflip ? ox : ox + width;
    int x_dir = hflip ? -1 : 1;

    int y_start = vflip ? oy + width : oy;
    int y_end = vflip ? oy : oy + width;
    int y_dir = vflip ? -1 : 1;

    int[] int_array = ttimage.getSource();

    /*
       if ((ox | oy) == 0) { // zero offset?  Great - do things as quick as possible...
          for (int y = 0; y < height; y++) {
             for (int x = 0; x < width; x++) {
                _w = ((v[x][y] >>> 8)) & 0xFF;
                int_array[c++] = colour_map.colour[_w];
             }
          }
       }
       else // deal with offset if it exists...
       {
       */

    for (int y = y_start; y != y_end; y += y_dir) {
      for (int x = x_start; x != x_end; x += x_dir) {
        int_array[c++] = mf.getColour(v[(x < width) ? x : x - width][(y < height) ? y : y - height]);
      }
    }
  }

  // AddNoise (NOT zero!)
  void addStatic(int intensity) {
    int _v;

    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        _v = v[x][y] + rnd.nextInt(intensity);

        v[x][y] = _v;
      }
    }
  }

  // SubtractNoise
  public void subtractStatic(int intensity) {
    int _v;

    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        _v = v[x][y] - rnd.nextInt(intensity);

        v[x][y] = _v;
      }
    }
  }

  public void horizontalSmearQuick(Texture t) {
    SimpleArray1D s_H1 = t.c_a1D[0].r;
    SimpleArray2D s_H2 = t.c_a2D[0].r;
    Program p = t.program;

    int i_ip = p.instruction_pointer;

    for (int y = 0; y < height; y++) {
      t.e = s_H1.v[y];
      for (int x = 0; x < width; x++) {
        p.instruction_pointer = i_ip;
        t.c = s_H2.v[x][y];
        s_H2.v[x][y] = p.getArgument();
      }
    }
  }

  public void verticalSmearQuick(Texture t) {
    SimpleArray1D s_H1 = t.c_a1D[0].r;
    SimpleArray2D s_H2 = t.c_a2D[0].r;
    Program p = t.program;

    int i_ip = p.instruction_pointer;
    for (int x = 0; x < width; x++) {
      t.e = s_H1.v[x];
      for (int y = 0; y < height; y++) {
        p.instruction_pointer = i_ip;
        t.c = s_H2.v[x][y];
        s_H2.v[x][y] = p.getArgument();
      }
    }
  }

  public void horizontalSmear(Texture t) {
    // SimpleArray1D s_H1 = t.c_a1D[0].r;
    SimpleArray2D s_H2 = t.c_a2D[0].r;
    Program p = t.program;

    int ym = 0x80000000 / height;

    int i_ip1 = p.instruction_pointer;
    p.getArgument();
    int i_ip2 = p.instruction_pointer;

    for (int y = 0; y < height; y++) {
      p.instruction_pointer = i_ip1;
      t.y = (y * ym) >> 15;
      t.e = p.getArgument();
      for (int x = 0; x < width; x++) {
        p.instruction_pointer = i_ip2;
        t.c = s_H2.v[x][y];
        s_H2.v[x][y] = p.getArgument();
      }
    }
  }

  public void verticalSmear(Texture t) {
    //SimpleArray1D s_H1 = t.c_a1D[0].r;
    SimpleArray2D s_H2 = t.c_a2D[0].r;
    Program p = t.program;

    int xm = 0x80000000 / width;

    int i_ip1 = p.instruction_pointer;
    p.getArgument();
    int i_ip2 = p.instruction_pointer;

    for (int x = 0; x < width; x++) {
      p.instruction_pointer = i_ip1;
      t.x = (x * xm) >> 15;
      t.e = p.getArgument();
      for (int y = 0; y < height; y++) {
        p.instruction_pointer = i_ip2;
        t.c = s_H2.v[x][y];
        s_H2.v[x][y] = p.getArgument();
      }
    }
  }

  public void horizontalWarpQuick(SimpleArray1D s_aV) {
    int[] temp = new int[width];

    for (int x = 0; x < width; x++) {
      temp[x] = getPointAAX((x << 8) + ((s_aV.v[height - 1] * width) >> 8), 0);
    }

    for (int y = 0; y < height - 1; y++) {
      for (int x = 0; x < width; x++) {
        v[x][y] = getPointAAX((x << 8) + ((s_aV.v[y] * width) >> 8), y + 1);
      }
    }

    for (int x = 0; x < width; x++) {
      v[x][height - 1] = temp[x];
    }
  }

  public void verticalWarpQuick(SimpleArray1D s_aH) {
    int[] temp = new int[height];

    for (int y = 0; y < height; y++) {
      temp[y] = getPointAAY(0, (y << 8) + ((s_aH.v[width - 1] * height) >> 8));
    }

    for (int x = 0; x < width - 1; x++) {
      for (int y = 0; y < height; y++) {
        v[x][y] = getPointAAY(x + 1, (y << 8) + ((s_aH.v[x] * height) >> 8));
      }
    }

    for (int y = 0; y < height; y++) {
      v[width - 1][y] = temp[y];
    }
  }

  public void horizontalWarp(Texture t) {
    Program p = t.program;
    int displacement;

    int ym = 0x80000000 / height;

    int i_ip1 = p.instruction_pointer;
    p.getArgument();
    //int i_ip2 = p.instruction_pointer;

    p.instruction_pointer = i_ip1;

    int[] temp = new int[width];

    t.y = 0;
    displacement = p.getArgument();

    for (int x = 0; x < width; x++) {
      temp[x] = getPointAAX((x << 8) + ((displacement * width) >> 8), 0);
    }

    for (int y = 0; y < height - 1; y++) {
      t.y = (y * ym) >> 15;
      p.instruction_pointer = i_ip1;
      displacement = p.getArgument();
      for (int x = 0; x < width; x++) {
        v[x][y] = getPointAAX((x << 8) + ((displacement * width) >> 8), y + 1);
      }
    }

    for (int x = 0; x < width; x++) {
      v[x][height - 1] = temp[x];
    }
  }

  public void verticalWarp(Texture t) {
    Program p = t.program;
    int displacement;

    int xm = 0x80000000 / width;

    int i_ip1 = p.instruction_pointer;
    p.getArgument();
    //int i_ip2 = p.instruction_pointer;

    p.instruction_pointer = i_ip1;

    int[] temp = new int[height];

    t.x = 0;
    displacement = p.getArgument();

    for (int y = 0; y < height; y++) {
      temp[y] = getPointAAY(0, (y << 8) + ((displacement * height) >> 8));
    }

    for (int x = 0; x < width - 1; x++) {
      for (int y = 0; y < height; y++) {
        t.x = (x * xm) >> 15;
        p.instruction_pointer = i_ip1;
        displacement = p.getArgument();
        v[x][y] = getPointAAY(x + 1, (y << 8) + ((displacement * height) >> 8));
      }
    }

    for (int y = 0; y < height; y++) {
      v[width - 1][y] = temp[y];
    }
  }

  public void squareMesh(int spacing, int w) {
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        v[x][y] = (((x % spacing) < w) || ((y % spacing) < w)) ? 0xffff : 0x0000;
      }
    }
  }

  public void compensationForCA() {
    offset_x = offset_x - (0x10000 / width);
    if (offset_x < 0) {
      offset_x += 0x10000;
    }

    offset_y = offset_y - (0x10000 / height);
    if (offset_y < 0) {
      offset_y += 0x10000;
    }
  }
}

/*
// considers "offset" values..
   void makeImageUsingColourMap(TTImage ttimage, ColourMap colour_map, ColourMap colour_map2) {
      int c = 0;
      int _v, _w;
      int inverting = TextureGarden.colour_invert ? 0xFFFFFF : 0;
      int ox = (offset_x * width) >>> 16;
      int oy = (offset_y * height) >>> 16;
   
   	// arraycopy?
      int[] int_array = ttimage.getSource();
   
      if ((ox | oy) == 0) { // zero offset?  Great - do things as quick as possible...
         for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
               _v = ((v[x][y] >>> 24)) & 0xFF;
               _w = ((v[x][y] >>> 8)) & 0xFF;
               int_array[c++] = (colour_map.colour[_v] | colour_map2.colour[_w]) ^ inverting;
            }
         }
      }
      else // deal with offset if it exists...
      {
         for (int y = oy; y < oy + height; y++) {
            for (int x = ox; x < ox + width; x++) {
               _v = ((v[(x < width) ? x : x - width][(y < height) ? y : y - height] >>> 24)) & 0xFF;
               _w = ((v[(x < width) ? x : x - width][(y < height) ? y : y - height] >>> 8)) & 0xFF;
               int_array[c++] = (colour_map.colour[_v] | colour_map2.colour[_w]) ^ inverting;
            }
         }
      }
   
      // ttimage.update();
      // return ttimage; // return new TTImage(int_array, width, height);
   }
*/

/*
   void makeImageUsingColourMapMargolus(TTImage ttimage, ColourMap colour_map) {
      int c = 0;
      int _v; // , _w;
      int _w1, _w2, _w3, _w4;
      int inverting = TextureGarden.colour_invert ? 0xFFFFFF : 0;
      int ox = (offset_x * width) >>> 16;
      int oy = (offset_y * height) >>> 16;
   
   	// arraycopy?
      int[] int_array = ttimage.getSource();
   
      if ((ox | oy) == 0) { // zero offset?  Great - do things as quick as possible...
         for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
               _v = v[x][y];
               _w1 = _v & 0xFF;
               _w2 = ((_v >> 8) & 0xFF);
               _w3 = ((_v >> 16) & 0xFF);
               _w4 = (_v >>> 24);
               int_array[c++] = (
                                   ((colour_map.colour[_w1] >> 2) & 0x3F3F3F) + 
                                   ((colour_map.colour[_w2] >> 2) & 0x3F3F3F) + 
                                   ((colour_map.colour[_w3] >> 2) & 0x3F3F3F) + 
                                   ((colour_map.colour[_w4] >> 2) & 0x3F3F3F)) ^ inverting;
            }
         }
      }
      else // deal with offset if it exists...
      {
         for (int y = oy; y < oy + height; y++) {
            for (int x = ox; x < ox + width; x++) {
               _v = v[(x < width) ? x : x - width][(y < height) ? y : y - height];
               _w1 = _v & 0xFF;
               _w2 = ((_v >> 8) & 0xFF);
               _w3 = ((_v >> 16) & 0xFF);
               _w4 = (_v >>> 24);
               int_array[c++] = (
                                   ((colour_map.colour[_w1] >> 2) & 0x3F3F3F) + 
                                   ((colour_map.colour[_w2] >> 2) & 0x3F3F3F) + 
                                   ((colour_map.colour[_w3] >> 2) & 0x3F3F3F) + 
                                   ((colour_map.colour[_w4] >> 2) & 0x3F3F3F)) ^ inverting;
               //_w = (3 + (_v & 0xFF) + ((_v >> 8) & 0xFF) + ((_v >> 16) & 0xFF) + (_v >>> 24)) >> 2;
               //int_array[c++] = (colour_map.colour[_w]) ^ inverting;
            }
         }
      }
   }
*/

/*
   void makeImageUsingColourMap(TTImage ttimage, ColourMap colour_map, ColourMap colour_map2, int min1, int max1, int min2, int max2) {
      int c = 0;
      int _v, _w;
      int tmp;
      int inverting = TextureGarden.colour_invert ? 0xFFFFFF : 0;
      int[] int_array = ttimage.getSource();
      int ox = (offset_x * width) >>> 16;
      int oy = (offset_y * height) >>> 16;
   
      if (!TextureGarden.normalise) {
         makeImageUsingColourMap(ttimage, colour_map,  colour_map2);
      }
      else
      {
         int range1 = max1 - min1 + 0x100;
         int range2 = max2 - min2 + 0x100;
      
         if (range1 <= 0) {
            range1 = 1;
         }
      
         if (range2 <= 0) {
            range2 = 1;
         }
      
         int mul1 = 0x7FFFFFFF / (range1 >> 1);
         int mul2 = 0x7FFFFFFF / (range2 >> 1);
      
         //if (int_array == null) {
            //int_array = new int[width * height]; // new array not needed - it needs to be zero'd!
         //}
      
         if ((ox | oy) == 0) { // zero offset?  Great - do things as quick as possible...
            for (int y = 0; y < height; y++) {
               for (int x = 0; x < width; x++) {
                  tmp = v[x][y];
                  _v = (((tmp >>> 16) - min2) * mul2) >>> 24; //) & 0xFF;
                  _w = (((tmp & 0xFFFF) - min1) * mul1) >>> 24; //) & 0xFF;
                  int_array[c++] = (colour_map.colour[_v] | colour_map2.colour[_w]) ^ inverting;
               }
            }
         }
         else // deal with offset if it exists...
         {
            for (int y = oy; y < oy + height; y++) {
               for (int x = ox; x < ox + width; x++) {
               // _v = ((v[(x < width) ? x : x - width][(y < height) ? y : y - height] >>> 24)) & 0xFF;
               // _w = ((v[(x < width) ? x : x - width][(y < height) ? y : y - height] >>> 8)) & 0xFF;
                  tmp = v[(x < width) ? x : x - width][(y < height) ? y : y - height];
                  _v = (((tmp >>> 16) - min2) * mul2) >>> 24; //) & 0xFF;
                  _w = (((tmp & 0xFFFF) - min1) * mul1) >>> 24; //) & 0xFF;
                  int_array[c++] = (colour_map.colour[_v] | colour_map2.colour[_w]) ^ inverting;
               }
            }
         }
      
         // ttimage.update();
      }
   }
*/

/*
   void lowFrequencyQuickNoise() {
      int start_x = (rnd.nextInt() & 15);
      int start_y = start_x; // (rnd.nextInt() & 7);
      int size_x = (rnd.nextInt() & 15) + 16;
      int size_y = size_x; // (rnd.nextInt() & 31) + 2;
      quickNoise(start_x, start_y, size_x, size_y);
   }
*/

/*
   void lowFrequencyNoiseHalf() {
      int radius = (rnd.nextInt() & 15) + 2;
   
      int intensity = 0xFF;
   
      noiseHalf(radius, intensity);
   }
	*/

/*
   void lowFrequencyBandwidthLimitedNoiseHalf() {
      int radius = (rnd.nextInt() & 15) + 3;
   
      int intensity = 0xFF;
   
      noiseBandwidthLimitedHalf(radius - 3, radius, intensity);
   }
*/

/*
   void mediumFrequencyQuickNoise() {
      int start_x = (rnd.nextInt() & 31);
      int start_y = (rnd.nextInt() & 127);
      int size_x = (rnd.nextInt() & 15) + 2;
      int size_y = (rnd.nextInt() & 15) + 2;
      quickNoise(start_x, start_y, size_x, size_y);
   }


   void highFrequencyQuickNoise() {
      int start_x = (rnd.nextInt() & 127);
      int start_y = (rnd.nextInt() & 127);
      int size_x = (rnd.nextInt() & 15) + 2;
      int size_y = (rnd.nextInt() & 15) + 2;
      quickNoise(start_x, start_y, size_x, size_y);
   }
*/

// *** RANGE OF NOISE FUNCTIONS is MADNESS!!!
// Actual functions...
/*
   void quickNoise(int intensity, int start_x, int start_y, int size_x, int size_y, Texture t) {
      if (start_x > width) {
         start_x = width - 1;
      }
   
      if (start_y > height) {
         start_y = height - 1;
      }
   
      if ((start_x + size_x) >= width) {
         size_x = width - start_x - 1;
      }
   
      if ((start_y + size_y) >= height) {
         size_y = height - start_y - 1;
      }
   
      // Log.log("start_x" + start_x);
      // Log.log("v.length" + v.length);
      for (int x = start_x; x < start_x + size_x; x++) {
         for (int y = start_y; y < start_y + size_y; y++) {
            v[x][y] = (intensity * Utils.simpleSine(rnd.nextInt(0x10000) + t.time)); // !!! >> 16;
         }
      }
   }


   void noiseHalf(int intensity, int radius, Texture t) {
      if (radius > width) {
         radius = width;
      }
   
      if (radius > height) {
         radius = height;
      }
   
      int r_squared = radius * radius;
      for (int x = radius; --x >= 0; ) {
         for (int y = radius; --y >= 0; ) {
            if ((x * x + y * y) < r_squared) { // crude and slow...
               int th = rnd.nextInt(0x10000) + t.time;
               v[x            ][y] = Utils.Sine(  intensity, th);
               v[width - 1 - x][y] = Utils.Cosine(intensity, th);
            }
         }
      }
   }
	*/

/*
   void noiseBandwidthLimitedHalf(int intensity, int internal_radius, int external_radius, Texture t) {
      if (external_radius > width) {
         external_radius = width;
      }
   
      if (external_radius > height) {
         external_radius = height;
      }
   
      int ir_squared = internal_radius * internal_radius;
      int er_squared = external_radius * external_radius;
      int a_r;
      for (int x = external_radius; --x >= 0; ) {
         for (int y = external_radius; --y >= 0; ) {
            a_r = (x * x + y * y);
            if (a_r <= er_squared) { // crude and slow...
               if (a_r >= ir_squared) { // crude and slow...
                  int th = rnd.nextInt(0x10000) + t.time;
                  v[x            ][y] = Utils.Sine(  intensity, th);
                  v[width - 1 - x][y] = Utils.Cosine(intensity, th);
                  //v[x][y] = intensity * (rnd.nextInt() >> 16);
                  //v[width - 1 - x][y] = intensity * (rnd.nextInt() >> 16);
               }
            }
         }
      }
   }
*/

/*
   void simpleFractalNoiseHalf(int intensity, int minimum_radius, Texture t) {
      int maxd = (width >> 1);
   
      if (maxd > (height >> 1)) {
         maxd = (height >> 1);
      }
   
      int a_r = minimum_radius * minimum_radius;
      int newi = 0x1000 * intensity;
      int val;
      int temp_r;
      for (int x = maxd; --x >= 0; ) {
         for (int y = maxd; --y >= 0; ) {
            temp_r = x * x + y * y;
            if (temp_r > a_r) {
               val = (newi / (temp_r + 1)); //  * 256;
            
               int th = rnd.nextInt(0x10000) + t.time;
            
               v[x][y] = Utils.Sine(val >> 8, th); //  * (Utils.simpleSine(rnd.nextInt(0x10000) + t.time) >> 8);
               v[width - 1 - x][y] = Utils.Cosine(val >> 8, th); //  * (U(Utils.simpleSine(rnd.nextInt(0x10000) + t.time) >> 8);
               //v[x][y] = val * (rnd.nextInt() >> 24);
               //v[width - 1 - x][y] = val * (rnd.nextInt() >> 24);
            }
         }
      }
   }
*/
