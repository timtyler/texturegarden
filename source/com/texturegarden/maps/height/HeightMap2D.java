package com.texturegarden.maps.height;

/* ToDo
 * Improve HeightReader function...
 * _/~\_ (bell_quick n)
 * _/~\_ (sin_quick n)
 * /\    (sawtooth_quick n)
 * /~~\  (circle)
 * /~~\  (parabola)
 * _/~   (cubic) ...?
 * _/\_  (spike_at x) ... ?
 * / (incline) (null function...)
 // * \ (decline)
 // * ~\_ (z_curve)
 // * \__/ (valley_parabolic)
 // * \__/ (valley_round)
 // * /\ (ridge_sawtooth n)
 // * -^-v- (sin n)
 // * ~/_\~ (ridge_bell n)
 */

import com.texturegarden.Texture;
import com.texturegarden.arrays.SimpleArray2D;
import com.texturegarden.image.ImageWrapper;
import com.texturegarden.program.Program;
import com.texturegarden.utilities.JUR;

public class HeightMap2D {
  static JUR rnd = new JUR();
  static Program program;
  static Texture texture;
  static HeightReader hr;

  static int ip;

  static {
    hr = new HeightReader() {
      int readHeight(int h) {
        texture.c = h;
        program.instruction_pointer = ip;
        program.executeAllInstructionsWithLock();
        return texture.elevation;
      }
    };
  }

  public static void heightMapRender(ImageWrapper tti, SimpleArray2D s_a, int range, Texture t, boolean hflip, boolean vflip) {
    t.program.claimTexture(t);
    texture = t;
    program = t.program;
    ip = program.ip_of_GET_ELEVATION;

    // LightSourceList ...
    HeightMapOperation hmo = new HeightMapOperation() {
      ImageWrapper tti;
      SimpleArray2D s_a;
      int light_x = 0x7800;
      int light_y = 0x8800;
      int light_z = 0x6000;
      int normalising_scale_factor;

      void init(int r1, int r2) {
        if (r1 <= 0) {
          r1 = 1;
        }

        normalising_scale_factor = 0x40000000 / r1;
      }

      int process(int colour, int c, int s, int e) {
        int temp_c = c & 0xFFFF;

        int normal_x = ((temp_c - (e & 0xFFFF)) * normalising_scale_factor) >> 15;
        int normal_y = ((temp_c - (s & 0xFFFF)) * normalising_scale_factor) >> 15;
        int normal_z = 0x17FF; // too big?

        int intensity = ((normal_x * light_x) + (normal_y * light_y) + (normal_z * light_z)) >> 12;
        // Log.log ("inten = " + intensity);

        if (intensity > 0xFFFF) {
          intensity = 0xFFFF;
        }

        if (intensity < 0) {
          intensity = 0;
        }

        int red = (colour >> 16) & 0xFF;
        int green = (colour >> 8) & 0xFF;
        int blue = (colour) & 0xFF;

        red = (red * intensity) & 0xFF0000;
        green = ((green * intensity) >> 8) & 0xFF00;
        blue = ((blue * intensity) >> 16) & 0xFF;

        return 0xFF000000 | red | green | blue;
      }

    };

    generalHeightMapOperation(tti, s_a, hmo, hr, range, 99, hflip, vflip);
    program.freeTexture();
  }

  // two levels...
  static void heightMapRenderRD(ImageWrapper tti, SimpleArray2D s_a, int range1, int range2, Texture t, boolean hflip, boolean vflip) {
    t.program.claimTexture(t);
    texture = t;
    program = t.program;

    ip = program.ip_of_GET_ELEVATION;

    HeightMapOperation hmo = new HeightMapOperation() {
      ImageWrapper tti;
      SimpleArray2D s_a;
      int light_x = 0x7800;
      int light_y = 0x8800;
      int light_z = 0x6000;
      int normalising_scale_factor1;
      int normalising_scale_factor2;

      void init(int r1, int r2) {
        if (r1 <= 0) {
          r1 = 1;
        }

        normalising_scale_factor1 = 0x40000000 / r1;

        if (r2 <= 0) {
          r2 = 1;
        }

        normalising_scale_factor2 = 0x40000000 / r2;
      }

      int process(int colour, int c, int s, int e) {
        int temp_c = c & 0xFFFF;
        int temp_c2 = c >>> 16;

        int normal_x = (temp_c - (e & 0xFFFF)) * normalising_scale_factor1;
        int normal_y = (temp_c - (s & 0xFFFF)) * normalising_scale_factor1;
        int normal_z = 0x17FF; // too big?

        normal_x = (normal_x - ((temp_c2 - (e >>> 16)) * normalising_scale_factor2)) >> 15;
        normal_y = (normal_y - ((temp_c2 - (s >>> 16)) * normalising_scale_factor2)) >> 15;
        // normal_z += 0x0FFF; // too big?

        /*
           int normal_magnitude_squared = (normal_x * normal_x) + (normal_y * normal_y) + (normal_z * normal_z);
           int normal_magnitude = SquareRoot.sqrt(normal_magnitude_squared);
           if (normal_magnitude <= 0) {
              normal_magnitude = 1;
           }
        
           int mf = 0x1000 / (normal_magnitude);
           normal_x = (normal_x * mf) >> 14;
           normal_y = (normal_y * mf) >> 14;
           normal_z = (normal_z * mf) >> 14;
        */

        int intensity = ((normal_x * light_x) + (normal_y * light_y) + (normal_z * light_z)) >> 12;
        // Log.log ("inten = " + intensity);

        if (intensity > 0xFFFF) {
          intensity = 0xFFFF;
        }

        if (intensity < 0) {
          intensity = 0;
        }

        int red = (colour >> 16) & 0xFF;
        int green = (colour >> 8) & 0xFF;
        int blue = (colour) & 0xFF;

        red = (red * intensity) & 0xFF0000;
        green = ((green * intensity) >> 8) & 0xFF00;
        blue = ((blue * intensity) >> 16) & 0xFF;

        return 0xFF000000 | red | green | blue;
      }

      /*
         int readHeight(int h) {
            if (ip >= 0) {
               texture.c = h;
               program.instruction_pointer = ip;
               program.executeAllInstructionsWithLock();
               return texture.elevation;
            }
            else
            {
               return h;
            }
         }
        */
    };

    generalHeightMapOperation(tti, s_a, hmo, hr, range1, range2, hflip, vflip);

    program.freeTexture();
  }
  public static void heightMapRenderXX(ImageWrapper tti, SimpleArray2D s_a, int range, Texture t, boolean hflip, boolean vflip) {
    t.program.claimTexture(t);
    texture = t;
    program = t.program;
    ip = program.ip_of_GET_ELEVATION;

    // LightSourceList ...
    HeightMapOperation hmo = new HeightMapOperation() {
      ImageWrapper tti;
      SimpleArray2D s_a;
      int light_x = 0x4000;
      int light_y = 0x8000;
      int light_z = 0x6000;
      int normalising_scale_factor;

      void init(int r1, int r2) {
        if (r1 <= 0) {
          r1 = 1;
        }

        normalising_scale_factor = 0x7FFFFFFF / r1;
      }

      int process(int colour, int c, int s, int e) {
        normalising_scale_factor = 0x4000;
        //        int normal_x = smartMultiply((((e - c) << 16) >> 16), normalising_scale_factor) >>> 14;
        //        int normal_y = smartMultiply((((s - c) << 16) >> 16), normalising_scale_factor) >>> 14;
        //
        //         int intensity = (smartMultiply(normal_x, light_x) + smartMultiply(normal_y, light_y) + (1 << 27)) >> 11;
        int normal_x = ((((e - c) << 16) >> 16) * normalising_scale_factor) >>> 14;
        int normal_y = ((((s - c) << 16) >> 16) * normalising_scale_factor) >>> 14;

        int intensity = ((normal_x * light_x) + (normal_y * light_y) + (1 << 27)) >> 11;

        if (intensity < 0) {
          intensity = -intensity;
        }

        if (intensity > 0xFFFF) {
          intensity = 0xFFFF;
        }

        int red = (colour >> 16) & 0xFF;
        int green = (colour >> 8) & 0xFF;
        int blue = colour & 0xFF;

        red = (red * intensity) & 0xFF0000;
        green = ((green * intensity) >> 8) & 0xFF00;
        blue = ((blue * intensity) >> 16) & 0xFF;

        return 0xFF000000 | red | green | blue;
      }
    };

    generalHeightMapOperation(tti, s_a, hmo, hr, range, 99, hflip, vflip);
    program.freeTexture();
  }

  //  static int smartMultiply(int a, int b) {
  //    if (a >= 0) {
  //      if (b >= 0) {
  //        return a * b;
  //      } else {
  //        return - (a * (-b));
  //      }
  //    } else {
  //      if (b >= 0) {
  //        return - ((-a) * b);
  //      } else {
  //        return (-a) * (-b);
  //      }
  //    }
  //  }

  // two levels...
  public static void heightMapRenderRDXX(ImageWrapper tti, SimpleArray2D s_a, int range1, int range2, Texture t, boolean hflip, boolean vflip) {
    t.program.claimTexture(t);
    texture = t;
    program = t.program;

    ip = program.ip_of_GET_ELEVATION;

    HeightMapOperation hmo = new HeightMapOperation() {
      ImageWrapper tti;
      SimpleArray2D s_a;
      int light_x = 0x7800;
      int light_y = 0x8800;
      int light_z = 0x6000;
      int normalising_scale_factor1;
      int normalising_scale_factor2;

      void init(int r1, int r2) {
        if (r1 <= 0) {
          r1 = 1;
        }

        normalising_scale_factor1 = 0x40000000 / r1;

        if (r2 <= 0) {
          r2 = 1;
        }

        normalising_scale_factor2 = 0x40000000 / r2;
      }

      int process(int colour, int c, int s, int e) {
        int temp_c = c & 0xFFFF;
        int temp_c2 = c >>> 16;

        int normal_x = (temp_c - (e & 0xFFFF)) * normalising_scale_factor1;
        int normal_y = (temp_c - (s & 0xFFFF)) * normalising_scale_factor1;
        int normal_z = 0x17FF; // too big?

        normal_x = (normal_x - ((temp_c2 - (e >>> 16)) * normalising_scale_factor2)) >> 15;
        normal_y = (normal_y - ((temp_c2 - (s >>> 16)) * normalising_scale_factor2)) >> 15;
        // normal_z += 0x0FFF; // too big?

        /*
           int normal_magnitude_squared = (normal_x * normal_x) + (normal_y * normal_y) + (normal_z * normal_z);
           int normal_magnitude = SquareRoot.sqrt(normal_magnitude_squared);
           if (normal_magnitude <= 0) {
              normal_magnitude = 1;
           }
        
           int mf = 0x1000 / (normal_magnitude);
           normal_x = (normal_x * mf) >> 14;
           normal_y = (normal_y * mf) >> 14;
           normal_z = (normal_z * mf) >> 14;
        */

        int intensity = ((normal_x * light_x) + (normal_y * light_y) + (normal_z * light_z)) >> 12;
        // Log.log ("inten = " + intensity);

        if (intensity > 0xFFFF) {
          intensity = 0xFFFF;
        }

        if (intensity < 0) {
          intensity = 0;
        }

        int red = (colour >> 16) & 0xFF;
        int green = (colour >> 8) & 0xFF;
        int blue = (colour) & 0xFF;

        red = (red * intensity) & 0xFF0000;
        green = ((green * intensity) >> 8) & 0xFF00;
        blue = ((blue * intensity) >> 16) & 0xFF;

        return 0xFF000000 | red | green | blue;
      }

      /*
         int readHeight(int h) {
            if (ip >= 0) {
               texture.c = h;
               program.instruction_pointer = ip;
               program.executeAllInstructionsWithLock();
               return texture.elevation;
            }
            else
            {
               return h;
            }
         }
      	*/
    };

    generalHeightMapOperation(tti, s_a, hmo, hr, range1, range2, hflip, vflip);

    program.freeTexture();
  }

  static void generalHeightMapOperation(ImageWrapper tti, SimpleArray2D s_a, HeightMapOperation hmo, HeightReader hr, int range1, int range2, boolean hflip, boolean vflip) {
    int c, s, e;
    int col;

    int[] pixels = tti.getArray();
    int width = s_a.width;
    int height = s_a.height;

    int x_max = width - 1;
    int y_max = height - 1;

    int offset_x = (s_a.offset_x * width) >>> 16;
    int offset_y = (s_a.offset_y * height) >>> 16;

    int[] height_map_cache = new int[width];
    // int[] preserved_for_later = new int[width];

    int temp_x;
    int temp_y;

    hmo.init(range1, range2);

    int x_start = false ? width - 1 : 0;
    int x_end = false ? 0 : width - 1;
    int x_dir = false ? -1 : 1;

    int y_start = false ? height - 1 : 0;
    int y_end = false ? 0 : height - 1;
    int y_dir = false ? -1 : 1;

    int x_startpo = x_start + x_dir;
    //int x_endmo   = x_end   - x_dir;

    int y_startpo = y_start + y_dir;
    //int y_endmo   = y_end   - y_dir;

    int adr;
    int y_base;

    // deal with the y = 0 case...
    //texture.x = 0;
    //texture.y = 0;

    //for (int x = 0; x < x_max; x++) {
    //preserved_for_later[x] = hr.readHeight(s_a.v[x][0]);
    //}

    temp_x = hflip ? offset_x : -offset_x;
    if (temp_x < 0) {
      temp_x += width;
    }

    temp_y = vflip ? offset_y : -offset_y;
    if (temp_y < 0) {
      temp_y += height;
    }

    adr = temp_x + width * temp_y;
    col = pixels[adr];
    c = hr.readHeight(s_a.v[0][0]);
    e = hr.readHeight(s_a.v[1][0]);
    s = hr.readHeight(s_a.v[0][1]);
    height_map_cache[0] = s;

    pixels[adr] = hmo.process(col, c, s, e);

    for (int x = 1; x < x_max; x++) {
      temp_x = hflip ? offset_x - x : x - offset_x;
      if (temp_x < 0) {
        temp_x += width;
      }

      adr = temp_x + width * temp_y;
      col = pixels[adr];
      c = e;
      e = hr.readHeight(s_a.v[x + 1][0]);
      s = hr.readHeight(s_a.v[x][1]);
      height_map_cache[x] = s;

      pixels[adr] = hmo.process(col, c, s, e); // top line...
    }

    height_map_cache[x_max] = hr.readHeight(s_a.v[x_max][1]);

    for (int y = y_startpo; y != y_end; y += y_dir) {
      temp_x = hflip ? offset_x : -offset_x;
      if (temp_x < 0) {
        temp_x += width;
      }

      temp_y = vflip ? offset_y - y : y - offset_y;

      if (temp_y < 0) {
        temp_y += height;
      }

      adr = temp_x + width * temp_y;
      col = pixels[adr];
      c = height_map_cache[x_start]; // hr.readHeight(s_a.v[0][y    ]);
      e = height_map_cache[x_startpo]; // hr.readHeight(s_a.v[1][y    ]);
      s = hr.readHeight(s_a.v[x_start][y + y_dir]);
      height_map_cache[x_start] = s;

      pixels[adr] = hmo.process(col, c, s, e);

      y_base = width * temp_y;

      for (int x = x_startpo; x != x_end; x += x_dir) {
        temp_x = hflip ? offset_x - x : x - offset_x;

        if (temp_x < 0) {
          temp_x += width;
        }

        adr = temp_x + y_base;
        col = pixels[adr];
        c = e;
        e = height_map_cache[x + x_dir];
        s = hr.readHeight(s_a.v[x][y + y_dir]);
        height_map_cache[x] = s;

        pixels[adr] = hmo.process(col, c, s, e);
      }

      height_map_cache[x_max] = hr.readHeight(s_a.v[x_max][y + y_dir]);
    }

    // 2 x ends...
    temp_x = hflip ? offset_x - x_end : x_end - offset_x;
    if (temp_x < 0) {
      temp_x += width;
    }

    for (int y = 0; y < y_max; y++) {
      temp_y = vflip ? offset_y - y : y - offset_y;
      if (temp_y < 0) {
        temp_y += height;
      }

      adr = temp_x + width * temp_y;

      col = pixels[adr];
      c = hr.readHeight(s_a.v[x_max][y]);
      e = hr.readHeight(s_a.v[0][y]);
      s = hr.readHeight(s_a.v[x_max][y + 1]);

      pixels[adr] = hmo.process(col, c, s, e);
    }

    temp_y = vflip ? offset_y - y_end : y_end - offset_y;
    if (temp_y < 0) {
      temp_y += height;
    }

    y_base = width * temp_y;

    for (int x = 0; x < x_max; x++) {
      temp_x = hflip ? offset_x - x : x - offset_x;
      // temp_x = hflip ? offset_x : -offset_x;
      if (temp_x < 0) {
        temp_x += width;
      }

      adr = temp_x + y_base; // is OK...

      col = pixels[adr];
      c = hr.readHeight(s_a.v[x][y_max]);
      e = hr.readHeight(s_a.v[x + 1][y_max]);
      s = hr.readHeight(s_a.v[x][0]); // preserved_for_later[x];

      pixels[adr] = hmo.process(col, c, s, e);
    }

    temp_x = hflip ? offset_x - x_end : x_end - offset_x;
    if (temp_x < 0) {
      temp_x += width;
    }

    temp_y = vflip ? offset_y - y_end : y_end - offset_y;
    if (temp_y < 0) {
      temp_y += height;
    }

    adr = temp_x + width * temp_y;

    col = pixels[adr];
    c = hr.readHeight(s_a.v[x_max][y_max]);
    s = hr.readHeight(s_a.v[x_max][0]);
    e = hr.readHeight(s_a.v[0][y_max]);

    pixels[adr] = hmo.process(col, c, s, e);
  }
}
