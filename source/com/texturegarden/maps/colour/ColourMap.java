package com.texturegarden.maps.colour;

/*
 * ToDo
 * ====
 * Palettes...
 * Fractal noise...
 * From text files...
 * CA diffusion blur...
 * Light sources...
 * Reaction diffusion...
 * "Wibble wobble" jelly network...
 */

import java.awt.Color;

import com.texturegarden.Texture;
import com.texturegarden.arrays.ComplexArray1D;
import com.texturegarden.fourier.FFT1D;
import com.texturegarden.utilities.*;
public class ColourMap {
  static JUR rnd = new JUR();

  public int[] colour;
  int size;
  int size_log2;

  public ColourMap() {
    // make new image...
    size_log2 = 8;
    size = 1 << size_log2;
    // rnd.setSeed(1);
    colour = new int[size];
  }

  // RGB
  public void randomiseRGB() {
    int start_r = rnd.nextInt(0x100);
    int start_g = rnd.nextInt(0x100);
    int start_b = rnd.nextInt(0x100);

    int end_r = rnd.nextInt(0x80) | (start_r & 0x80);
    int end_g = rnd.nextInt(0x80) | (start_g & 0x80);
    int end_b = rnd.nextInt(0x80) | (start_b & 0x80);

    colourScaleRGB(start_r, start_g, start_b, end_r, end_g, end_b);
  }

  void colourScaleRGB(int start_r, int start_g, int start_b, int end_r, int end_g, int end_b) {
    int _r, _g, _b;
    int range_r = end_r - start_r;
    int range_g = end_g - start_g;
    int range_b = end_b - start_b;
    colour = new int[size];

    for (int x = 0; x < size; x++) {
      _r = (start_r + ((x * range_r) >> size_log2)) & 0xff;
      _g = (start_g + ((x * range_g) >> size_log2)) & 0xff;
      _b = (start_b + ((x * range_b) >> size_log2)) & 0xff;

      colour[x] = 0xFF000000 | (_r << 16) | (_g << 8) | _b;
    }
  }

  void colourScaleRGBSawtooth(int start_r, int start_g, int start_b, int end_r, int end_g, int end_b, int sawtooth) {
    int _r, _g, _b;
    int range_r = end_r - start_r;
    int range_g = end_g - start_g;
    int range_b = end_b - start_b;
    colour = new int[size];

    for (int x = 0; x < (size >> sawtooth); x++) {
      _r = (start_r + ((x * range_r) >> (size_log2 - sawtooth))) & 0xff;
      _g = (start_g + ((x * range_g) >> (size_log2 - sawtooth))) & 0xff;
      _b = (start_b + ((x * range_b) >> (size_log2 - sawtooth))) & 0xff;

      // for (int i = 0; i < sawtooth; i++) {
      for (int j = 0; j < size; j += (size >> (sawtooth - 1))) {
        if (sawtooth > 0) {
          colour[x + j] = 0xFF000000 | (_r << 16) | (_g << 8) | _b;

          colour[(size >> (sawtooth - 1)) - 1 - x + j] = 0xFF000000 | (_r << 16) | (_g << 8) | _b;
        }
      }
      // }
    }
  }

  public void randomise() {
    // fadeToBlack(); // 0,0,0, 0xff,0xff,0xFF);

    //rnd.setSeed(1);
    if (rnd.nextBoolean()) {
      randomiseHSB();
    } else {
      randomiseHSBFourier();
    }
  }

  void fadeToOrFromBlack(int end_r, int end_g, int end_b, boolean from_to, int freq) {
    if (from_to) {
      //fadeFromBlack(end_r, end_g, end_b, freq);
    } else {
      //fadeToBlack(end_r, end_g, end_b, freq);
    }
  }

  void fadeToBlack(Texture t, int end_r, int end_g, int end_b, int freq) {
    // Texture t = TextureGarden.texture_array.getSelectedTextureContainer().texture;

    if (end_r != 0) {
      end_r = t.red_value;
    }

    if (end_g != 0) {
      end_g = t.green_value;
    }

    if (end_b != 0) {
      end_b = t.blue_value;
    }

    int r = 0; // inv ? Texture.red_value  : 0;
    int g = 0; // inv ? Texture.green_value : 0;
    int b = 0; // inv ? Texture.blue_value  : 0;

    //if (inv) {
    //end_r ^= Texture.red_value;
    //end_g ^= Texture.green_value;
    //end_b ^= Texture.blue_value;
    //}

    if (freq > 0) {
      colourScaleRGBSawtooth(r, g, b, end_r, end_g, end_b, freq);
    } else {
      colourScaleRGB(r, g, b, end_r, end_g, end_b);
    }
  }

  void fadeFromBlack(Texture t, int end_r, int end_g, int end_b, int freq) {
    // Texture t = TextureGarden.texture_array.getSelectedTextureContainer().texture;

    if (end_r != 0) {
      end_r = t.red_value;
    }

    if (end_g != 0) {
      end_g = t.green_value;
    }

    if (end_b != 0) {
      end_b = t.blue_value;
    }

    int r = 0; // inv ? Texture.red_value  : 0;
    int g = 0; // inv ? Texture.green_value : 0;
    int b = 0; // inv ? Texture.blue_value  : 0;

    //if (inv) {
    //end_r ^= Texture.red_value;
    //end_g ^= Texture.green_value;
    //end_b ^= Texture.blue_value;
    //}

    if (freq > 0) {
      colourScaleRGBSawtooth(end_r, end_g, end_b, r, g, b, freq);
    } else {
      colourScaleRGB(end_r, end_g, end_b, r, g, b);
    }
  }

  public void fadeFromTo(int start_r, int start_g, int start_b, int end_r, int end_g, int end_b, boolean from_to, int freq) {
    if (from_to) {
      //fadeFromTo(end_r, end_g, end_b, start_r, start_g, start_b, freq);
    } else {
      //fadeFromTo(start_r, start_g, start_b, end_r, end_g, end_b, freq);
    }
  }

  void fadeFromTo(Texture t, int start_r, int start_g, int start_b, int end_r, int end_g, int end_b, int freq) {
    // Texture t = TextureGarden.texture_array.getSelectedTextureContainer().texture;

    if (end_r != 0) {
      end_r = t.red_value;
    }

    if (end_g != 0) {
      end_g = t.green_value;
    }

    if (end_b != 0) {
      end_b = t.blue_value;
    }

    if (freq > 0) {
      colourScaleRGBSawtooth(start_r, start_g, start_b, end_r, end_g, end_b, freq);
    } else {
      colourScaleRGB(start_r, start_g, start_b, end_r, end_g, end_b);
    }
  }

  void randomiseHSB() {
    float start_h = rnd.nextInt(256) / 256.0f;
    float start_s = rnd.nextInt(256) / 256.0f;
    float start_b = rnd.nextInt(256) / 256.0f;

    float end_h = rnd.nextInt(256) / 256.0f;
    float end_s = rnd.nextInt(256) / 256.0f;
    float end_b = rnd.nextInt(256) / 256.0f;

    colourScaleHSB(start_h, start_s, start_b, end_h, end_s, end_b);
  }

  void colourScaleHSB(float start_h, float start_s, float start_b, float end_h, float end_s, float end_b) {
    if (rnd.nextBoolean()) {
      start_b = 1.0f;
      end_b = 0.0f;
      start_s = 1.0f;
    } else {
      start_b = 0.0f;
      end_b = 1.0f;
      if (rnd.nextBoolean()) {
        end_s = 1.0f;
      } else {
        end_s = 0.0f;
      }
    }

    float range_h = end_h - start_h;
    float range_s = end_s - start_s;
    float range_b = end_b - start_b;

    colour = new int[size];

    float _h, _s, _b;

    for (int x = 0; x < size; x++) {
      _h = start_h + (x * range_h) / 256.0f;
      _s = start_s + (x * range_s) / 256.0f;
      _b = start_b + (x * range_b) / 256.0f;

      colour[x] = Color.HSBtoRGB(_h, _s, _b);
    }
  }

  void randomiseHSBFourier() {
    ComplexArray1D c_aH = new ComplexArray1D(size);
    ComplexArray1D c_aS = new ComplexArray1D(size);
    ComplexArray1D c_aB = new ComplexArray1D(size);

    for (int x = 0; x < 3; x++) {
      c_aH.r.v[x] = (rnd.nextInt() << 8) >> 8;
      c_aH.i.v[x] = (rnd.nextInt() << 8) >> 8;
      c_aS.r.v[x] = (rnd.nextInt() << 8) >> 8;
      c_aS.i.v[x] = (rnd.nextInt() << 8) >> 8;
      c_aB.r.v[x] = (rnd.nextInt() << 8) >> 8;
      c_aB.i.v[x] = (rnd.nextInt() << 8) >> 8;
    }

    c_aH = FFT1D.fft_inv(c_aH);
    c_aS = FFT1D.fft_inv(c_aS);
    c_aB = FFT1D.fft_inv(c_aB);

    c_aH.r.equaliseScaled(0x10000);
    c_aS.r.equaliseScaled(0x10000);
    c_aB.r.equaliseScaled(0x10000);

    colour = new int[size];

    float _h, _s, _b;

    for (int x = 0; x < size; x++) {
      _h = c_aH.r.v[x] / 65536.0f;
      _s = c_aS.r.v[x] / 65536.0f;
      _b = c_aB.r.v[x] / 65536.0f;

      colour[x] = Color.HSBtoRGB(_h, _s, _b);
    }
  }
}
