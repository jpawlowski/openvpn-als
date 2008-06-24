
				/*
 *  Adito
 *
 *  Copyright (C) 2003-2006 3SP LTD. All Rights Reserved
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2 of
 *  the License, or (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public
 *  License along with this program; if not, write to the Free Software
 *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */
			
package com.maverick.crypto.math;

import java.util.Random;
import java.util.Stack;

public class BigInteger {

  private int signum; // -1 means -ve; +1 means +ve; 0 means 0;
  private int mag[]; // array of ints with [0] being the most significant
  private int nBits = -1; // cache bitCount() value
  private int nBitLength = -1; // cache bitLength() value
  private static final long IMASK = 0xffffffffL;
  private long mQuote = -1L; // -m^(-1) mod b, b = 2^32 (see Montgomery mult.)
  int firstNonzeroIntNum = -2;
  private BigInteger() {
  }

  private BigInteger(int nWords) {
    signum = 1;
    mag = new int[nWords];
  }

  private BigInteger(
      int signum,
      int[] mag) {
    this.signum = signum;
    if (mag.length > 0) {
      int i = 0;
      while (i < mag.length && mag[i] == 0) {
        i++;
      }
      if (i == 0) {
        this.mag = mag;
      }
      else {
        // strip leading 0 bytes
        int[] newMag = new int[mag.length - i];
        System.arraycopy(mag, i, newMag, 0, newMag.length);
        this.mag = newMag;
        if (newMag.length == 0) {
          this.signum = 0;
        }
      }
    }
    else {
      this.mag = mag;
      this.signum = 0;
    }
  }

  public BigInteger(
      String sval) throws NumberFormatException {
    this(sval, 10);
  }

  public BigInteger(
      String sval,
      int rdx) throws NumberFormatException {
    if (sval.length() == 0) {
      throw new NumberFormatException("Zero length BigInteger");
    }

    if (rdx < Character.MIN_RADIX || rdx > Character.MAX_RADIX) {
      throw new NumberFormatException("Radix out of range");
    }

    int index = 0;
    signum = 1;

    if (sval.charAt(0) == '-') {
      if (sval.length() == 1) {
        throw new NumberFormatException("Zero length BigInteger");
      }

      signum = -1;
      index = 1;
    }

    // strip leading zeros from the string value
    while (index < sval.length() &&
           Character.digit(sval.charAt(index), rdx) == 0) {
      index++;
    }

    if (index >= sval.length()) {
      // zero value - we're done
      signum = 0;
      mag = new int[0];
      return;
    }

    //////
    // could we work out the max number of ints required to store
    // sval.length digits in the given base, then allocate that
    // storage in one hit?, then generate the magnitude in one hit too?
    //////

    BigInteger b = BigInteger.ZERO;
    BigInteger r = valueOf(rdx);
    while (index < sval.length()) {
      // (optimise this by taking chunks of digits instead?)
      b = b.multiply(r).add(valueOf(Character.digit(sval.charAt(index), rdx)));
      index++;
    }

    mag = b.mag;
    return;
  }

  public BigInteger(
      byte[] bval) throws NumberFormatException {
    if (bval.length == 0) {
      throw new NumberFormatException("Zero length BigInteger");
    }

    signum = 1;
    if (bval[0] < 0) {
      // FIXME:
      int iBval;
      signum = -1;
      // strip leading sign bytes
      for (iBval = 0; iBval < bval.length && bval[iBval] == -1; iBval++) {
        ;
      }
      mag = new int[ (bval.length - iBval) / 2 + 1];
      // copy bytes to magnitude
      // invert bytes then add one to find magnitude of value
    }
    else {
      // strip leading zero bytes and return magnitude bytes
      mag = makeMagnitude(bval);
    }
  }

  private int[] makeMagnitude(byte[] bval) {
    int i;
    int[] mag;
    int firstSignificant;

    // strip leading zeros
    for (firstSignificant = 0;
         firstSignificant < bval.length && bval[firstSignificant] == 0;
         firstSignificant++) {
      ;
    }

    if (firstSignificant >= bval.length) {
      return new int[0];
    }

    int nInts = (bval.length - firstSignificant + 3) / 4;
    int bCount = (bval.length - firstSignificant) % 4;
    if (bCount == 0) {
      bCount = 4;

    }
    mag = new int[nInts];
    int v = 0;
    int magnitudeIndex = 0;
    for (i = firstSignificant; i < bval.length; i++) {
      v <<= 8;
      v |= bval[i] & 0xff;
      bCount--;
      if (bCount <= 0) {
        mag[magnitudeIndex] = v;
        magnitudeIndex++;
        bCount = 4;
        v = 0;
      }
    }

    if (magnitudeIndex < mag.length) {
      mag[magnitudeIndex] = v;
    }

    return mag;
  }

  public BigInteger(
      int sign,
      byte[] mag) throws NumberFormatException {
    if (sign < -1 || sign > 1) {
      throw new NumberFormatException("Invalid sign value");
    }

    if (sign == 0) {
      this.signum = 0;
      this.mag = new int[0];
      return;
    }

    // copy bytes
    this.mag = makeMagnitude(mag);
    this.signum = sign;
  }

  public BigInteger(
      int numBits,
      Random rnd) throws IllegalArgumentException {
    if (numBits < 0) {
      throw new IllegalArgumentException("numBits must be non-negative");
    }

    int nBytes = (numBits + 7) / 8;

    byte[] b = new byte[nBytes];

    if (nBytes > 0) {
      nextRndBytes(rnd, b);
      // strip off any excess bits in the MSB
      b[0] &= rndMask[8 * nBytes - numBits];
    }

    this.mag = makeMagnitude(b);
    this.signum = 1;
    this.nBits = -1;
    this.nBitLength = -1;
  }

  private static final int BITS_PER_BYTE = 8;
  private static final int BYTES_PER_INT = 4;

  /**
   * strictly speaking this is a little dodgey from a compliance
   * point of view as it forces people to be using SecureRandom as
   * well, that being said - this implementation is for a crypto
   * library and you do have the source!
   */
  private void nextRndBytes(
      Random rnd,
      byte[] bytes) {
    int numRequested = bytes.length;
    int numGot = 0, r = 0;

    if (rnd instanceof com.maverick.crypto.security.SecureRandom) {
      ( (com.maverick.crypto.security.SecureRandom) rnd).nextBytes(bytes);
    }
    else {
      for (; ; ) {
        for (int i = 0; i < BYTES_PER_INT; i++) {
          if (numGot == numRequested) {
            return;
          }

          r = (i == 0 ? rnd.nextInt() : r >> BITS_PER_BYTE);
          bytes[numGot++] = (byte) r;
        }
      }
    }
  }

  private static final byte[] rndMask = {
      (byte) 255, 127, 63, 31, 15, 7, 3, 1};

  public BigInteger(
      int bitLength,
      int certainty,
      Random rnd) throws ArithmeticException {
    int nBytes = (bitLength + 7) / 8;

    byte[] b = new byte[nBytes];

    do {
      if (nBytes > 0) {
        nextRndBytes(rnd, b);
        // strip off any excess bits in the MSB
        b[0] &= rndMask[8 * nBytes - bitLength];
      }

      this.mag = makeMagnitude(b);
      this.signum = 1;
      this.nBits = -1;
      this.nBitLength = -1;
      if (certainty > 0 && bitLength > 2) {
        this.mag[this.mag.length - 1] |= 1;
      }
    }
    while (this.bitLength() != bitLength
           || !this.isProbablePrime(certainty));
  }

  public BigInteger abs() {
    return (signum >= 0) ? this : this.negate();
  }

  /**
   * return a = a + b - b preserved.
   */
  private int[] add(
      int[] a,
      int[] b) {
    int tI = a.length - 1;
    int vI = b.length - 1;
    long m = 0;

    while (vI >= 0) {
      m += ( ( (long) a[tI]) & IMASK) + ( ( (long) b[vI--]) & IMASK);
      a[tI--] = (int) m;
      m >>>= 32;
    }

    while (tI >= 0 && m != 0) {
      m += ( ( (long) a[tI]) & IMASK);
      a[tI--] = (int) m;
      m >>>= 32;
    }

    return a;
  }

  public BigInteger add(
      BigInteger val) throws ArithmeticException {
    if (val.signum == 0 || val.mag.length == 0) {
      return this;
    }
    if (this.signum == 0 || this.mag.length == 0) {
      return val;
    }

    if (val.signum < 0) {
      if (this.signum > 0) {
        return this.subtract(val.negate());
      }
    }
    else {
      if (this.signum < 0) {
        return val.subtract(this.negate());
      }
    }

    // both BigIntegers are either +ve or -ve; set the sign later

    int[] mag, op;

    if (this.mag.length < val.mag.length) {
      mag = new int[val.mag.length + 1];

      System.arraycopy(val.mag, 0, mag, 1, val.mag.length);
      op = this.mag;
    }
    else {
      mag = new int[this.mag.length + 1];

      System.arraycopy(this.mag, 0, mag, 1, this.mag.length);
      op = val.mag;
    }

    return new BigInteger(this.signum, add(mag, op));
  }

  public int bitCount() {
    if (nBits == -1) {
      nBits = 0;
      for (int i = 0; i < mag.length; i++) {
        nBits += bitCounts[mag[i] & 0xff];
        nBits += bitCounts[ (mag[i] >> 8) & 0xff];
        nBits += bitCounts[ (mag[i] >> 16) & 0xff];
        nBits += bitCounts[ (mag[i] >> 24) & 0xff];
      }
    }

    return nBits;
  }

  private final static byte bitCounts[] = {
      0, 1, 1, 2, 1, 2, 2, 3, 1, 2, 2, 3, 2, 3, 3, 4,
      1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3, 4, 3, 4, 4, 5,
      1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3, 4, 3, 4, 4, 5,
      2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6,
      1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3, 4, 3, 4, 4, 5,
      2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6,
      2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6,
      3, 4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5, 6, 6, 7,
      1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3, 4, 3, 4, 4, 5,
      2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6,
      2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6,
      3, 4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5, 6, 6, 7,
      2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6,
      3, 4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5, 6, 6, 7,
      3, 4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5, 6, 6, 7,
      4, 5, 5, 6, 5, 6, 6, 7, 5, 6, 6, 7, 6, 7, 7, 8};

  private int bitLength(
      int indx,
      int[] mag) {
    int bitLength;

    if (mag.length == 0) {
      return 0;
    }
    else {
      while (indx != mag.length && mag[indx] == 0) {
        indx++;
      }

      if (indx == mag.length) {
        return 0;
      }

      // bit length for everything after the first int
      bitLength = 32 * ( (mag.length - indx) - 1);

      // and determine bitlength of first int
      bitLength += bitLen(mag[indx]);

      if (signum < 0) {
        // Check if magnitude is a power of two
        boolean pow2 =
            ( (bitCounts[mag[indx] & 0xff]) +
             (bitCounts[ (mag[indx] >> 8) & 0xff]) +
             (bitCounts[ (mag[indx] >> 16) & 0xff]) +
             (bitCounts[ (mag[indx] >> 24) & 0xff])) == 1;

        for (int i = indx + 1; i < mag.length && pow2; i++) {
          pow2 = (mag[i] == 0);
        }

        bitLength -= (pow2 ? 1 : 0);
      }
    }

    return bitLength;
  }

  public int bitLength() {
    if (nBitLength == -1) {
      if (signum == 0) {
        nBitLength = 0;
      }
      else {
        nBitLength = bitLength(0, mag);
      }
    }

    return nBitLength;
  }

  //
  // bitLen(val) is the number of bits in val.
  //
  static int bitLen(int w) {
    // Binary search - decision tree (5 tests, rarely 6)
    return
        (w < 1 << 15 ?
         (w < 1 << 7 ?
          (w < 1 << 3 ?
           (w < 1 << 1 ? (w < 1 << 0 ? (w < 0 ? 32 : 0) : 1) :
            (w < 1 << 2 ? 2 : 3)) :
           (w < 1 << 5 ? (w < 1 << 4 ? 4 : 5) : (w < 1 << 6 ? 6 : 7))) :
          (w < 1 << 11 ?
           (w < 1 << 9 ? (w < 1 << 8 ? 8 : 9) : (w < 1 << 10 ? 10 : 11)) :
           (w < 1 << 13 ? (w < 1 << 12 ? 12 : 13) : (w < 1 << 14 ? 14 : 15)))) :
         (w < 1 << 23 ?
          (w < 1 << 19 ?
           (w < 1 << 17 ? (w < 1 << 16 ? 16 : 17) : (w < 1 << 18 ? 18 : 19)) :
           (w < 1 << 21 ? (w < 1 << 20 ? 20 : 21) : (w < 1 << 22 ? 22 : 23))) :
          (w < 1 << 27 ?
           (w < 1 << 25 ? (w < 1 << 24 ? 24 : 25) : (w < 1 << 26 ? 26 : 27)) :
           (w < 1 << 29 ? (w < 1 << 28 ? 28 : 29) : (w < 1 << 30 ? 30 : 31)))));
  }

  private final static byte bitLengths[] = {
      0, 1, 2, 2, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4, 4, 4,
      5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5,
      6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6,
      6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6,
      7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7,
      7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7,
      7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7,
      7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7,
      8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8,
      8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8,
      8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8,
      8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8,
      8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8,
      8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8,
      8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8,
      8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8};

  public int compareTo(Object o) {
    return compareTo( (BigInteger) o);
  }

  /**
   * unsigned comparison on two arrays - note the arrays may
   * start with leading zeros.
   */
  private int compareTo(
      int xIndx,
      int[] x,
      int yIndx,
      int[] y) {
    while (xIndx != x.length && x[xIndx] == 0) {
      xIndx++;
    }

    while (yIndx != y.length && y[yIndx] == 0) {
      yIndx++;
    }

    if ( (x.length - xIndx) < (y.length - yIndx)) {
      return -1;
    }

    if ( (x.length - xIndx) > (y.length - yIndx)) {
      return 1;
    }

    // lengths of magnitudes the same, test the magnitude values

    while (xIndx < x.length) {
      long v1 = (long) (x[xIndx++]) & IMASK;
      long v2 = (long) (y[yIndx++]) & IMASK;
      if (v1 < v2) {
        return -1;
      }
      if (v1 > v2) {
        return 1;
      }
    }

    return 0;
  }

  public int compareTo(
      BigInteger val) {
    if (signum < val.signum) {
      return -1;
    }
    if (signum > val.signum) {
      return 1;
    }

    return compareTo(0, mag, 0, val.mag);
  }

  /**
   * return z = x / y - done in place (z value preserved, x contains the
   * remainder)
   */
  private int[] divide(
      int[] x,
      int[] y) {
    int xyCmp = compareTo(0, x, 0, y);
    int[] count;

    if (xyCmp > 0) {
      int[] c;

      int shift = bitLength(0, x) - bitLength(0, y);

      if (shift > 1) {
        c = shiftLeft(y, shift - 1);
        count = shiftLeft(ONE.mag, shift - 1);
      }
      else {
        c = new int[x.length];
        count = new int[1];

        System.arraycopy(y, 0, c, c.length - y.length, y.length);
        count[0] = 1;
      }

      int[] iCount = new int[count.length];

      subtract(0, x, 0, c);
      System.arraycopy(count, 0, iCount, 0, count.length);

      int xStart = 0;
      int cStart = 0;
      int iCountStart = 0;

      for (; ; ) {
        int cmp = compareTo(xStart, x, cStart, c);

        while (cmp >= 0) {
          subtract(xStart, x, cStart, c);
          add(count, iCount);
          cmp = compareTo(xStart, x, cStart, c);
        }

        xyCmp = compareTo(xStart, x, 0, y);

        if (xyCmp > 0) {
          if (x[xStart] == 0) {
            xStart++;
          }

          shift = bitLength(cStart, c) - bitLength(xStart, x);

          if (shift == 0) {
            c = shiftRightOne(cStart, c);
            iCount = shiftRightOne(iCountStart, iCount);
          }
          else {
            c = shiftRight(cStart, c, shift);
            iCount = shiftRight(iCountStart, iCount, shift);
          }

          if (c[cStart] == 0) {
            cStart++;
          }

          if (iCount[iCountStart] == 0) {
            iCountStart++;
          }
        }
        else if (xyCmp == 0) {
          add(count, ONE.mag);
          for (int i = xStart; i != x.length; i++) {
            x[i] = 0;
          }
          break;
        }
        else {
          break;
        }
      }
    }
    else if (xyCmp == 0) {
      count = new int[1];

      count[0] = 1;
    }
    else {
      count = new int[1];

      count[0] = 0;
    }

    return count;
  }

  public BigInteger divide(
      BigInteger val) throws ArithmeticException {
    if (val.signum == 0) {
      throw new ArithmeticException("Divide by zero");
    }

    if (signum == 0) {
      return BigInteger.ZERO;
    }

    if (val.compareTo(BigInteger.ONE) == 0) {
      return this;
    }

    int[] mag = new int[this.mag.length];
    System.arraycopy(this.mag, 0, mag, 0, mag.length);

    return new BigInteger(this.signum * val.signum, divide(mag, val.mag));
  }

  public BigInteger[] divideAndRemainder(
      BigInteger val) throws ArithmeticException {
    if (val.signum == 0) {
      throw new ArithmeticException("Divide by zero");
    }

    BigInteger biggies[] = new BigInteger[2];

    if (signum == 0) {
      biggies[0] = biggies[1] = BigInteger.ZERO;

      return biggies;
    }

    if (val.compareTo(BigInteger.ONE) == 0) {
      biggies[0] = this;
      biggies[1] = BigInteger.ZERO;

      return biggies;
    }

    int[] remainder = new int[this.mag.length];
    System.arraycopy(this.mag, 0, remainder, 0, remainder.length);

    int[] quotient = divide(remainder, val.mag);

    biggies[0] = new BigInteger(this.signum * val.signum, quotient);
    biggies[1] = new BigInteger(this.signum, remainder);

    return biggies;
  }

  public boolean equals(
      Object val) {
    if (val == this) {
      return true;
    }

    if (! (val instanceof BigInteger)) {
      return false;
    }
    BigInteger biggie = (BigInteger) val;

    if (biggie.signum != signum || biggie.mag.length != mag.length) {
      return false;
    }

    for (int i = 0; i < mag.length; i++) {
      if (biggie.mag[i] != mag[i]) {
        return false;
      }
    }

    return true;
  }

  public BigInteger gcd(
      BigInteger val) {
    if (val.signum == 0) {
      return this.abs();
    }
    else if (signum == 0) {
      return val.abs();
    }

    BigInteger r;
    BigInteger u = this;
    BigInteger v = val;

    while (v.signum != 0) {
      r = u.mod(v);
      u = v;
      v = r;
    }

    return u;
  }

  public int hashCode() {
    return 0;
  }

  public int intValue() {
    if (mag.length == 0) {
      return 0;
    }

    if (signum < 0) {
      return -mag[mag.length - 1];
    }
    else {
      return mag[mag.length - 1];
    }
  }

  /**
   * return whether or not a BigInteger is probably prime with a
   * probability of 1 - (1/2)**certainty.
   * <p>
   * From Knuth Vol 2, pg 395.
   */
  public boolean isProbablePrime(
      int certainty) {
    if (certainty == 0) {
      return true;
    }

    BigInteger n = this.abs();

    if (n.equals(TWO)) {
      return true;
    }

    if (n.equals(ONE) || !n.testBit(0)) {
      return false;
    }

    if ( (certainty & 0x1) == 1) {
      certainty = certainty / 2 + 1;
    }
    else {
      certainty /= 2;
    }

    //
    // let n = 1 + 2^kq
    //
    BigInteger q = n.subtract(ONE);
    int k = q.getLowestSetBit();

    q = q.shiftRight(k);

    Random rnd = new Random();
    for (int i = 0; i <= certainty; i++) {
      BigInteger x;

      do {
        x = new BigInteger(n.bitLength(), rnd);
      }
      while (x.compareTo(ONE) <= 0 || x.compareTo(n) >= 0);

      int j = 0;
      BigInteger y = x.modPow(q, n);

      while (! ( (j == 0 && y.equals(ONE)) || y.equals(n.subtract(ONE)))) {
        if (j > 0 && y.equals(ONE)) {
          return false;
        }
        if (++j == k) {
          return false;
        }
        y = y.modPow(TWO, n);
      }
    }

    return true;
  }

  public long longValue() {
    long val = 0;

    if (mag.length == 0) {
      return 0;
    }

    if (mag.length > 1) {
      val = ( (long) mag[mag.length - 2] << 32)
          | (mag[mag.length - 1] & IMASK);
    }
    else {
      val = (mag[mag.length - 1] & IMASK);
    }

    if (signum < 0) {
      return -val;
    }
    else {
      return val;
    }
  }

  public BigInteger max(
      BigInteger val) {
    return (compareTo(val) > 0) ? this : val;
  }

  public BigInteger min(
      BigInteger val) {
    return (compareTo(val) < 0) ? this : val;
  }

  public BigInteger mod(
      BigInteger m) throws ArithmeticException {
    if (m.signum <= 0) {
      throw new ArithmeticException("BigInteger: modulus is not positive");
    }

    BigInteger biggie = this.remainder(m);

    return (biggie.signum >= 0 ? biggie : biggie.add(m));
  }

  public BigInteger modInverse(
      BigInteger m) throws ArithmeticException {
    if (m.signum != 1) {
      throw new ArithmeticException("Modulus must be positive");
    }

    BigInteger x = new BigInteger();
    BigInteger y = new BigInteger();

    BigInteger gcd = BigInteger.extEuclid(this, m, x, y);

    if (!gcd.equals(BigInteger.ONE)) {
      throw new ArithmeticException("Numbers not relatively prime.");
    }

    if (x.compareTo(BigInteger.ZERO) < 0) {
      x = x.add(m);
    }

    return x;
  }

  /**
   * Calculate the numbers u1, u2, and u3 such that:
   *
   * u1 * a + u2 * b = u3
   *
   * where u3 is the greatest common divider of a and b.
   * a and b using the extended Euclid algorithm (refer p. 323
   * of The Art of Computer Programming vol 2, 2nd ed).
   * This also seems to have the side effect of calculating
   * some form of multiplicative inverse.
   *
   * @param a    First number to calculate gcd for
   * @param b    Second number to calculate gcd for
   * @param u1Out      the return object for the u1 value
   * @param u2Out      the return object for the u2 value
   * @return The greatest common divisor of a and b
   */
  private static BigInteger extEuclid(
      BigInteger a,
      BigInteger b,
      BigInteger u1Out,
      BigInteger u2Out) {
    BigInteger res;

    BigInteger u1 = BigInteger.ONE;
    BigInteger u3 = a;
    BigInteger v1 = BigInteger.ZERO;
    BigInteger v3 = b;

    while (v3.compareTo(BigInteger.ZERO) > 0) {
      BigInteger q, tn, tv;

      q = u3.divide(v3);

      tn = u1.subtract(v1.multiply(q));
      u1 = v1;
      v1 = tn;

      tn = u3.subtract(v3.multiply(q));
      u3 = v3;
      v3 = tn;
    }

    u1Out.signum = u1.signum;
    u1Out.mag = u1.mag;

    res = u3.subtract(u1.multiply(a)).divide(b);
    u2Out.signum = res.signum;
    u2Out.mag = res.mag;

    return u3;
  }

  /**
   * zero out the array x
   */
  private void zero(
      int[] x) {
    for (int i = 0; i != x.length; i++) {
      x[i] = 0;
    }
  }

  public BigInteger modPow(
      BigInteger exponent,
      BigInteger m) throws ArithmeticException {
    int[] zVal = null;
    int[] yAccum = null;
    int[] yVal;

    // Montgomery exponentiation is only possible if the modulus is odd,
    // but AFAIK, this is always the case for crypto algo's
    boolean useMonty = ( (m.mag[m.mag.length - 1] & 1) == 1);
    long mQ = 0;
    if (useMonty) {
      mQ = m.getMQuote();

      // tmp = this * R mod m
      BigInteger tmp = this.shiftLeft(32 * m.mag.length).mod(m);
      zVal = tmp.mag;

      useMonty = (zVal.length == m.mag.length);

      if (useMonty) {
        yAccum = new int[m.mag.length + 1];
      }
    }

    if (!useMonty) {
      if (mag.length <= m.mag.length) {
        //zAccum = new int[m.magnitude.length * 2];
        zVal = new int[m.mag.length];

        System.arraycopy(mag, 0, zVal,
                         zVal.length - mag.length, mag.length);
      }
      else {
        //
        // in normal practice we'll never see this...
        //
        BigInteger tmp = this.remainder(m);

        //zAccum = new int[m.magnitude.length * 2];
        zVal = new int[m.mag.length];

        System.arraycopy(tmp.mag, 0, zVal,
                         zVal.length - tmp.mag.length, tmp.mag.length);
      }

      yAccum = new int[m.mag.length * 2];
    }

    yVal = new int[m.mag.length];

    //
    // from LSW to MSW
    //
    for (int i = 0; i < exponent.mag.length; i++) {
      int v = exponent.mag[i];
      int bits = 0;

      if (i == 0) {
        while (v > 0) {
          v <<= 1;
          bits++;
        }

        //
        // first time in initialise y
        //
        System.arraycopy(zVal, 0, yVal, 0, zVal.length);

        v <<= 1;
        bits++;
      }

      while (v != 0) {
        if (useMonty) {
          // Montgomery square algo doesn't exist, and a normal
          // square followed by a Montgomery reduction proved to
          // be almost as heavy as a Montgomery mulitply.
          multiplyMonty(yAccum, yVal, yVal, m.mag, mQ);
        }
        else {
          square(yAccum, yVal);
          remainder(yAccum, m.mag);
          System.arraycopy(yAccum, yAccum.length - yVal.length,
                           yVal, 0, yVal.length);
          zero(yAccum);
        }
        bits++;

        if (v < 0) {
          if (useMonty) {
            multiplyMonty(yAccum, yVal, zVal, m.mag, mQ);
          }
          else {
            multiply(yAccum, yVal, zVal);
            remainder(yAccum, m.mag);
            System.arraycopy(yAccum, yAccum.length - yVal.length,
                             yVal, 0, yVal.length);
            zero(yAccum);
          }
        }

        v <<= 1;
      }

      while (bits < 32) {
        if (useMonty) {
          multiplyMonty(yAccum, yVal, yVal, m.mag, mQ);
        }
        else {
          square(yAccum, yVal);
          remainder(yAccum, m.mag);
          System.arraycopy(yAccum, yAccum.length - yVal.length,
                           yVal, 0, yVal.length);
          zero(yAccum);
        }
        bits++;
      }
    }

    if (useMonty) {
      // Return y * R^(-1) mod m by doing y * 1 * R^(-1) mod m
      zero(zVal);
      zVal[zVal.length - 1] = 1;
      multiplyMonty(yAccum, yVal, zVal, m.mag, mQ);
    }

    return new BigInteger(1, yVal);
  }

  /**
   * return w with w = x * x - w is assumed to have enough space.
   */
  private int[] square(
      int[] w,
      int[] x) {
    long u1, u2, c;

    if (w.length != 2 * x.length) {
      throw new IllegalArgumentException("no I don't think so...");
    }

    for (int i = x.length - 1; i != 0; i--) {
      long v = (x[i] & IMASK);

      u1 = v * v;
      u2 = u1 >>> 32;
      u1 = u1 & IMASK;

      u1 += (w[2 * i + 1] & IMASK);

      w[2 * i + 1] = (int) u1;
      c = u2 + (u1 >> 32);

      for (int j = i - 1; j >= 0; j--) {
        u1 = (x[j] & IMASK) * v;
        u2 = u1 >>> 31; // multiply by 2!
        u1 = (u1 & 0x7fffffff) << 1; // multiply by 2!
        u1 += (w[i + j + 1] & IMASK) + c;

        w[i + j + 1] = (int) u1;
        c = u2 + (u1 >>> 32);
      }
      c += w[i] & IMASK;
      w[i] = (int) c;
      w[i - 1] = (int) (c >> 32);
    }

    u1 = (x[0] & IMASK);
    u1 = u1 * u1;
    u2 = u1 >>> 32;
    u1 = u1 & IMASK;

    u1 += (w[1] & IMASK);

    w[1] = (int) u1;
    w[0] = (int) (u2 + (u1 >> 32) + w[0]);

    return w;
  }

  /**
   * return x with x = y * z - x is assumed to have enough space.
   */
  private int[] multiply(
      int[] x,
      int[] y,
      int[] z) {
    for (int i = z.length - 1; i >= 0; i--) {
      long a = z[i] & IMASK;
      long value = 0;

      for (int j = y.length - 1; j >= 0; j--) {
        value += a * (y[j] & IMASK) + (x[i + j + 1] & IMASK);

        x[i + j + 1] = (int) value;

        value >>>= 32;
      }

      x[i] = (int) value;
    }

    return x;
  }

  /**
   * Calculate mQuote = -m^(-1) mod b with b = 2^32 (32 = word size)
   */
  private long getMQuote() {
    if (mQuote != -1L) { // allready calculated
      return mQuote;
    }
    if ( (mag[mag.length - 1] & 1) == 0) {
      return -1L; // not for even numbers
    }

    byte[] bytes = {
        1, 0, 0, 0, 0};
    BigInteger b = new BigInteger(1, bytes); // 2^32
    mQuote = this.negate().mod(b).modInverse(b).longValue();
    return mQuote;
  }

  /**
   * Montgomery multiplication: a = x * y * R^(-1) mod m
   * <br>
   * Based algorithm 14.36 of Handbook of Applied Cryptography.
   * <br>
   * <li> m, x, y should have length n </li>
   * <li> a should have length (n + 1) </li>
   * <li> b = 2^32, R = b^n </li>
   * <br>
   * The result is put in x
   * <br>
   * NOTE: the indices of x, y, m, a different in HAC and in Java
   */
  public void multiplyMonty(
      int[] a,
      int[] x,
      int[] y,
      int[] m,
      long mQuote) { // mQuote = -m^(-1) mod b
    int n = m.length;
    int nMinus1 = n - 1;
    long y_0 = y[n - 1] & IMASK;

    // 1. a = 0 (Notation: a = (a_{n} a_{n-1} ... a_{0})_{b} )
    for (int i = 0; i <= n; i++) {
      a[i] = 0;
    }

    // 2. for i from 0 to (n - 1) do the following:
    for (int i = n; i > 0; i--) {

      long x_i = x[i - 1] & IMASK;

      // 2.1 u = ((a[0] + (x[i] * y[0]) * mQuote) mod b
      long u = ( ( ( (a[n] & IMASK) + ( (x_i * y_0) & IMASK)) & IMASK) *
                mQuote) & IMASK;

      // 2.2 a = (a + x_i * y + u * m) / b
      long prod1 = x_i * y_0;
      long prod2 = u * (m[n - 1] & IMASK);
      long tmp = (a[n] & IMASK) + (prod1 & IMASK) + (prod2 & IMASK);
      long carry = (prod1 >>> 32) + (prod2 >>> 32) + (tmp >>> 32);
      for (int j = nMinus1; j > 0; j--) {
        prod1 = x_i * (y[j - 1] & IMASK);
        prod2 = u * (m[j - 1] & IMASK);
        tmp = (a[j] & IMASK) + (prod1 & IMASK) +
            (prod2 & IMASK) + (carry & IMASK);
        carry = (carry >>> 32) + (prod1 >>> 32) +
            (prod2 >>> 32) + (tmp >>> 32);
        a[j + 1] = (int) tmp; // division by b
      }
      carry += (a[0] & IMASK);
      a[1] = (int) carry;
      a[0] = (int) (carry >>> 32);
    }

    // 3. if x >= m the x = x - m
    if (compareTo(0, a, 0, m) >= 0) {
      subtract(0, a, 0, m);
    }

    // put the result in x
    for (int i = 0; i < n; i++) {
      x[i] = a[i + 1];
    }
  }

  public BigInteger multiply(
      BigInteger val) {
    if (signum == 0 || val.signum == 0) {
      return BigInteger.ZERO;
    }

    int[] res = new int[mag.length + val.mag.length];

    return new BigInteger(signum * val.signum, multiply(res, mag, val.mag));
  }

  public BigInteger negate() {
    return new BigInteger( -signum, mag);
  }

  public BigInteger pow(
      int exp) throws ArithmeticException {
    if (exp < 0) {
      throw new ArithmeticException("Negative exponent");
    }
    if (signum == 0) {
      return (exp == 0 ? BigInteger.ONE : this);
    }

    BigInteger y, z;
    y = BigInteger.ONE;
    z = this;

    while (exp != 0) {
      if ( (exp & 0x1) == 1) {
        y = y.multiply(z);
      }
      exp >>= 1;
      if (exp != 0) {
        z = z.multiply(z);
      }
    }

    return y;
  }

  /**
   * return x = x % y - done in place (y value preserved)
   */
  private int[] remainder(
      int[] x,
      int[] y) {
    int xyCmp = compareTo(0, x, 0, y);

    if (xyCmp > 0) {
      int[] c;
      int shift = bitLength(0, x) - bitLength(0, y);

      if (shift > 1) {
        c = shiftLeft(y, shift - 1);
      }
      else {
        c = new int[x.length];

        System.arraycopy(y, 0, c, c.length - y.length, y.length);
      }

      subtract(0, x, 0, c);

      int xStart = 0;
      int cStart = 0;

      for (; ; ) {
        int cmp = compareTo(xStart, x, cStart, c);

        while (cmp >= 0) {
          subtract(xStart, x, cStart, c);
          cmp = compareTo(xStart, x, cStart, c);
        }

        xyCmp = compareTo(xStart, x, 0, y);

        if (xyCmp > 0) {
          if (x[xStart] == 0) {
            xStart++;
          }

          shift = bitLength(cStart, c) - bitLength(xStart, x);

          if (shift == 0) {
            c = shiftRightOne(cStart, c);
          }
          else {
            c = shiftRight(cStart, c, shift);
          }

          if (c[cStart] == 0) {
            cStart++;
          }
        }
        else if (xyCmp == 0) {
          for (int i = xStart; i != x.length; i++) {
            x[i] = 0;
          }
          break;
        }
        else {
          break;
        }
      }
    }
    else if (xyCmp == 0) {
      for (int i = 0; i != x.length; i++) {
        x[i] = 0;
      }
    }

    return x;
  }

  /**
   * Returns a BigInteger whose value is <tt>(this | val)</tt>.  (This method
   * returns a negative BigInteger if and only if either this or val is
   * negative.)
   *
   * @param val value to be OR'ed with this BigInteger.
   * @return <tt>this | val</tt>
   */
  public BigInteger or(BigInteger val) {
    int[] result = new int[Math.max(intLength(), val.intLength())];
    for (int i = 0; i < result.length; i++) {
      result[i] = (int) (getInt(result.length - i - 1)
                         | val.getInt(result.length - i - 1));

    }
    return valueOf(result);
  }

  private static int[] makePositive(int a[]) {
    int keep, j;

    // Find first non-sign (0xffffffff) int of input
    for (keep = 0; keep < a.length && a[keep] == -1; keep++) {
      ;
    }

    /* Allocate output array.  If all non-sign ints are 0x00, we must
     * allocate space for one extra output int. */
    for (j = keep; j < a.length && a[j] == 0; j++) {
      ;
    }
    int extraInt = (j == a.length ? 1 : 0);
    int result[] = new int[a.length - keep + extraInt];

    /* Copy one's complement of input into into output, leaving extra
     * int (if it exists) == 0x00 */
    for (int i = keep; i < a.length; i++) {
      result[i - keep + extraInt] = ~a[i];

      // Add one to one's complement to generate two's complement
    }
    for (int i = result.length - 1; ++result[i] == 0; i--) {
      ;
    }

    return result;
  }

  private static int[] makePositive(byte a[]) {
    int keep, k;
    int byteLength = a.length;

    // Find first non-sign (0xff) byte of input
    for (keep = 0; keep < byteLength && a[keep] == -1; keep++) {
      ;
    }

    /* Allocate output array.  If all non-sign bytes are 0x00, we must
     * allocate space for one extra output byte. */
    for (k = keep; k < byteLength && a[k] == 0; k++) {
      ;
    }

    int extraByte = (k == byteLength) ? 1 : 0;
    int intLength = ( (byteLength - keep + extraByte) + 3) / 4;
    int result[] = new int[intLength];

    /* Copy one's complement of input into into output, leaving extra
     * byte (if it exists) == 0x00 */
    int b = byteLength - 1;
    for (int i = intLength - 1; i >= 0; i--) {
      result[i] = a[b--] & 0xff;
      int numBytesToTransfer = Math.min(3, b - keep + 1);
      if (numBytesToTransfer < 0) {
        numBytesToTransfer = 0;
      }
      for (int j = 8; j <= 8 * numBytesToTransfer; j += 8) {
        result[i] |= ( (a[b--] & 0xff) << j);

        // Mask indicates which bits must be complemented
      }
      int mask = -1 >>> (8 * (3 - numBytesToTransfer));
      result[i] = ~result[i] & mask;
    }

    // Add one to one's complement to generate two's complement
    for (int i = result.length - 1; i >= 0; i--) {
      result[i] = (int) ( (result[i] & 0xffffffffL) + 1);
      if (result[i] != 0) {
        break;
      }
    }

    return result;
  }

  private BigInteger(int[] val) {
    if (val.length == 0) {
      throw new NumberFormatException("Zero length BigInteger");
    }

    if (val[0] < 0) {
      mag = makePositive(val);
      signum = -1;
    }
    else {
      mag = trustedStripLeadingZeroInts(val);
      signum = (mag.length == 0 ? 0 : 1);
    }
  }

  private static int[] trustedStripLeadingZeroInts(int val[]) {
    int byteLength = val.length;
    int keep;

    // Find first nonzero byte
    for (keep = 0; keep < val.length && val[keep] == 0; keep++) {
      ;
    }

    // Only perform copy if necessary
    if (keep > 0) {
      int result[] = new int[val.length - keep];
      for (int i = 0; i < val.length - keep; i++) {
        result[i] = val[keep + i];
      }
      return result;
    }
    return val;
  }

  private int signInt() {
    return (int) (signum < 0 ? -1 : 0);
  }

  private static BigInteger valueOf(int val[]) {
    return (val[0] > 0 ? new BigInteger(val, 1) : new BigInteger(val));
  }

  private BigInteger(int[] magnitude, int signum) {
    this.signum = (magnitude.length == 0 ? 0 : signum);
    this.mag = magnitude;
  }

  private int firstNonzeroIntNum() {
    /*
     * Initialize firstNonzeroIntNum field the first time this method is
     * executed. This method depends on the atomicity of int modifies;
     * without this guarantee, it would have to be synchronized.
     */
    if (firstNonzeroIntNum == -2) {
      // Search for the first nonzero int
      int i;
      for (i = mag.length - 1; i >= 0 && mag[i] == 0; i--) {
        ;
      }
      firstNonzeroIntNum = mag.length - i - 1;
    }
    return firstNonzeroIntNum;
  }

  private int intLength() {
    return bitLength() / 32 + 1;
  }

  private int getInt(int n) {
    if (n < 0) {
      return 0;
    }
    if (n >= mag.length) {
      return signInt();
    }

    int magInt = mag[mag.length - n - 1];

    return (int) (signum >= 0 ? magInt :
                  (n <= firstNonzeroIntNum() ? -magInt : ~magInt));
  }

  public BigInteger remainder(
      BigInteger val) throws ArithmeticException {
    if (val.signum == 0) {
      throw new ArithmeticException("BigInteger: Divide by zero");
    }

    if (signum == 0) {
      return BigInteger.ZERO;
    }

    int[] res = new int[this.mag.length];

    System.arraycopy(this.mag, 0, res, 0, res.length);

    return new BigInteger(signum, remainder(res, val.mag));
  }

  /**
   * do a left shift - this returns a new array.
   */
  private int[] shiftLeft(
      int[] mag,
      int n) {
    int nInts = n >>> 5;
    int nBits = n & 0x1f;
    int magLen = mag.length;
    int newMag[] = null;

    if (nBits == 0) {
      newMag = new int[magLen + nInts];
      for (int i = 0; i < magLen; i++) {
        newMag[i] = mag[i];
      }
    }
    else {
      int i = 0;
      int nBits2 = 32 - nBits;
      int highBits = mag[0] >>> nBits2;

      if (highBits != 0) {
        newMag = new int[magLen + nInts + 1];
        newMag[i++] = highBits;
      }
      else {
        newMag = new int[magLen + nInts];
      }

      int m = mag[0];
      for (int j = 0; j < magLen - 1; j++) {
        int next = mag[j + 1];

        newMag[i++] = (m << nBits) | (next >>> nBits2);
        m = next;
      }

      newMag[i] = mag[magLen - 1] << nBits;
    }

    return newMag;
  }

  public BigInteger shiftLeft(
      int n) {
    if (signum == 0 || mag.length == 0) {
      return ZERO;
    }
    if (n == 0) {
      return this;
    }

    if (n < 0) {
      return shiftRight( -n);
    }

    return new BigInteger(signum, shiftLeft(mag, n));
  }

  /**
   * do a right shift - this does it in place.
   */
  private int[] shiftRight(
      int start,
      int[] mag,
      int n) {
    int nInts = (n >>> 5) + start;
    int nBits = n & 0x1f;
    int magLen = mag.length;

    if (nInts != start) {
      int delta = (nInts - start);

      for (int i = magLen - 1; i >= nInts; i--) {
        mag[i] = mag[i - delta];
      }
      for (int i = nInts - 1; i >= start; i--) {
        mag[i] = 0;
      }
    }

    if (nBits != 0) {
      int nBits2 = 32 - nBits;
      int m = mag[magLen - 1];

      for (int i = magLen - 1; i >= nInts + 1; i--) {
        int next = mag[i - 1];

        mag[i] = (m >>> nBits) | (next << nBits2);
        m = next;
      }

      mag[nInts] >>>= nBits;
    }

    return mag;
  }

  /**
   * do a right shift by one - this does it in place.
   */
  private int[] shiftRightOne(
      int start,
      int[] mag) {
    int magLen = mag.length;

    int m = mag[magLen - 1];

    for (int i = magLen - 1; i >= start + 1; i--) {
      int next = mag[i - 1];

      mag[i] = (m >>> 1) | (next << 31);
      m = next;
    }

    mag[start] >>>= 1;

    return mag;
  }

  public BigInteger shiftRight(
      int n) {
    if (n == 0) {
      return this;
    }

    if (n < 0) {
      return shiftLeft( -n);
    }

    if (n >= bitLength()) {
      return (this.signum < 0 ? valueOf( -1) : BigInteger.ZERO);
    }

    int[] res = new int[this.mag.length];

    System.arraycopy(this.mag, 0, res, 0, res.length);

    return new BigInteger(this.signum, shiftRight(0, res, n));
  }

  public int signum() {
    return signum;
  }

  /**
   * returns x = x - y - we assume x is >= y
   */
  private int[] subtract(
      int xStart,
      int[] x,
      int yStart,
      int[] y) {
    int iT = x.length - 1;
    int iV = y.length - 1;
    long m;
    int borrow = 0;

    do {
      m = ( ( (long) x[iT]) & IMASK) - ( ( (long) y[iV--]) & IMASK) + borrow;

      x[iT--] = (int) m;

      if (m < 0) {
        borrow = -1;
      }
      else {
        borrow = 0;
      }
    }
    while (iV >= yStart);

    while (iT >= xStart) {
      m = ( ( (long) x[iT]) & IMASK) + borrow;
      x[iT--] = (int) m;

      if (m < 0) {
        borrow = -1;
      }
      else {
        break;
      }
    }

    return x;
  }

  public BigInteger subtract(
      BigInteger val) {
    if (val.signum == 0 || val.mag.length == 0) {
      return this;
    }
    if (signum == 0 || mag.length == 0) {
      return val.negate();
    }
    if (val.signum < 0) {
      if (this.signum > 0) {
        return this.add(val.negate());
      }
    }
    else {
      if (this.signum < 0) {
        return this.add(val.negate());
      }
    }

    BigInteger bigun, littlun;
    int compare = compareTo(val);
    if (compare == 0) {
      return ZERO;
    }

    if (compare < 0) {
      bigun = val;
      littlun = this;
    }
    else {
      bigun = this;
      littlun = val;
    }

    int res[] = new int[bigun.mag.length];

    System.arraycopy(bigun.mag, 0, res, 0, res.length);

    return new BigInteger(this.signum * compare,
                          subtract(0, res, 0, littlun.mag));
  }

  public byte[] toByteArray() {
    int bitLength = bitLength();
    byte[] bytes = new byte[bitLength / 8 + 1];

    int bytesCopied = 4;
    int mag = 0;
    int ofs = this.mag.length - 1;
    int carry = 1;
    long lMag;
    for (int i = bytes.length - 1; i >= 0; i--) {
      if (bytesCopied == 4 && ofs >= 0) {
        if (signum < 0) {
          // we are dealing with a +ve number and we want a -ve one, so
          // invert the magnitude ints and add 1 (propagating the carry)
          // to make a 2's complement -ve number
          lMag = ~this.mag[ofs--] & IMASK;
          lMag += carry;
          if ( (lMag & ~IMASK) != 0) {
            carry = 1;
          }
          else {
            carry = 0;
          }
          mag = (int) (lMag & IMASK);
        }
        else {
          mag = this.mag[ofs--];
        }
        bytesCopied = 1;
      }
      else {
        mag >>>= 8;
        bytesCopied++;
      }

      bytes[i] = (byte) mag;
    }

    return bytes;
  }

  public String toString() {
    return toString(10);
  }

  public String toString(int rdx) {
    if (mag == null) {
      return "null";
    }
    else if (signum == 0) {
      return "0";
    }

    String s = new String();
    String h;

    if (rdx == 16) {
      for (int i = 0; i < mag.length; i++) {
        h = "0000000" + Integer.toHexString(mag[i]);
        h = h.substring(h.length() - 8);
        s = s + h;
      }
    }
    else {
      // This is algorithm 1a from chapter 4.4 in Seminumerical Algorithms, slow but it works
      Stack S = new Stack();
      BigInteger base = new BigInteger(Integer.toString(rdx, rdx), rdx);
      // The sign is handled separatly.
      // Notice however that for this to work, radix 16 _MUST_ be a special case,
      // unless we want to enter a recursion well. In their infinite wisdom, why did not
      // the Sun engineers made a c'tor for BigIntegers taking a BigInteger as parameter?
      // (Answer: Becuase Sun's BigIntger is clonable, something bouncycastle's isn't.)
      BigInteger u = new BigInteger(this.abs().toString(16), 16);
      BigInteger b;

      // For speed, maye these test should look directly a u.magnitude.length?
      while (!u.equals(BigInteger.ZERO)) {
        b = u.mod(base);
        if (b.equals(BigInteger.ZERO)) {
          S.push("0");
        }
        else {
          S.push(Integer.toString(b.mag[0], rdx));
        }
        u = u.divide(base);
      }
      // Then pop the stack
      while (!S.empty()) {
        s = s + S.pop();
      }
    }
    // Strip leading zeros.
    while (s.length() > 1 && s.charAt(0) == '0') {
      s = s.substring(1);

    }
    if (s.length() == 0) {
      s = "0";
    }
    else if (signum == -1) {
      s = "-" + s;

    }
    return s;
  }

  public static final BigInteger ZERO = new BigInteger(0, new byte[0]);
  public static final BigInteger ONE = valueOf(1);
  private static final BigInteger TWO = valueOf(2);

  public static BigInteger valueOf(
      long val) {
    if (val == 0) {
      return BigInteger.ZERO;
    }

    // store val into a byte array
    byte[] b = new byte[8];
    for (int i = 0; i < 8; i++) {
      b[7 - i] = (byte) val;
      val >>= 8;
    }

    return new BigInteger(b);
  }

  private int max(int a, int b) {
    if (a < b) {
      return b;
    }
    return a;
  }

  public int getLowestSetBit() {
    if (this.equals(ZERO)) {
      return -1;
    }

    int w = mag.length - 1;

    while (w >= 0) {
      if (mag[w] != 0) {
        break;
      }

      w--;
    }

    int b = 31;

    while (b > 0) {
      if ( (mag[w] << b) == 0x80000000) {
        break;
      }

      b--;
    }

    return ( ( (mag.length - 1) - w) * 32 + (31 - b));
  }

  public boolean testBit(
      int n) throws ArithmeticException {
    if (n < 0) {
      throw new ArithmeticException("Bit position must not be negative");
    }

    if ( (n / 32) >= mag.length) {
      return signum < 0;
    }

    return ( (mag[ (mag.length - 1) - n / 32] >> (n % 32)) & 1) > 0;
  }
}
