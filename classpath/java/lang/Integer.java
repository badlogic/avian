/* Copyright (c) 2008, Avian Contributors

   Permission to use, copy, modify, and/or distribute this software
   for any purpose with or without fee is hereby granted, provided
   that the above copyright notice and this permission notice appear
   in all copies.

   There is NO WARRANTY for this software.  See license.txt for
   details. */

package java.lang;

public final class Integer extends Number implements Comparable<Integer> {
  public static final Class TYPE = Class.forCanonicalName("I");

  public static final int MIN_VALUE = 0x80000000;
  public static final int MAX_VALUE = 0x7FFFFFFF;

  private final int value;

  public Integer(int value) {
    this.value = value;
  }

  public Integer(String s) {
    this.value = parseInt(s);
  }

  public static Integer valueOf(int value) {
    return new Integer(value);
  }

  public static Integer valueOf(String value) {
    return valueOf(parseInt(value));
  }

  public boolean equals(Object o) {
    return o instanceof Integer && ((Integer) o).value == value;
  }

  public int hashCode() {
    return value;
  }

  public int compareTo(Integer other) {
    return value - other.value;
  }

  public String toString() {
    return toString(value);
  }

  public static String toString(int v, int radix) {
    return Long.toString(v, radix);
  }

  public static String toString(int v) {
    return toString(v, 10);
  }

  public static String toHexString(int v) {
    return Long.toString(((long) v) & 0xFFFFFFFFL, 16);
  }

  public static String toBinaryString(int v) {
    return Long.toString(((long) v) & 0xFFFFFFFFL, 2);
  }

  public byte byteValue() {
    return (byte) value;
  }

  public short shortValue() {
    return (short) value;
  }

  public int intValue() {
    return value;
  }

  public long longValue() {
    return value;
  }

  public float floatValue() {
    return (float) value;
  }

  public double doubleValue() {
    return (double) value;
  }

  public static int parseInt(String s) {
    return parseInt(s, 10);
  }

  public static int parseInt(String s, int radix) {
    return (int) Long.parseLong(s, radix);
  }
  
  /**
   * Reverses the order of the bytes of the specified integer.
   *
   * @param i
   *            the integer value for which to reverse the byte order.
   * @return the reversed value.
   * @since 1.5
   */
  public static int reverseBytes(int i) {
      // Hacker's Delight 7-1, with minor tweak from Veldmeijer
      // http://graphics.stanford.edu/~seander/bithacks.html
      i =    ((i >>>  8) & 0x00FF00FF) | ((i & 0x00FF00FF) <<  8);
      return ( i >>> 16              ) | ( i               << 16);
  }
  
  /**
   * Determines the number of leading zeros in the specified integer prior to
   * the {@link #highestOneBit(int) highest one bit}.
   *
   * @param i
   *            the integer to examine.
   * @return the number of leading zeros in {@code i}.
   * @since 1.5
   */
  public static int numberOfLeadingZeros(int i) {
      // Hacker's Delight, Figure 5-6
      if (i <= 0) {
          return (~i >> 26) & 32;
      }
      int n = 1;
      if (i >> 16 == 0) {
          n +=  16;
          i <<= 16;
      }
      if (i >> 24 == 0) {
          n +=  8;
          i <<= 8;
      }
      if (i >> 28 == 0) {
          n +=  4;
          i <<= 4;
      }
      if (i >> 30 == 0) {
          n +=  2;
          i <<= 2;
      }
      return n - (i >>> 31);
  }
  
  /**
   * Table for Seal's algorithm for Number of Trailing Zeros. Hacker's Delight
   * online, Figure 5-18 (http://www.hackersdelight.org/revisions.pdf)
   * The entries whose value is -1 are never referenced.
   */
  private static final byte[] NTZ_TABLE = {
      32,  0,  1, 12,  2,  6, -1, 13,   3, -1,  7, -1, -1, -1, -1, 14,
      10,  4, -1, -1,  8, -1, -1, 25,  -1, -1, -1, -1, -1, 21, 27, 15,
      31, 11,  5, -1, -1, -1, -1, -1,   9, -1, -1, 24, -1, -1, 20, 26,
      30, -1, -1, -1, -1, 23, -1, 19,  29, -1, 22, 18, 28, 17, 16, -1
  };
  
  /**
   * Determines the number of trailing zeros in the specified integer after
   * the {@link #lowestOneBit(int) lowest one bit}.
   *
   * @param i
   *            the integer to examine.
   * @return the number of trailing zeros in {@code i}.
   * @since 1.5
   */
  public static int numberOfTrailingZeros(int i) {
      // Seal's algorithm - Hacker's Delight 5-18
      // BEGIN android-changed - Harmony version should be one-liner in comment below
      i &= -i;
      i = (i <<  4) + i;    // x *= 17
      i = (i <<  6) + i;    // x *= 65
      i = (i << 16) - i;    // x *= 65535
      return NTZ_TABLE[i >>> 26]; // NTZ_TABLE[((i & -i) * 0x0450FBAF) >>> 26]
      // END android-changed
  }
}
