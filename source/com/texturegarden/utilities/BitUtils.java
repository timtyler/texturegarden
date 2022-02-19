package com.texturegarden.utilities;


/**
 *  BitUtils - Tim Tyler 2000.
 */

/*
 * ToDo:
 *
 * Add more utilities...
 *
 */
public class BitUtils {

  /** Determines whether only one bit is set;
   *  @param x the int in question
   *  @returns true if x is a power of two
   *  @author Tim Tyler tim@tt1.org
   */
  static boolean isAPowerOfTwo(int x) {
    return ((x & -x) == x);
  }

  /** Count the number of set bits in a byte;
   *  @param x the byte to have its bits counted
   *  @returns the number of bits set in x
   *  @author Tim Tyler tim@tt1.org
   */
  static int bitCount(byte x) {
    int temp;
    int y = (int) x;

    temp = 0x55;
    y = (y & temp) + (y >>> 1 & temp);
    temp = 0x33;
    y = (y & temp) + (y >>> 2 & temp);

    return (y & 0x07) + (y >>> 4);
  }

  /** Count the number of set bits in a short;
   *  @param x the short to have its bits counted
   *  @returns the number of bits set in x
   *  @author Tim Tyler tim@tt1.org
   */
 public static int bitCount(short x) {
    int temp;
    int y = (int) x;

    temp = 0x5555;
    y = (y & temp) + (y >>> 1 & temp);
    temp = 0x3333;
    y = (y & temp) + (y >>> 2 & temp);
    temp = 0x0707;
    y = (y & temp) + (y >>> 4 & temp);

    return (y & 0x000f) + (y >>> 8);
  }

  /** Count the number of set bits in an int;
   *  @param x the int to have its bits counted
   *  @author Tim Tyler tim@tt1.org
   *  @returns the number of bits set in x
   */
  static int bitCount(int x) {
    int temp;

    temp = 0x55555555;
    x = (x & temp) + (x >>> 1 & temp);
    temp = 0x33333333;
    x = (x & temp) + (x >>> 2 & temp);
    temp = 0x07070707;
    x = (x & temp) + (x >>> 4 & temp);
    temp = 0x000F000F;
    x = (x & temp) + (x >>> 8 & temp);

    return (x & 0x1F) + (x >>> 16);
  }

  /** Count the number of set bits in an long;
   *  @param x the long to have its bits counted
   *  @author Tim Tyler tim@tt1.org
   *  @returns the number of bits set in x
   */
  static int bitCount(long x) {
    long temp;

    temp = 0x5555555555555555L;
    x = (x & temp) + (x >>> 1 & temp);
    temp = 0x3333333333333333L;
    x = (x & temp) + (x >>> 2 & temp);
    temp = 0x0707070707070707L;
    x = (x & temp) + (x >>> 4 & temp);
    temp = 0x000F000F000F000FL;
    x = (x & temp) + (x >>> 8 & temp);
    temp = 0x0000001F0000001FL;
    x = (x & temp) + (x >>> 16 & temp);

    return (int) ((x & 0x3f) + (x >>> 32));
  }

  /** Count the number of set bits in an int;
   *  @param x the int to have its bits counted
   *  @author Tim Tyler tim@tt1.org
   *  @returns the number of bits set in x
   */

  static int slowBitCount(int x) {

    int temp1 = 0x77777777;
    int temp2 = 0x33333333;
    int temp3 = 0x11111111;

    return (((x - ((x >>> 1) & temp1) - ((x >>> 2) & temp2) - ((x >>> 3) & temp3)) + ((x - ((x >>> 1) & temp1) - ((x >>> 2) & temp2) - ((x >>> 3) & temp3)) >>> 4)) & 0x0F0F0F0F) % 255;

  }

  /** 
   * Counts number of 1 bits in a 32 bit unsigned number. 
   * 
   * @param x unsigned 32 bit number whose bits you wish to count. 
   * 
   * @return number of 1 bits in x. 
   * @author Roedy Green roedy@mindprod.com 
   */
  public static int countBits(int x) {
    // collapsing partial parallel sums method 
    // collapse 32x1 bit counts to 16x2 bit counts, mask 01010101 
    x = (x >>> 1 & 0x55555555) + (x & 0x55555555);
    // collapse 16x2 bit counts to 8x4 bit counts, mask 00110011 
    x = (x >>> 2 & 0x33333333) + (x & 0x33333333);
    // collapse 8x4 bit counts to 4x8 bit counts, mask 00001111 
    x = (x >>> 4 & 0x0f0f0f0f) + (x & 0x0f0f0f0f);
    // collapse 4x8 bit counts to 2x16 bit counts 
    x = (x >>> 8 & 0x00ff00ff) + (x & 0x00ff00ff);
    // collapse 2x16 bit counts to 1x32 bit count 
    return (x >>> 16) + (x & 0x0000ffff);
  }

  /** 
  * Counts number of 1 bits in a 32 bit unsigned number. 
  * 
  * @param x unsigned 32 bit number whose bits you wish to count. 
  * 
  * @return number of 1 bits in x. 
  * @author Roedy Green roedy@mindprod.com 
  */
  public static int countBits2(int x) {
    // classic shift method 
    int result = 0;
    for (int i = 0; i < 32; i++) {
      result += x & 1;
      x >>>= 1;
    }
    return result;
  }

  public static int countBitsNovel(int x) {
    int mask;
    int sum;

    if (x == 0) { /* a common case */
      return (0);
    } else if (x == 0xffffffff) { /* ditto, but the early return is essential: */
      return (32); /* it leaves mod 31 (not 33) final states */
    }

    mask = 0x42108421;
    sum = x & mask; /* 5x: accumulate through a 1-in-5 sieve */
    sum += (x >>>= 1) & mask;
    sum += (x >>>= 1) & mask;
    sum += (x >>>= 1) & mask;
    sum += (x >>>= 1) & mask;
    sum %= (mask = 31); /* casting out mod 31 (save that constant) */

    if (sum < 0) { /* hack for Java's signed integers */
      sum += 35;
    }

    return ((sum != 0) ? sum : mask); /* return bits (zero indicated 31 bits on) */
  }

  /** 
   * Counts number of 1 bits in a 32 bit unsigned number. 
   * This method counts the number of bits set within the given 
   * integer. Given an n-bit value with k of those bits set, the 
   * efficiency of this algorithm is O(k) rather than the O(n) of 
   * an algorithm that simply looped through all bits counting non 
   * zero ones. 
   * 
   * @param x unsigned 32 bit number whose bits you wish to count. 
   * 
   * @return number of 1 bits in x. 
   * @author Dale King KingD@TCE.com KingD@TCE.com 
   */
  public static int countBits3(int x) {
    int count = 0;
    while (x != 0) {
      // The result of this operation is to subtract off 
      // the least significant non-zero bit. This can be seen 
      // from noting that subtracting 1 from any number causes 
      // all bits up to and including the least significant 
      // non-zero bit to be complemented. 
      // 
      // For example: 
      // 10101100 x 
      // 10101011 x-1 
      // 10101000 (x - 1) & x 
      x &= x - 1;
      count++;
    }
    return count;
  }

  // main method (timings)
  public static void main(String args[]) {
    long start_time;
    int N = 40000000;

    Log.log("START...");

    do {
      start_time = System.currentTimeMillis();
      for (int x = 0; x < N; x++) {
        //temp = countBits3(x);
      }

      Log.log("countBits3:" + (System.currentTimeMillis() - start_time));

      start_time = System.currentTimeMillis();
      for (int x = 0; x < N; x++) {
        //temp = countBitsClassic(x);
      }

      Log.log("countBitsClassic:" + (System.currentTimeMillis() - start_time));

    } while (1 == 1);
  }
}
