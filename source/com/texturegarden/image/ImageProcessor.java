/**
 *TTImageProcessor Tim Tyler 2001
 */

/*
 * ToDo
 * ====
 * Add more image processing functions...
 * Generate less garbage by allowing the sender to specify the output array...
 */

package com.texturegarden.image;
import java.awt.Color;
import java.awt.Toolkit;

import com.texturegarden.utilities.Hortensius32Fast;
import com.texturegarden.utilities.Log;

public class ImageProcessor   {
  static Hortensius32Fast rnd = new Hortensius32Fast();

  // rotation types...
  final static int NORMAL = 0;
  final static int ROTATE_CW = 1;
  final static int ROTATE_180 = 2;
  final static int ROTATE_ACW = 3;
  final static int HFLIP = 4;
  final static int VFLIP = 5;
  final static int HFLIP_CW = 6;
  final static int HFLIP_ACW = 7;

  static Toolkit toolkit;

  static {
    toolkit = Toolkit.getDefaultToolkit();
  }

  /**
   * Very simple rotation wrapper
   */
  static ImageWrapper rotate(ImageWrapper _i) {
    return affine(_i, ROTATE_CW);
  }

  /**
   * Simple flip and mirror affine transforms, which return a new image.
   */
  static ImageWrapper affine(ImageWrapper _i, int type) {
    int w = _i.getWidth(null);
    int h = _i.getHeight(null);

    /*if (w < 1) {
       debug("Image not yet loaded!");
       return null;
    }*/

    // PixelArrays.ensureArray4Size(w * h);
    // int[] pixels4 = PixelArrays.pixels4;

    int[] pixels_out = new int[w * h];

    // debug("w * h:" + (w * h));

    int[] pixels4 = imageToArray(_i);

    // debug("pixels4:" + (pixels4.length));

    switch (type) {
      case NORMAL : // don't use this...
        for (int i = 0; i < w; i++) {
          for (int j = 0; j < h; j++) {
            pixels_out[i + w * j] = pixels4[i + w * j];
          }
        }

        Log.log("ImageProcessor - please don't use this routine!");
        return new ImageWrapper(pixels_out, w, h);

      case ROTATE_CW :
        for (int i = 0; i < w; i++) {
          for (int j = 0; j < h; j++) {
            pixels_out[h - j - 1 + h * i] = pixels4[i + w * j];
          }
        }
        return new ImageWrapper(pixels_out, h, w);

      case ROTATE_ACW :
        for (int i = 0; i < w; i++) {
          for (int j = 0; j < h; j++) {
            pixels_out[j + h * (w - i - 1)] = pixels4[i + w * j];
          }
        }
        return new ImageWrapper(pixels_out, h, w);

      case ROTATE_180 : // don't use this...
        for (int i = 0; i < w; i++) {
          for (int j = 0; j < h; j++) {
            pixels_out[(w - i - 1) + w * (h - j - 1)] = pixels4[i + w * j];
          }
        }
        return new ImageWrapper(pixels_out, w, h);

      case HFLIP : // don't use this...
        for (int i = 0; i < w; i++) {
          for (int j = 0; j < h; j++) {
            pixels_out[(w - i - 1) + w * j] = pixels4[i + w * j];
          }
        }

        break;

      case VFLIP : // don't use this...
        for (int i = 0; i < w; i++) {
          for (int j = 0; j < h; j++) {
            pixels_out[i + w * (h - j - 1)] = pixels4[i + w * j];
          }
        }

        return new ImageWrapper(pixels_out, w, h);

      case HFLIP_CW :
        for (int i = 0; i < w; i++) {
          for (int j = 0; j < h; j++) {
            pixels_out[h - j - 1 + h * (w - i - 1)] = pixels4[i + w * j];
          }
        }

        return new ImageWrapper(pixels_out, h, w);

      case HFLIP_ACW :
        for (int i = 0; i < w; i++) {
          for (int j = 0; j < h; j++) {
            pixels_out[j + h * i] = pixels4[i + w * j];
          }
        }

        return new ImageWrapper(pixels_out, h, w);
    }

    // return new TTImage(pixels_out, w, h);
    return null;
  }

  /**
   * RGBA filter 0xAARRGGBB
   * Given an image apply a simple intensity filter to its components.
   * Returns a new image.
   */
  static ImageWrapper simpleIntensityFilter(ImageWrapper _i, int m) {
    int[] image_pix = _i.getArray();
    int w = _i.getWidth();
    int h = _i.getHeight();

    int m1 = m & 0xff;
    int m2 = (m >> 8) & 0xff;
    int m3 = (m >> 16) & 0xff;
    int m4 = m >> 24;

    if (m4 == 255) {
      m4 = 256;
    }

    /*if (w < 1) {
        debug("Image not yet loaded!");
        return null;
     }*/

    for (int i = 0; i < w; i++) {
      for (int j = 0; j < h; j++) {
        int pix = image_pix[i + w * j];
        int b = pix & 0xff;
        int g = (pix >> 8) & 0xff;
        int r = (pix >> 16) & 0xff;
        int a = pix >>> 24;

        b = (b * m1) >> 8;
        g = (g * m2) >> 8;
        r = (r * m3) >> 8;
        a = (a * m4) >> 8;

        image_pix[i + w * j] = b | g << 8 | r << 16 | a << 24;
      }
    }

    return _i; // new TTImage(pixels_out, w, h);
  }

  /**
   * Add/Sub RGB Noise 0xAARRGGBB
   * Given an image apply a simple intensity filter to its components.
   * Returns a new image.
   */
  static ImageWrapper addNoise(ImageWrapper _i, int m) {
    int[] image_pix = _i.getArray();
    int w = _i.getWidth();
    int h = _i.getHeight();

    int mb = (m << 24) >> 23;
    int mg = (m << 16) >> 23;
    int mr = (m << 8) >> 23;

    //int w = _i.getWidth(null);
    //i/nt h = _i.getHeight(null);

    /*if (w < 1) {
        debug("Image not yet loaded!");
        return null;
     }*/

    //int[] pixels_out = new int[w * h];

    //int[] pixels4 = imageToArray(_i);

    for (int i = 0; i < w; i++) {
      for (int j = 0; j < h; j++) {
        int pix = image_pix[i + w * j];
        int b = pix & 0xff;
        int g = (pix >> 8) & 0xff;
        int r = (pix >> 16) & 0xff;
        int a = pix & 0xff000000;

        if (mr >= 0) {
          r += rnd.nextInt(mr + 1);
          if (r > 255) {
            r = 255;
          }
        } else {
          r -= rnd.nextInt(-mr);
          if (r < 0) {
            r = 0;
          }
        }

        if (mg >= 0) {
          g += rnd.nextInt(mg + 1);
          if (g > 255) {
            g = 255;
          }
        } else {
          g -= rnd.nextInt(-mg);
          if (g < 0) {
            g = 0;
          }
        }

        if (mb >= 0) {
          b += rnd.nextInt(mb + 1);
          if (b > 255) {
            b = 255;
          }
        } else {
          b -= rnd.nextInt(-mb);
          if (b < 0) {
            b = 0;
          }
        }

        image_pix[i + w * j] = b | g << 8 | r << 16 | a;
      }
    }

    return _i; // new TTImage(pixels_out, w, h);
  }

  /**
   * RGBA filter 0xAARRGGBB
   * Given an image apply a simple intensity filter to its components.
   * Returns a new image.
   */
  static ImageWrapper invertingFilter(ImageWrapper _i, boolean ir, boolean ig, boolean ib) {
    int[] image_pix = _i.getArray();
    int w = _i.getWidth();
    int h = _i.getHeight();

    for (int i = 0; i < w; i++) {
      for (int j = 0; j < h; j++) {
        int pix = image_pix[i + w * j];
        int b = pix & 0xff;
        int g = (pix >> 8) & 0xff;
        int r = (pix >> 16) & 0xff;
        int a = pix & 0xff000000;

        if (ir) {
          r = r ^ 255;
        }

        if (ig) {
          g = g ^ 255;
        }

        if (ib) {
          b = b ^ 255;
        }

        image_pix[i + w * j] = b | g << 8 | r << 16 | a;
      }
    }

    return _i;
  }

  /**
   * HSB filter
   * Given an image apply an HSB filter to it.
   * Returns a new image.
   */
  static ImageWrapper HSBFilter(ImageWrapper _i, float _h, float _s, float _b, boolean set_h, boolean set_s, boolean set_b) {
    float[] hsb = new float[3];

    int[] image_pix = _i.getArray();
    int w = _i.getWidth(null);
    int h = _i.getHeight(null);

    int[] pixels_out = new int[w * h];

    /*if (w < 1) {
          debug("Image not yet loaded!");
          return null;
       }*/

    for (int i = 0; i < w; i++) {
      for (int j = 0; j < h; j++) {
        int pix = image_pix[i + w * j];
        int b = pix & 0xff;
        int g = (pix >> 8) & 0xff;
        int r = (pix >> 16) & 0xff;

        Color.RGBtoHSB(r, g, b, hsb);

        // debug("h:" + hsb[0] + " s:" + hsb[0] + " b:" + hsb[0] );

        if (set_h) {
          hsb[0] = _h;
        } else {
          if (_h < 0) {
            hsb[0] = 1 - hsb[0] + _h;
          } else {
            hsb[0] += _h;
          }

          if (hsb[0] >= 1) {
            hsb[0] -= 1;
          }
        }

        if (set_s) {
          hsb[1] = _s;
        } else {
          if (_s < 0) {
            hsb[1] = 1 + (hsb[1] * _s);
          } else {
            hsb[1] = hsb[1] * _s;
          }

          if (hsb[1] >= 1) {
            hsb[1] = 1;
          }
        }

        if (set_b) {
          hsb[2] = _b;
        } else {
          if (_b < 0) {
            hsb[2] = 1 + (hsb[2] * _b);
          } else {
            hsb[2] = hsb[2] * _b;
          }

          if (hsb[2] >= 1) {
            hsb[2] = 1;
          }
        }

        pixels_out[i + w * j] = (pix & 0xff000000) | (0xffffff & Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]));
      }
    }

    return new ImageWrapper(pixels_out, w, h);
    // return _i; // new TTImage(image_pix, w, h);
  }

  /**
   * Mask
   * Given an image apply a mask to it.
   * Returns a new image.
   */
  static ImageWrapper mask(ImageWrapper _i, ImageWrapper _m, float weight_h, float weight_s, float weight_b, boolean mask_a) { // float _h, float _s, float _b, boolean set_h, boolean set_s, boolean set_b) {
    float[] hsb = new float[3];
    float[] hsb2 = new float[3];

    int w = _i.getWidth(null);
    int h = _i.getHeight(null);

    /*if (w < 1) {
          debug("Image not yet loaded!");
          return null;
       }*/

    // int[] pixels3 = PixelArrays.ensureArray3Size(w * h);
    // int[] pixels4 = PixelArrays.ensureArray4Size(w * h);

    int[] pixels_out = new int[w * h];

    //imageToArray(_m, pixels3);
    // imageToArray(_i, pixels4);

    int[] pixels4 = imageToArray(_i);
    int[] pixels3 = imageToArray(_m);

    for (int i = 0; i < w; i++) {
      for (int j = 0; j < h; j++) {
        int pix = pixels4[i + w * j];
        int b = pix & 0xff;
        int g = (pix >> 8) & 0xff;
        int r = (pix >> 16) & 0xff;
        Color.RGBtoHSB(r, g, b, hsb);

        int pix2 = pixels3[i + w * j];
        int b2 = pix2 & 0xff;
        int g2 = (pix2 >> 8) & 0xff;
        int r2 = (pix2 >> 16) & 0xff;
        Color.RGBtoHSB(r2, g2, b2, hsb2);

        if (weight_h == 0) {
          hsb[0] = hsb[0] * hsb2[0];
        } else {
          hsb[0] = (hsb[0] * (1 - weight_h)) + (hsb2[0] * weight_h);
        }

        if (weight_s == 0) {
          hsb[1] = hsb[1] * hsb2[1];
        } else {
          hsb[1] = (hsb[1] * (1 - weight_s)) + (hsb2[1] * weight_s);
        }

        if (weight_b == 0) {
          hsb[2] = hsb[2] * hsb2[2];
        } else {
          hsb[2] = (hsb[2] * (1 - weight_b)) + (hsb2[2] * weight_b);
        }

        //.//hsb[1] = (hsb[1] * (1 - weight_s)) + (hsb2[1] * weight_s);
        //hsb[2] = (hsb[2] * (1 - weight_b)) + (hsb2[2] * weight_b);

        int a = ((mask_a) ? pix2 : pix) & 0xff000000;
        pixels_out[i + w * j] = a | (0xffffff & Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]));
      }
    }

    return new ImageWrapper(pixels_out, w, h);
  }

  /**
   * Scale - makes images smaller by powers of two only...
   * scale factors = 1024 -> normal size...
   */
  static ImageWrapper scale(ImageWrapper _i, int x_scale, int y_scale) {
    // apparently, there are too many bugs in vendors' getScaledInstance code...
    /*
       if (x_scale == 1024) {
          return _i;
       }
    
       int w = _i.getWidth(null);
       int h = _i.getHeight(null);
    
       int w2 = (w * x_scale) >> 10;
       int h2 = (h * y_scale) >> 10;
    
       if (w2 == 0) {
          w2 = 1;
       }
    
       if (h2 == 0) {
          h2 = 1;
       }
    
       return _i.getScaledInstance(w2, h2, TTImage.SCALE_SMOOTH);
    
    }
    // */

    if (x_scale == 1024) {
      return _i;
    }

    //if ((Rockz.coords.bitcount != 1) || (x_scale > 1024)) {
    if (x_scale > 1024) {
      return scaleSlow(_i, x_scale, y_scale);
    }

    int w = _i.getWidth(null);
    int h = _i.getHeight(null);

    int w2 = (w * x_scale) >> 10;
    int h2 = (h * y_scale) >> 10;

    if (w2 == 0) {
      w2 = 1;
    }

    if (h2 == 0) {
      h2 = 1;
    }

    // PixelArrays.ensureArray4Size(w * h);
    // int[] pixels4 = PixelArrays.pixels4;

    int[] pixels_out = new int[w2 * h2];

    int nx = 1024 / x_scale;
    int ny = 1024 / y_scale;

    int[] pixels4 = imageToArray(_i);

    //imageToArray(_i, pixels4);

    if (x_scale <= 1024) {
      for (int i2 = 0; i2 < w2; i2++) {
        for (int j2 = 0; j2 < h2; j2++) {
          int rootx = i2 * nx;
          int rooty = j2 * ny;

          int r_total = 0;
          int g_total = 0;
          int b_total = 0;
          int a_total = 0;
          int cnt = 0;

          for (int i3 = 0; i3 < nx; i3++) {
            for (int j3 = 0; j3 < ny; j3++) {
              if ((rootx + i3) < w) {
                if ((rooty + j3) < h) {
                  int pix = pixels4[rootx + i3 + w * (rooty + j3)];

                  int a = pix >>> 24;
                  b_total += (pix & 0xff) * a;
                  g_total += ((pix >> 8) & 0xff) * a;
                  r_total += ((pix >> 16) & 0xff) * a;
                  a_total += a;
                  cnt++;
                }
              }
            }
          }

          int divisor = a_total; //  * nx * ny;
          if (divisor != 0) {
            r_total = r_total / divisor;
            g_total = g_total / divisor;
            b_total = b_total / divisor;
          } else {
            r_total = 0;
            g_total = 0;
            b_total = 0;
          }

          a_total = a_total / cnt;

          pixels_out[i2 + w2 * j2] = b_total | g_total << 8 | r_total << 16 | a_total << 24;
        }
      }
    } else {
      nx = x_scale >> 10;
      ny = y_scale >> 10;

      for (int i2 = 0; i2 < w2; i2++) {
        for (int j2 = 0; j2 < h2; j2++) {
          int pix = pixels4[((i2 << 10) / x_scale) + w * ((j2 << 10) / y_scale)];

          pixels_out[i2 + w2 * j2] = pix;
        }
      }
    }

    return new ImageWrapper(pixels_out, w2, h2);
  }

  /**
   * Scale - makes images smaller...
   * scale factors = 1024 => normal size...
   */
  static ImageWrapper scaleSlow(ImageWrapper _i, int x_scale, int y_scale) {
    int w = _i.getWidth(null);
    int h = _i.getHeight(null);

    int w2 = (w * x_scale) >> 10;
    int h2 = (h * y_scale) >> 10;

    if (w2 == 0) {
      w2 = 1;
    }

    if (h2 == 0) {
      h2 = 1;
    }

    // PixelArrays.ensureArray4Size(w * h);
    // int[] pixels4 = PixelArrays.pixels4;

    int[] pixels_out = new int[w2 * h2]; // out

    int[] pixels4 = imageToArray(_i);

    // arrays for easy access...
    int[][] r = new int[w][h]; // in
    int[][] g = new int[w][h]; // in
    int[][] b = new int[w][h]; // in
    int[][] a = new int[w][h]; // in

    for (int j = 0; j < h; j++) {
      for (int i = 0; i < w; i++) {
        int pix = pixels4[i + w * j];
        int alpha = pix >>> 24;
        b[i][j] = (pix & 0xff) * alpha;
        g[i][j] = ((pix >> 8) & 0xff) * alpha;
        r[i][j] = ((pix >> 16) & 0xff) * alpha;
        a[i][j] = alpha;
      }
    }

    int _x, _y;
    int _x2, _y2;
    int _xinc, _yinc;
    //int maxx, maxy;

    // maxx = w << 10;
    //maxy = h << 10;

    _xinc = (1 << 20) / x_scale;
    _yinc = (1 << 20) / y_scale;

    for (int j2 = 0; j2 < h2; j2++) {
      _y = (j2 << 20) / y_scale;

      for (int i2 = 0; i2 < w2; i2++) {
        _x = (i2 << 20) / x_scale;

        int r_total = 0;
        int g_total = 0;
        int b_total = 0;
        int a_total = 0;
        int cnt = 0;

        for (int j3 = 0; j3 < 4; j3++) {
          _y2 = _y + ((j3 * _yinc) >> 2);
          for (int i3 = 0; i3 < 4; i3++) {
            _x2 = _x + ((i3 * _xinc) >> 2);

            if ((_x2 >> 10) < w) {
              if ((_y2 >> 10) < h) {
                int alpha = a[_x2 >> 10][_y2 >> 10];
                b_total += b[_x2 >> 10][_y2 >> 10] * alpha;
                g_total += g[_x2 >> 10][_y2 >> 10] * alpha;
                r_total += r[_x2 >> 10][_y2 >> 10] * alpha;
                a_total += alpha;
                cnt++;
              }
            }
          }
        }

        int divisor = a_total;
        if (divisor != 0) {
          r_total = (r_total >> 8) / divisor;
          g_total = (g_total >> 8) / divisor;
          b_total = (b_total >> 8) / divisor;
        } else {
          r_total = 0;
          g_total = 0;
          b_total = 0;
        }

        a_total = a_total / cnt;

        pixels_out[i2 + w2 * j2] = b_total | g_total << 8 | r_total << 16 | a_total << 24;
      }
    }

    return new ImageWrapper(pixels_out, w2, h2);
  }

  // Can't just draw one on top of the other...
  //  because image may have come from an imageProducer...
  // Only uses binary alpha channel at the moment :-(
  static ImageWrapper combineTwoImages(ImageWrapper i_bot, ImageWrapper i_top) {
    if (i_bot == null) {
      Log.log("texture == null");
    }
    if (i_bot == null) {
      Log.log("mask == null");
    }
    int w = i_bot.getWidth(null);
    int h = i_bot.getHeight(null);
    int[] pixels_out = new int[w * h]; // out
    return combineTwoImagesInternally(i_bot, i_top, w, h, pixels_out);
  }

  static ImageWrapper combineTwoImagesInternally(ImageWrapper i_bot, ImageWrapper i_top, int w, int h, int[] pixels_out) {
    //PixelArrays.ensureArray3Size(w * h);
    //int[] pixels3 = PixelArrays.pixels3;
    //PixelArrays.ensureArray4Size(w * h);
    //int[] pixels4 = PixelArrays.pixels4;

    int[] pixels3 = imageToArray(i_bot);
    int[] pixels4 = imageToArray(i_top);

    int pix;
    int pix2;
    for (int j = 0; j < h; j++) {
      for (int i = 0; i < w; i++) {
        pix = pixels3[i + w * j];
        pix2 = pixels4[i + w * j];
        if ((pix2 & 0xff000000) == 0xff000000) {
          pix = pix2;
        }
        pixels_out[i + w * j] = pix;
      }
    }

    return new ImageWrapper(pixels_out, w, h);
  }

  // Only uses binary alpha channel at the moment :-(
  static ImageWrapper combineTwoImagesWithFullOverwrite(ImageWrapper i_bot, ImageWrapper i_top) {
    if (i_bot == null) {
      Log.log("texture == null");
    }
    if (i_bot == null) {
      Log.log("mask == null");
    }
    int w = i_bot.getWidth(null);
    int h = i_bot.getHeight(null);
    int[] pixels_out = new int[w * h]; // out
    return combineTwoImagesWithFullOverwriteInternally(i_bot, i_top, w, h, pixels_out);
  }

  static ImageWrapper combineTwoImagesWithFullOverwriteInternally(ImageWrapper i_bot, ImageWrapper i_top, int w, int h, int[] pixels_out) {
    //PixelArrays.ensureArray3Size(w * h);
    //int[] pixels3 = PixelArrays.pixels3;
    //PixelArrays.ensureArray4Size(w * h);
    //int[] pixels4 = PixelArrays.pixels4;

    int[] pixels3 = imageToArray(i_bot);
    int[] pixels4 = imageToArray(i_top);

    int pix;
    int pix2;
    for (int j = 0; j < h; j++) {
      for (int i = 0; i < w; i++) {
        pix = pixels3[i + w * j];
        pix2 = pixels4[i + w * j];
        if ((pix2 & 0xff000000) != 0) {
          pix = pix2;
        }
        pixels_out[i + w * j] = pix;
      }
    }

    return new ImageWrapper(pixels_out, w, h);
  }

  static ImageWrapper applyMaskToImage(ImageWrapper texture, ImageWrapper mask) {
    if (texture == null) {
      Log.log("texture == null");
    }
    if (mask == null) {
      Log.log("mask == null");
    }
    int w = texture.getWidth(null);
    int h = texture.getHeight(null);
    int[] pixels_out = new int[w * h]; // out
    return applyMaskToImageInternally(texture, mask, w, h, pixels_out);
  }

  // texture , mask
  static ImageWrapper applyMaskToImageInternally(ImageWrapper texture, ImageWrapper mask, int w, int h, int[] pixels_out) {
    //if (texture == null) { debug("texture == null"); }
    //if (mask == null) { debug("mask == null"); }
    //int w = texture.getWidth(null);
    //int h = texture.getHeight(null);

    //PixelArrays.ensureArray3Size(w * h);
    //int[] pixels3 = PixelArrays.pixels3;
    ///ixelArrays.ensureArray4Size(w * h);
    //int[] pixels4 = PixelArrays.pixels4;
    // int[] pixels_out = new int[w * h]; // out

    int[] pixels3 = imageToArray(texture);
    int[] pixels4 = imageToArray(mask);

    int pix;
    int pix2;
    for (int j = 0; j < h; j++) {
      for (int i = 0; i < w; i++) {
        pix = pixels3[i + w * j];
        pix2 = (pixels4[i + w * j] >> 16) & 0xff;
        if (pix2 < 0xE0) { // red component determines transparency...
          pix = pix & 0xFFFFFF; //  | (pix2 << 24);
        }
        //else
        //{
        //pix = pix & 0xffffff;
        //}

        pixels_out[i + w * j] = pix;
      }
    }

    return new ImageWrapper(pixels_out, w, h);
  }

  // has side effects...
  // grab pixels into an array...
  static int[] imageToArray(ImageWrapper i) { // , int[] ia) {
    if (i == null) {
      // GfxManager.zeroArray(ia);
      // debug("Problems in ImageProcessor");
      return null;
      // return new int[8192]; // null; // !?!?!
    } else {
      return i.getArray();
    }
  }
  /*
     int w = i.getWidth(null);
     int h = i.getHeight(null);
  
     PixelGrabber pg = new PixelGrabber(i, 0, 0, w, h, ia, 0, w);
     try {
        pg.grabPixels();
     }
        catch (InterruptedException e) {
           debug(e.toString());
        }
  }
  }
  */
}
