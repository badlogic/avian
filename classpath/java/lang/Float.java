/* Copyright (c) 2008, Avian Contributors

   Permission to use, copy, modify, and/or distribute this software
   for any purpose with or without fee is hereby granted, provided
   that the above copyright notice and this permission notice appear
   in all copies.

   There is NO WARRANTY for this software.  See license.txt for
   details. */

package java.lang;

public final class Float extends Number {
  public static final Class TYPE = Class.forCanonicalName("F");
  private static final int EXP_BIT_MASK = 0x7F800000;
  private static final int SIGNIF_BIT_MASK = 0x007FFFFF;
  public static final float MAX_VALUE = 3.40282346638528860e+38f;
  public static final float MIN_VALUE = 1.40129846432481707e-45f;
  public static final float NaN = 0.0f / 0.0f; // FIXME pretty sure this is wrong.
  public static final float POSITIVE_INFINITY = 1.0f / 0.0f;
  public static final float NEGATIVE_INFINITY = -1.0f / 0.0f;
  
  private final float value;  

  public Float(String value) {
    this.value = parseFloat(value);
  }

  public Float(float value) {
    this.value = value;
  }

  public static Float valueOf(float value) {
    return new Float(value);
  }

  public static Float valueOf(String s) {
    return new Float(s);
  }

  public boolean equals(Object o) {
    return o instanceof Float && ((Float) o).value == value;
  }

  public int hashCode() {
    return floatToRawIntBits(value);
  }

  public String toString() {
    return toString(value);
  }

  public static String toString(float v) {
    return Double.toString(v);
  }

  public byte byteValue() {
    return (byte) value;
  }

  public short shortValue() {
    return (short) value;
  }

  public int intValue() {
    return (int) value;
  }

  public long longValue() {
    return (long) value;
  }

  public float floatValue() {
    return value;
  }

  public double doubleValue() {
    return (double) value;
  }

  public boolean isInfinite() {
    return isInfinite(value);
  }

  public boolean isNaN() {
    return isNaN(value);
  }

  public static float parseFloat(String s) {
    int[] numRead = new int[1];
    float f = floatFromString(s, numRead);
    if (numRead[0] == 1) {
      return f;
    } else {
      throw new NumberFormatException(s);
    }
  }
  
  public static int floatToIntBits(float value) {
    int result = floatToRawIntBits(value);
    
    // Check for NaN based on values of bit fields, maximum
    // exponent and nonzero significand.
    if (((result & EXP_BIT_MASK) == EXP_BIT_MASK) && (result & SIGNIF_BIT_MASK) != 0) {
      result = 0x7fc00000;
    }
    return result;
  }
  
  public static int compare(float float1, float float2) {
      // Non-zero, non-NaN checking.
      if (float1 > float2) {
          return 1;
      }
      if (float2 > float1) {
          return -1;
      }
      if (float1 == float2 && 0.0f != float1) {
          return 0;
      }

      // NaNs are equal to other NaNs and larger than any other float
      if (isNaN(float1)) {
          if (isNaN(float2)) {
              return 0;
          }
          return 1;
      } else if (isNaN(float2)) {
          return -1;
      }

      // Deal with +0.0 and -0.0
      int f1 = floatToRawIntBits(float1);
      int f2 = floatToRawIntBits(float2);
      // The below expression is equivalent to:
      // (f1 == f2) ? 0 : (f1 < f2) ? -1 : 1
      // because f1 and f2 are either 0 or Integer.MIN_VALUE
      return (f1 >> 31) - (f2 >> 31);
  }

  public static native int floatToRawIntBits(float value);

  public static native float intBitsToFloat(int bits);

  public static native boolean isInfinite(float value);

  public static native boolean isNaN(float value);

  public static native float floatFromString(String s, int[] numRead);
}
