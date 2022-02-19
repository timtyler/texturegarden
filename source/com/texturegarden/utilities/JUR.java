package com.texturegarden.utilities;
public class JUR {
  static final long serialVersionUID = 3905348978240129619L;

  private long seed;

  private final static long multiplier = 0x5DEECE66DL;
  private final static long addend = 0xBL;
  private final static long mask = (1L << 48) - 1;

  private int stored;
  private int stored_bits_left = 0;

  public JUR() {
    this(System.currentTimeMillis());
  }

  public JUR(long seed) {
    setSeed(seed);
  }

  public void setSeed(long seed) {
    this.seed = (seed ^ multiplier) & mask;
    haveNextNextGaussian = false;
  }

  protected int next(int bits) {
    long nextseed = (seed * multiplier + addend) & mask;
    seed = nextseed;
    return (int) (nextseed >>> (48 - bits));
  }

  private static final int BITS_PER_BYTE = 8;
  private static final int BYTES_PER_INT = 4;

  public void nextBytes(byte[] bytes) {
    int numRequested = bytes.length;

    int numGot = 0, rnd = 0;

    while (true) {
      for (int i = 0; i < BYTES_PER_INT; i++) {
        if (numGot == numRequested)
          return;

        rnd = (i == 0 ? next(BITS_PER_BYTE * BYTES_PER_INT) : rnd >> BITS_PER_BYTE);
        bytes[numGot++] = (byte) rnd;
      }
    }
  }

  public int nextInt() {
    return next(32);
  }

  public int nextInt(int n) {
    if (n <= 0)
      throw new IllegalArgumentException("n must be positive");

    if ((n & -n) == n)
      return (int) ((n * (long) next(31)) >> 31);

    int bits, val;
    do {
      bits = next(31);
      val = bits % n;
    } while (bits - val + (n - 1) < 0);
    return val;
  }

  public long nextLong() {

    return ((long) (next(32)) << 32) + next(32);
  }

  public boolean nextBoolean() {
    return next(1) != 0;
  }

  public float nextFloat() {
    int i = next(24);
    return i / ((float) (1 << 24));
  }

  public double nextDouble() {
    long l = ((long) (next(26)) << 27) + next(27);
    return l / (double) (1L << 53);
  }

  public char nextChar() {

    return (char) (next(16));
  }

  public short nextShort() {
    return (short) (next(16));
  }

  public byte nextByte() {
    return (byte) (next(8));
  }

  private double nextNextGaussian;
  private boolean haveNextNextGaussian = false;

  public double nextGaussian() {
    if (haveNextNextGaussian) {
      haveNextNextGaussian = false;
      return nextNextGaussian;
    } else {
      double v1, v2, s;
      do {
        v1 = 2 * nextDouble() - 1; // between -1 and 1
        v2 = 2 * nextDouble() - 1; // between -1 and 1
        s = v1 * v1 + v2 * v2;
      } while (s >= 1);
      double multiplier = Math.sqrt(-2 * Math.log(s) / s);
      nextNextGaussian = v2 * multiplier;
      haveNextNextGaussian = true;
      return v1 * multiplier;
    }
  }

  public boolean nextBooleanEfficiently() {
    boolean b;

    if (stored_bits_left-- == 0) {
      stored = next(32);
      stored_bits_left = 31;
    }

    b = (stored & 1) == 0;
    stored >>>= 1;

    return b;
  }

  public String returnName() {
    return "JUR";
  }
}
