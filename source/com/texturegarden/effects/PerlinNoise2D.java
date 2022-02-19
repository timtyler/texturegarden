package com.texturegarden.effects;

import com.texturegarden.utilities.*;
import com.texturegarden.utilities.*;

/**
 * PerlinNoise2D - Tim Tyler 2001.
 * this class sucks...
 * for reference, see: http://freespace.virgin.net/hugo.elias/models/m_perlin.htm
 */

// interpolation...
class PerlinNoise2D {
  float persistence;
  float number_of_octaves;
  float n;
  int amplitude = 65536;
  JUR rnd = new JUR();

  int cosineInterpolate(int a, int b, int x) {
    float ft = (float) (Math.PI * x) / 256.0f;
    float f = (float) (1.0f - Math.cos(ft)) * 0.5f;

    return (int) (a - (a * f) + (b * f));
  }

  /*
     int cosineInterpolate(int a, int b, float x) {
        // float ft = Math.PI * x;
        float f = (1.0f - Math.cos(Math.PI * x)) * 0.5f;
     
        return  (int)(a - (a * f) + (b * f));
     }
  */

  // PRNG... (no seed?)
  int basicPRNG(int x, int y) {
    rnd.setSeed(x + y * 197);
    return rnd.nextInt(65536);

    //n = x + y * 57;
    //n = (n << 13) ^ n;
    //return (1.0f - ( (n * (n * n * 15731 + 789221) + 1376312589) & 0x7fffffff) / 1073741824.0);
    //return 1.0f - (n * (n * n * 15731 + 789221) + 1376312589) / 1073741824.0;
  }

  int smoothedPerlinNoise(int x, int y) {
    int corners = (basicPRNG(x - 1, y - 1) + basicPRNG(x + 1, y - 1) + basicPRNG(x - 1, y + 1) + basicPRNG(x + 1, y + 1)) / 16; // 1/4
    int sides = (basicPRNG(x - 1, y) + basicPRNG(x + 1, y) + basicPRNG(x, y - 1) + basicPRNG(x, y + 1)) / 8; // 1/2
    int center = basicPRNG(x, y) / 4; // 1/2

    return corners + sides + center;
  }

  int interpolatedNoise(int x, int y) {
    int integerX = (int) (x);
    int fractionalX = x - integerX;

    int integerY = (int) (y);
    int fractionalY = y - integerY;

    int v1 = smoothedPerlinNoise(integerX, integerY);
    int v2 = smoothedPerlinNoise(integerX + 1, integerY);
    int v3 = smoothedPerlinNoise(integerX, integerY + 1);
    int v4 = smoothedPerlinNoise(integerX + 1, integerY + 1);

    int i1 = cosineInterpolate(v1, v2, fractionalX);
    int i2 = cosineInterpolate(v3, v4, fractionalX);

    return cosineInterpolate(i1, i2, fractionalY);
  }

  void perlinNoise2DSetup(int p, int o) {
    persistence = p / 100.0f;
    number_of_octaves = o / 100.0f;
    n = number_of_octaves - 1;
  }

  int perlinNoise2D(int x, int y) {
    int total = 0;
    int frequency;
    int amplitude;

    for (int i = 0; i < n; i++) {
      frequency = 1 << i;
      amplitude = (int) Math.pow(persistence, i);

      total = total + interpolatedNoise(x * frequency, y * frequency) * amplitude;
    }

    return (int) total;
  }
}
