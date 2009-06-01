
				/*
 *  OpenVPNALS
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
			
package com.maverick.crypto.publickey;

import com.maverick.crypto.digests.SHA1Digest;
import java.math.BigInteger;
import com.maverick.crypto.security.SecureRandom;

public final class Dsa {

  public static byte[] sign(BigInteger x,
                            BigInteger p, BigInteger q, BigInteger g,
                            byte[] data) {
	  
    BigInteger hM = new BigInteger(1, data);

    hM = hM.mod(q);

    BigInteger r = g.modPow(x, p).mod(q);
    BigInteger s = x.modInverse(q).multiply(hM.add(x.multiply(r))).mod(q);

    int dataSz = data.length;
    byte[] signature = new byte[dataSz * 2];
    byte[] tmp;

    tmp = unsignedBigIntToBytes(r, dataSz);
    System.arraycopy(tmp, 0, signature, 0, dataSz);

    tmp = unsignedBigIntToBytes(s, dataSz);
    System.arraycopy(tmp, 0, signature, dataSz, dataSz);

    return signature;
  }

  public static boolean verify(BigInteger y,
                               BigInteger p, BigInteger q, BigInteger g,
                               byte[] signature, byte[] data) {
    int dataSz = signature.length / 2;
    byte[] ra = new byte[dataSz];
    byte[] sa = new byte[dataSz];

    System.arraycopy(signature, 0, ra, 0, dataSz);
    System.arraycopy(signature, dataSz, sa, 0, dataSz);

    BigInteger hM = new BigInteger(1, data);
    BigInteger r = new BigInteger(1, ra);
    BigInteger s = new BigInteger(1, sa);

    hM = hM.mod(q);

    BigInteger w = s.modInverse(q);
    BigInteger u1 = hM.multiply(w).mod(q);
    BigInteger u2 = r.multiply(w).mod(q);
    BigInteger v = g.modPow(u1, p).multiply(y.modPow(u2, p)).mod(p).mod(q);

    return (v.compareTo(r) == 0);
  }

  private static byte[] unsignedBigIntToBytes(BigInteger bi, int size) {
    byte[] tmp = bi.toByteArray();
    byte[] tmp2 = null;
    if (tmp.length > size) {
      tmp2 = new byte[size];
      System.arraycopy(tmp, tmp.length - size, tmp2, 0, size);
    }
    else if (tmp.length < size) {
      tmp2 = new byte[size];
      System.arraycopy(tmp, 0, tmp2, size - tmp.length, tmp.length);
    }
    else {
      tmp2 = tmp;
    }
    return tmp2;
  }

  public static BigInteger generatePublicKey(BigInteger g, BigInteger p,
                                             BigInteger x) {
    return g.modPow(x, p);
  }

  public static DsaPrivateKey generateKey(int bits, SecureRandom rnd) {

    BigInteger p, q, g, x, y;
    BigInteger ZERO = BigInteger.valueOf(0);
    DSAParametersGenerator dsaParams = new DSAParametersGenerator();
    dsaParams.init(bits, 80, rnd);

    DSAParameters dsa = dsaParams.generateParameters();

    q = dsa.getQ();
    p = dsa.getP();
    g = dsa.getG();

    do {
      x = new BigInteger(160, rnd);
    }
    while (x.equals(ZERO) || x.compareTo(q) >= 0);

    //
    // calculate the public key.
    //
    y = g.modPow(x, p);

    return new DsaPrivateKey(p, q, g, x);
  }

}

/**
 * generate suitable parameters for DSA, in line with FIPS 186-2.
 */
class DSAParametersGenerator {
  private int size;
  private int certainty;
  private SecureRandom random;

  private static BigInteger ONE = BigInteger.valueOf(1);
  private static BigInteger TWO = BigInteger.valueOf(2);

  /**
   * initialise the key generator.
   *
   * @param size size of the key (range 2^512 -> 2^1024 - 64 bit increments)
   * @param certainty measure of robustness of prime (for FIPS 186-2 compliance this should be at least 80).
   * @param random random byte source.
   */
  public void init(
      int size,
      int certainty,
      SecureRandom random) {
    this.size = size;
    this.certainty = certainty;
    this.random = random;
  }

  /**
   * add value to b, returning the result in a. The a value is treated
   * as a BigInteger of length (a.length * 8) bits. The result is
   * modulo 2^a.length in case of overflow.
   */
  private void add(
      byte[] a,
      byte[] b,
      int value) {
    int x = (b[b.length - 1] & 0xff) + value;

    a[b.length - 1] = (byte) x;
    x >>>= 8;

    for (int i = b.length - 2; i >= 0; i--) {
      x += (b[i] & 0xff);
      a[i] = (byte) x;
      x >>>= 8;
    }
  }

  /**
   * which generates the p and g values from the given parameters,
   * returning the DSAParameters object.
   * <p>
   * Note: can take a while...
   */
  public DSAParameters generateParameters() {
    byte[] seed = new byte[20];
    byte[] part1 = new byte[20];
    byte[] part2 = new byte[20];
    byte[] u = new byte[20];
    SHA1Digest sha1 = new SHA1Digest();
    int n = (size - 1) / 160;
    byte[] w = new byte[size / 8];

    BigInteger q = null, p = null, g = null;
    int counter = 0;
    boolean primesFound = false;

    while (!primesFound) {
      do {
        random.nextBytes(seed);

        sha1.update(seed, 0, seed.length);

        sha1.doFinal(part1, 0);

        System.arraycopy(seed, 0, part2, 0, seed.length);

        add(part2, seed, 1);

        sha1.update(part2, 0, part2.length);

        sha1.doFinal(part2, 0);

        for (int i = 0; i != u.length; i++) {
          u[i] = (byte) (part1[i] ^ part2[i]);
        }

        u[0] |= (byte) 0x80;
        u[19] |= (byte) 0x01;

        q = new BigInteger(1, u);
      }
      while (!q.isProbablePrime(certainty));

      counter = 0;

      int offset = 2;

      while (counter < 4096) {
        for (int k = 0; k < n; k++) {
          add(part1, seed, offset + k);
          sha1.update(part1, 0, part1.length);
          sha1.doFinal(part1, 0);
          System.arraycopy(part1, 0, w, w.length - (k + 1) * part1.length,
                           part1.length);
        }

        add(part1, seed, offset + n);
        sha1.update(part1, 0, part1.length);
        sha1.doFinal(part1, 0);
        System.arraycopy(part1, part1.length - ( (w.length - (n) * part1.length)),
                         w, 0, w.length - n * part1.length);

        w[0] |= (byte) 0x80;

        BigInteger x = new BigInteger(1, w);

        BigInteger c = x.mod(q.multiply(TWO));

        p = x.subtract(c.subtract(ONE));

        if (p.testBit(size - 1)) {
          if (p.isProbablePrime(certainty)) {
            primesFound = true;
            break;
          }
        }

        counter += 1;
        offset += n + 1;
      }
    }

    //
    // calculate the generator g
    //
    BigInteger pMinusOneOverQ = p.subtract(ONE).divide(q);

    for (; ; ) {
      BigInteger h = new BigInteger(size, random);
      if (h.compareTo(ONE) <= 0 || h.compareTo(p.subtract(ONE)) >= 0) {
        continue;
      }

      g = h.modPow(pMinusOneOverQ, p);
      if (g.compareTo(ONE) <= 0) {
        continue;
      }

      break;
    }

    return new DSAParameters(p, q, g, new DSAValidationParameters(seed, counter));
  }
}

class DSAParameters

{
  private BigInteger g;
  private BigInteger q;
  private BigInteger p;
  private DSAValidationParameters validation;

  public DSAParameters(
      BigInteger p,
      BigInteger q,
      BigInteger g) {
    this.g = g;
    this.p = p;
    this.q = q;
  }

  public DSAParameters(
      BigInteger p,
      BigInteger q,
      BigInteger g,
      DSAValidationParameters params) {
    this.g = g;
    this.p = p;
    this.q = q;
    this.validation = params;
  }

  public BigInteger getP() {
    return p;
  }

  public BigInteger getQ() {
    return q;
  }

  public BigInteger getG() {
    return g;
  }

  public DSAValidationParameters getValidationParameters() {
    return validation;
  }

  public boolean equals(
      Object obj) {
    if (! (obj instanceof DSAParameters)) {
      return false;
    }

    DSAParameters pm = (DSAParameters) obj;

    return (pm.getP().equals(p) && pm.getQ().equals(q) && pm.getG().equals(g));
  }
}

class DSAValidationParameters {
  private byte[] seed;
  private int counter;

  public DSAValidationParameters(
      byte[] seed,
      int counter) {
    this.seed = seed;
    this.counter = counter;
  }

  public int getCounter() {
    return counter;
  }

  public byte[] getSeed() {
    return seed;
  }

  public boolean equals(
      Object o) {
    if (o == null || ! (o instanceof DSAValidationParameters)) {
      return false;
    }

    DSAValidationParameters other = (DSAValidationParameters) o;

    if (other.counter != this.counter) {
      return false;
    }

    if (other.seed.length != this.seed.length) {
      return false;
    }

    for (int i = 0; i != other.seed.length; i++) {
      if (other.seed[i] != this.seed[i]) {
        return false;
      }
    }

    return true;
  }
}
