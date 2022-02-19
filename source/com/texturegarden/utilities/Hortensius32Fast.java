package com.texturegarden.utilities;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.texturegarden.utilities.*;

/**
 * Hortensius32FastFast:
 * <P>
 * <b>Hortensius32Fast</b> is a drop-in subclass replacement
 * for java.util.Random.  It is *NOT* properly synchronised and
 * cannot be used in a multithreaded environment without care.
 *
 * <p><b>Hortensius32FastFast</b> is not a subclass of java.util.Random.  It has
 * the same public methods as Random does, however, and it is
 * algorithmically identical to Hortensius32Fast.  Hortensius32FastFast
 * has hard-code inlined all of its methods directly, and made all of them
 * final (well, the ones of consequence anyway).  Further, these
 * methods are <i>not</i> synchronized, so the same Hortensius32FastFast
 * instance cannot be shared by multiple threads.  But all this helps
 * Hortensius32FastFast achieve over twice the speed of Hortensius32Fast.
 *
 * next(int bits) >>>'s by (32-bits) to get a value ranging
 * between 0 and 2^bits-1 long inclusive; hope that's correct.
 * setSeed(seed) set initial values to the working area
 * of 624 words. For setSeed(seed), seed is any 32-bit integer
 * <b>except for 0</b>.
 *
 */

public class Hortensius32Fast extends JUR {
  private static final int MASK = (int) Long.parseLong("11010010100101010101001010101010", 2); // wrong

  int integer_seed;
  private static final long GOOD_SEED = 4357;

  public Hortensius32Fast() {
    super(GOOD_SEED);
    setSeed(GOOD_SEED);
  }

  public Hortensius32Fast(final long seed) {
    if (seed == 0) {
      setSeed(GOOD_SEED);
    } else {
      setSeed(seed);
    }
  }

  public Hortensius32Fast(final int seed) {
    if (seed == 0) {
      setSeed(GOOD_SEED);
    } else {
      setSeed((long) seed);
    }
  }

  public void setSeed(final long seed) {
    super.setSeed(seed);

    integer_seed = (int) seed;
  }

  protected int next(final int bits) {
    integer_seed = (integer_seed >>> 1) ^ (integer_seed & MASK) ^ (integer_seed << 1);

    return integer_seed >>> (32 - bits); // hope that's right!
  }

  private void writeObject(ObjectOutputStream out) throws IOException {
    out.defaultWriteObject();
  }

  private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
    in.defaultReadObject();
  }

  public boolean nextBoolean() {
    return next(1) != 0;
  }

  public boolean nextBoolean(final float probability) {
    if (probability < 0 || probability > 1)
      throw new IllegalArgumentException("probability must be between 0.0 and 1.0 inclusive.");
    return nextFloat() < probability;
  }

  public int nextInt(final int n) {
    if (n <= 0)
      throw new IllegalArgumentException("n must be positive");

    if ((n & -n) == n) // i.e., n is a power of 2
      return (int) ((n * (long) next(31)) >> 31);

    int bits, val;
    do {
      bits = next(31);
      val = bits % n;
    } while (bits - val + (n - 1) < 0);
    return val;
  }

  public double nextDouble() {
    return (((long) next(26) << 27) + next(27)) / (double) (1L << 53);
  }

  public float nextFloat() {
    return next(24) / ((float) (1 << 24));
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

  public String returnName() {
    return "Hortensius";
  }
}
